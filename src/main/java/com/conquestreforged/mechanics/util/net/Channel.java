package com.conquestreforged.mechanics.util.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public class Channel {

    private final SimpleChannel channel;
    private final AtomicInteger counter = new AtomicInteger(-1);

    private Channel(ResourceLocation name, String protocol) {
        this.channel = NetworkRegistry.newSimpleChannel(name, () -> protocol, s -> true, s -> true);
    }

    public <T> Channel register(Class<T> message, Encoder<T> encoder, Decoder<T> decoder, Consumer<T> handler) {
        return register(message, encoder, decoder, wrap(handler));
    }

    public <T> Channel register(Class<T> message, Encoder<T> encoder, Decoder<T> decoder, Handler<T> handler) {
        return register(message, encoder, decoder, wrap(handler));
    }

    public <T> Channel register(Class<T> message, Encoder<T> encoder, Decoder<T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        channel.registerMessage(counter.addAndGet(1), message, encoder, decoder, handler);
        return this;
    }

    public <T> void send(PlayerEntity player, T message) {
        if (player instanceof ServerPlayerEntity) {
            send((ServerPlayerEntity) player, message);
        }
    }

    public <T> void send(ServerPlayerEntity player, T message) {
        send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public <T> void send(IWorld world, T message) {
        send(world.getDimension(), message);
    }

    public <T> void send(Dimension dimension, T message) {
        send(dimension.getType(), message);
    }

    public <T> void send(DimensionType dimension, T message) {
        send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    public <T> void send(PacketDistributor.PacketTarget target, T message) {
        channel.send(target, message);
    }

    public <T> void sendServer(T message) {
        channel.sendToServer(message);
    }

    public static Channel create(String name, String protocol) {
        return create(new ResourceLocation("mechanics", name), protocol);
    }

    public static Channel create(String namespace, String name, String protocol) {
        return create(new ResourceLocation(namespace, name), protocol);
    }

    public static Channel create(ResourceLocation name, String protocol) {
        return new Channel(name, protocol);
    }

    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> wrap(Consumer<T> handler) {
        return (t, context) -> {
            final NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> handler.accept(t));
            ctx.setPacketHandled(true);
        };
    }

    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> wrap(Handler<T> handler) {
        return (t, context) -> {
            final NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> handler.accept(t, ctx));
            ctx.setPacketHandled(true);
        };
    }

    public interface Handler<T> extends BiConsumer<T, NetworkEvent.Context> {

    }

    public interface Encoder<T> extends BiConsumer<T, PacketBuffer> {

    }

    public interface Decoder<T> extends Function<PacketBuffer, T> {

    }
}
