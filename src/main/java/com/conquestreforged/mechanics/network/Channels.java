package com.conquestreforged.mechanics.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Channels {

    public static final SimpleChannel TIME = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("mechanics", "time"),
            () -> "0.1",
            s -> true,
            s -> true
    );

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {}
}
