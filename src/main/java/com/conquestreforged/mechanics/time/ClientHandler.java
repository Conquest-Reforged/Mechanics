package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.time.timer.WorldTimer;
import com.conquestreforged.mechanics.util.Log;
import com.conquestreforged.mechanics.util.net.Channels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler {

    private static final Marker MARKER = MarkerManager.getMarker("Client");
    private static final WorldTimer timer = new WorldTimer();

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        Log.info(MARKER, "Setting up client");

        Channels.TIME.register(TimeMessage.class, TimeMessage::encode, TimeMessage::decode, ClientHandler::handleMessage);
        MinecraftForge.EVENT_BUS.addListener(ClientHandler::tick);
    }

    private static void handleMessage(TimeMessage message, NetworkEvent.Context context) {
        if (context.getDirection().getReceptionSide().isClient()) {
            timer.setRate(message.getRate());
            Log.trace(MARKER, "Received packet: rate={}", message.getRate());
        }
    }

    private static void tick(TickEvent.ClientTickEvent event) {
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

        if (timer.canTick(player.world)) {
            timer.tick(player.world);
        }
    }
}
