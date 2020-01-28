package com.conquestreforged.mechanics;

import com.conquestreforged.mechanics.config.Config;
import com.conquestreforged.mechanics.config.ConfigHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.io.File;

@Mod("mechanics")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Mechanics {

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(Mechanics::load);
    }

    private static void load(FMLServerStartedEvent event) {
        Config config = ConfigHelper.load(new File("config/mechanics.json"));
        for (Module module : Module.REGISTRY.values()) {
            if (module.isEnabled(config)) {
                module.init();
                module.onLoad(config);
            }
        }
    }
}
