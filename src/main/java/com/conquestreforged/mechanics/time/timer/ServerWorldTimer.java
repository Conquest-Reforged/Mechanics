package com.conquestreforged.mechanics.time.timer;

import com.conquestreforged.mechanics.time.TimeMessage;
import com.conquestreforged.mechanics.time.timer.ticker.TimeTicker;
import com.conquestreforged.mechanics.util.net.Channels;
import com.conquestreforged.mechanics.util.Loggable;
import net.minecraft.world.IWorld;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.EnumMap;
import java.util.Map;

public class ServerWorldTimer extends WorldTimer implements Loggable {

    private static final Marker MARKER = MarkerManager.getMarker("ServerWorldTimer");

    private final Map<Period, TimeTicker> modifiers = new EnumMap<>(Period.class);

    private long lastUpdate = 0L;
    private TimeTicker ticker = TimeTicker.NONE;
    private Period currentPeriod = Period.MORNING;

    public void add(Period period, TimeTicker timeTicker) {
        modifiers.put(period, timeTicker);
    }

    @Override
    public Marker getMarker() {
        return MARKER;
    }

    @Override
    public void tick(IWorld world) {
        long now = System.currentTimeMillis();
        long ticks = world.getWorldInfo().getDayTime();
        Period period = Period.getPeriod(ticks);
        if (period != currentPeriod) {
            lastUpdate = now;
            currentPeriod = period;
            ticker = modifiers.getOrDefault(period, TimeTicker.NONE);
            updateRate(world, ticker.getIncrement(world));
        } else if (now - lastUpdate > 1000) {
            lastUpdate = now;
            updateRate(world, ticker.getIncrement(world));
        }
        super.tick(world);
    }

    public void updateRate(IWorld world, float rate) {
        if (rate != getRate()) {
            super.setRate(rate);
            Channels.TIME.send(world, new TimeMessage(getRate()));
            trace("Sending time packet: dim={}, rate={}", world.getDimension().getType(), getRate());
        }
    }
}
