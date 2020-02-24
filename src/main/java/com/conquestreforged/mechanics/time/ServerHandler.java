package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.util.Log;
import com.conquestreforged.mechanics.util.net.Channels;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerHandler {

    private static final Marker MARKER = MarkerManager.getMarker("Server");

    @SubscribeEvent
    public static void init(FMLDedicatedServerSetupEvent event) {
        Log.info(MARKER, "Registering networking");
        Channels.TIME.register(TimeMessage.class, TimeMessage::encode, TimeMessage::decode, msg -> {});
    }
}
