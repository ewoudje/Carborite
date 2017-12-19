package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;

public class TeleportConfirmPacket implements PacketPlayIn {

    private int id;

    @Override
    public void read(PacketDataSerializer serializer) {
        id = serializer.readVarInt();
    }

    public int getId() {
        return id;
    }
}
