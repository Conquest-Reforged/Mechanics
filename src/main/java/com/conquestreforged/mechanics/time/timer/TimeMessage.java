package com.conquestreforged.mechanics.time.timer;

import net.minecraft.network.PacketBuffer;

public class TimeMessage {

    private final int dimension;
    private final float rate;

    public TimeMessage(int dimension, float rate) {
        this.dimension = dimension;
        this.rate = rate;
    }

    public int getDimension() {
        return dimension;
    }

    public float getRate() {
        return rate;
    }

    public static void encode(TimeMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.dimension);
        buffer.writeFloat(message.rate);
    }

    public static TimeMessage decode(PacketBuffer buffer) {
        return new TimeMessage(buffer.readInt(), buffer.readFloat());
    }
}
