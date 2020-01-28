package com.conquestreforged.mechanics.time.timer;

import com.conquestreforged.mechanics.network.Channels;
import com.conquestreforged.mechanics.time.ticker.TimeTicker;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.EnumMap;
import java.util.Map;

public class WorldTimerServer extends WorldTimer {

    private final Map<Period, TimeTicker> modifiers = new EnumMap<>(Period.class);

    private float currentRate = 1F;
    private Period currentPeriod = Period.MORNING;

    public WorldTimerServer(DimensionType type) {
        super(type);
    }

    @Override
    protected float getIncrement(IWorld world) {
        long ticks = world.getWorldInfo().getDayTime();
        Period period = getPeriod(ticks);
        TimeTicker ticker = getTicker(period);
        return getIncrement(world, ticker, ticks);
    }

    public void add(Period period, TimeTicker timeTicker) {
        modifiers.put(period, timeTicker);
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

    private float getIncrement(IWorld world, TimeTicker ticker, long ticks) {
        if (ticks % 20 == 0) {
            float rate = ticker.getIncrement(world);
            if (rate != currentRate) {
                currentRate = rate;
                TimeMessage message = new TimeMessage(getDimension().getId(), currentRate);
                Channels.TIME.send(PacketDistributor.DIMENSION.with(world.getDimension()::getType), message);
            }
        }
        return currentRate;
    }
}
