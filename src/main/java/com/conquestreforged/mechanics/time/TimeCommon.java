package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.Module;
import com.conquestreforged.mechanics.network.Channels;
import com.conquestreforged.mechanics.time.timer.TimeMessage;
import com.conquestreforged.mechanics.time.timer.WorldTimer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TimeCommon {

    private static final TimeModule SERVER = new TimeModule(WorldTimer::new);

    @SubscribeEvent
    public static void init(FMLServerAboutToStartEvent event) {
        Module.REGISTRY.put("time", TimeCommon.SERVER);

        Channels.TIME.registerMessage(
                0,
                TimeMessage.class,
                TimeMessage::encode,
                TimeMessage::decode,
                TimeModule.INSTANCE::handleMessage
        );
    }

    @SubscribeEvent
    public static void sleep(PlayerSleepInBedEvent event) {
        if (event.getEntity().world.isRemote) {
            return;
        }

        if (TimeCommon.SERVER.has(event.getEntity().world.getDimension().getType())) {
            // bypasses the server.updateAllPlayersSleepingFlag() call
            event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);

            // put the player in the bed
            event.getEntityLiving().startSleeping(event.getPos());
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TimeCommon.SERVER.tick(event.world);
        }
    }
}
