package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;

public class HeldItemSlotPacket implements PacketPlayOut {

    int i;

    public HeldItemSlotPacket(int i) {
        this.i = i;
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeByte(i);
    }
}
