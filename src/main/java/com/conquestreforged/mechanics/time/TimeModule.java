package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.Module;
import com.conquestreforged.mechanics.config.Config;
import com.conquestreforged.mechanics.time.ticker.SleepTimeTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TimeModule implements Module {

    private final Map<DimensionType, WorldTimer> timers = new ConcurrentHashMap<>();

    @Override
    public boolean isEnabled(Config config) {
        return config.time.isEnabled();
    }

    @Override
    public void addConfigDefaults(Config config) {
        config.time.put(DimensionType.OVERWORLD.getRegistryName() + "", 4F);
    }

    @Override
    public void onLoad(MinecraftServer server, Config config) {
        timers.clear();
        for (ServerWorld world : server.getWorlds()) {
            DimensionType dimension = world.getDimension().getType();
            Optional<Float> multiplier = config.time.get(dimension.getRegistryName() + "", Float.class);
            if (multiplier.isPresent()) {
                WorldTimer timer = new WorldTimer().add(Period.NIGHT, new SleepTimeTicker(multiplier.get()));
                timers.put(dimension, timer);
            }
        }
    }

    @SubscribeEvent
    public void onSleep(PlayerSleepInBedEvent event) {
        if (timers.containsKey(event.getEntity().world.getDimension().getType())) {
            // bypasses the server.updateAllPlayersSleepingFlag() call
            event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);

            // put the player in the bed
            event.getEntityLiving().startSleeping(event.getPos());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            WorldTimer timer = timers.get(event.world.getDimension().getType());
            if (timer == null) {
                return;
            }
            timer.tick(event.world);
        }
    }
}
