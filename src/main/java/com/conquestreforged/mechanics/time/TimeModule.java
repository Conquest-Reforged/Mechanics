package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.Module;
import com.conquestreforged.mechanics.time.timer.Period;
import com.conquestreforged.mechanics.time.timer.ServerWorldTimer;
import com.conquestreforged.mechanics.time.timer.WorldTimer;
import com.conquestreforged.mechanics.time.timer.ticker.SleepTimeTicker;
import com.conquestreforged.mechanics.util.Channels;
import com.conquestreforged.mechanics.util.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TimeModule implements Module {

    private static final Marker MARKER = MarkerManager.getMarker("Time").addParents(Module.MARKER);

    // ConcurrentMap as server implementations may tick worlds asynchronously
    private final Map<DimensionType, WorldTimer> timers = new ConcurrentHashMap<>();

    @Override
    public Marker getMarker() {
        return MARKER;
    }

    @Override
    public String getName() {
        return "time";
    }

    @Override
    public void unload() {
        timers.clear();
    }

    @Override
    public boolean isEnabled(Config config) {
        return config.time.isEnabled();
    }

    @Override
    public void addDefaults(Config config) {
        info("Adding config defaults");
        config.time.put(DimensionType.OVERWORLD.getRegistryName() + "", 4F);
    }

    @Override
    public void load(Config config) {
        info("Loading from config");
        timers.clear();
        for (DimensionType dimension : DimensionType.getAll()) {
            Optional<Double> multiplier = config.time.get(dimension.getRegistryName() + "", Double.class);
            if (multiplier.isPresent()) {
                float value = multiplier.get().floatValue();
                ServerWorldTimer timer = new ServerWorldTimer();
                timer.add(Period.NIGHT, new SleepTimeTicker(value));
                timers.put(dimension, timer);
                info("Added timer: dim={}, rate={}", dimension.getRegistryName(), value);
            }
        }
    }

    @SubscribeEvent
    public void onSleep(PlayerSleepInBedEvent event) {
        if (event.getEntity().world.isRemote) {
            return;
        }

        if (timers.containsKey(event.getEntity().world.getDimension().getType())) {
            trace("Handling player sleep");
            // bypasses the server.updateAllPlayersSleepingFlag() call
            event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);

            // put the player in the bed
            event.getEntityLiving().startSleeping(event.getPos());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            WorldTimer timer = timers.get(event.world.getDimension().getType());
            if (timer != null) {
                timer.tick(event.world);
            }
        }
    }

    @SubscribeEvent
    public void onDimChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            trace("Handling player dimension change");
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            WorldTimer timer = timers.get(event.getTo());
            if (timer != null) {
                trace("Sending time packet: player={}, rate={}", player.getName(), timer.getRate());
                Channels.send(Channels.TIME, event.getPlayer(), new TimeMessage(timer.getRate()));
            }
        }
    }
}
