package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.time.ticker.TimeTicker;
import net.minecraft.world.IWorld;

import java.util.EnumMap;
import java.util.Map;

public class WorldTimer {

    private static final float MAX = 23999;

    private final Map<Period, TimeTicker> modifiers = new EnumMap<>(Period.class);

    private float time = -1F;
    private Period currentPeriod = Period.MORNING;

    public WorldTimer add(Period time, TimeTicker modifier) {
        modifiers.put(time, modifier);
        return this;
    }

    public void tick(IWorld world) {
        // get current ticks & the last stored ticks
        long ticks = world.getWorldInfo().getDayTime();
        float time = getPartialTime(ticks);

        // detect time period and get the appropriate ticker
        Period period = getPeriod(ticks);
        TimeTicker ticker = getTicker(period);

        // increment the partial time value
        setPartialTime(time + ticker.getIncrement(world));

        // set the day time. an offset of -1 is used as the server will increment it +1 later in the tick cycle
        world.getWorldInfo().setDayTime(getTime() - 1L);
    }

    public long getTime() {
        return (long) time;
    }

    private Period getPeriod(long ticks) {
        if (!currentPeriod.isWithin(ticks)) {
            currentPeriod = Period.getPeriod(ticks);
        }
        return currentPeriod;
    }

    private TimeTicker getTicker(Period period) {
        return modifiers.getOrDefault(period, TimeTicker.NONE);
    }

    private float getPartialTime(long ticks) {
        // if the delta is greater than one then someone else has changed the time
        if (Math.abs(ticks - getTime()) > 1) {
            time = ticks;
        }
        return time;
    }

    private void setPartialTime(float time) {
        // wrap the time if it overflows into the next day
        while (time > MAX) {
            time -= MAX;
        }
        this.time = time;
    }
}
