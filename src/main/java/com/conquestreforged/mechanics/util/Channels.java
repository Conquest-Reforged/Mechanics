package com.conquestreforged.mechanics.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Channels {

    public static final SimpleChannel TIME = Channels.create("time");

    private static final String protocolVersion = "0.0.1";
    private static final AtomicInteger messageIdCounter = new AtomicInteger(-1);

    public static int nextMessageId() {
        return messageIdCounter.addAndGet(1);
    }

    public static void send(SimpleChannel channel, PlayerEntity player, Object message) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity target = (ServerPlayerEntity) player;
            channel.send(PacketDistributor.PLAYER.with(() -> target), message);
        }
    }

    public static void send(SimpleChannel channel, Dimension dimension, Object message) {
        channel.send(PacketDistributor.DIMENSION.with(dimension::getType), message);
    }

    public static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> emptyHandler() {
        return (t, context) -> {};
    }

    public static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> drainingHandler() {
        return (t, context) -> context.get().setPacketHandled(true);
    }

    private static SimpleChannel create(String name) {
        return NetworkRegistry.newSimpleChannel(
                new ResourceLocation("mechanics", name),
                () -> protocolVersion,
                s -> s.equals(protocolVersion),
                s -> s.equals(protocolVersion)
        );
    }
}
