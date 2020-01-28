package com.conquestreforged.mechanics;

import com.conquestreforged.mechanics.config.Config;
import com.conquestreforged.mechanics.config.ConfigHelper;
import com.conquestreforged.mechanics.time.TimeModule;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.io.File;

@Mod("mechanics")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Mechanics {

    @SubscribeEvent
    public static void init(FMLServerAboutToStartEvent event) {
        Module.REGISTRY.add(new TimeModule());

        reload(event.getServer());
    }

    public static void reload(MinecraftServer server) {
        Config config = ConfigHelper.load(new File("config/mechanics.json"));
        for (Module module : Module.REGISTRY) {
            if (module.isEnabled(config)) {
                module.onLoad(server, config);
                MinecraftForge.EVENT_BUS.register(module);
            } else {
                MinecraftForge.EVENT_BUS.unregister(module);
            }
        }
    }
}
