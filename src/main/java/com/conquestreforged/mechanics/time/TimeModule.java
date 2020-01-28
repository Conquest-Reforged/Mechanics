package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.Module;
import com.conquestreforged.mechanics.config.Config;
import com.conquestreforged.mechanics.time.ticker.SleepTimeTicker;
import com.conquestreforged.mechanics.time.timer.Period;
import com.conquestreforged.mechanics.time.timer.TimeMessage;
import com.conquestreforged.mechanics.time.timer.WorldTimer;
import com.conquestreforged.mechanics.time.timer.WorldTimerServer;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class TimeModule implements Module {

    public static final TimeModule INSTANCE = new TimeModule(WorldTimer::new);

    private final Function<DimensionType, WorldTimer> constructor;
    private final Map<DimensionType, WorldTimer> timers = new ConcurrentHashMap<>();

    public TimeModule(Function<DimensionType, WorldTimer> constructor) {
        this.constructor = constructor;
    }

    public boolean has(DimensionType dimensionType) {
        return timers.containsKey(dimensionType);
    }

    public void tick(IWorld world) {
        WorldTimer timer = timers.get(world.getDimension().getType());
        if (timer == null) {
            return;
        }
        timer.tick(world);
    }

    @Override
    public boolean isEnabled(Config config) {
        return config.time.isEnabled();
    }

    @Override
    public void addConfigDefaults(Config config) {
        config.time.put(DimensionType.OVERWORLD.getRegistryName() + "", 4F);
    }

    @Override
    public void init() {
        timers.clear();
    }

    @Override
    public void onLoad(Config config) {
        for (DimensionType dimension : DimensionType.getAll()) {
            Optional<Double> multiplier = config.time.get(dimension.getRegistryName() + "", Double.class);
            if (multiplier.isPresent()) {
                WorldTimerServer timer = new WorldTimerServer(dimension);
                timer.add(Period.NIGHT, new SleepTimeTicker(multiplier.get().floatValue()));
                timers.put(dimension, timer);
                System.out.println("#" + dimension);
            }
        }
    }

    protected void handleMessage(TimeMessage message, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isClient()) {
            DimensionType type = DimensionType.getById(message.getDimension());
            WorldTimer timer = timers.computeIfAbsent(type, constructor);
            timer.setRate(message.getRate());
            context.get().setPacketHandled(true);
        }
    }
}
