package com.conquestreforged.mechanics.time;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TimeClient {

    @SubscribeEvent
    public static void init(FMLServerAboutToStartEvent event) {
        TimeModule.INSTANCE.init();
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        Screen screen = Minecraft.getInstance().currentScreen;
        if (screen != null && !(screen instanceof SleepInMultiplayerScreen)) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        TimeModule.INSTANCE.tick(player.world);
    }
}
