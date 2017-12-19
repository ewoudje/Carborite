package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.Server;
import com.ewoudje.carborite.networking.PacketDataSerializer;

import java.io.IOException;

public class PluginPacket implements PacketPlayOut, PacketPlayIn {

    String channel;
    byte[] data;

    public PluginPacket() {

    }

    public PluginPacket(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void read(PacketDataSerializer serializer) {
        channel = serializer.readString(20);
        int i = serializer.readableBytes();

        if (i >= 0 && i <= 1048576) {
            this.data = new byte[i];
            serializer.getBytes(i, data);
        } else {
            Server.error(new IOException("Payload may not be larger than 1048576 bytes"), "Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeString(channel);
        serializer.writeBytes(data);
    }
}
