package com.conquestreforged.mechanics;

import com.conquestreforged.mechanics.time.TimeModule;
import com.conquestreforged.mechanics.util.Log;
import com.conquestreforged.mechanics.util.config.Config;
import com.conquestreforged.mechanics.util.config.ConfigHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;

@Mod("mechanics")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Mechanics {

    private static final Marker MARKER = MarkerManager.getMarker("Main");

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        Log.debug(MARKER, "Registering modules");
        Module.REGISTRY.add(new TimeModule());
        MinecraftForge.EVENT_BUS.addListener(Mechanics::load);
    }

    private static void load(FMLServerStartedEvent event) {
        Log.debug(MARKER, "Loading modules");
        Config config = ConfigHelper.load(new File("config/mechanics.json"));
        for (Module module : Module.REGISTRY) {
            if (module.isEnabled(config)) {
                module.load(config);
                MinecraftForge.EVENT_BUS.register(module);
                Log.debug(MARKER, " Enabled module: {}", module.getName());
            } else {
                module.unload();
                MinecraftForge.EVENT_BUS.unregister(module);
                Log.debug(MARKER, " Disabled module: {}", module.getName());
            }
        }
    }
}
