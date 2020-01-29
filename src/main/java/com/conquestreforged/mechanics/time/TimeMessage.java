package com.conquestreforged.mechanics.time;

import com.conquestreforged.mechanics.util.Channels;
import net.minecraft.network.PacketBuffer;

public class TimeMessage {

    public static final int ID = Channels.nextMessageId();

    private final float rate;

    public TimeMessage(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    public static void encode(TimeMessage message, PacketBuffer buffer) {
        buffer.writeFloat(message.rate);
    }

    public static TimeMessage decode(PacketBuffer buffer) {
        return new TimeMessage(buffer.readFloat());
    }
}
