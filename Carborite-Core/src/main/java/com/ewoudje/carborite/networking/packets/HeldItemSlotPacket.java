package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;

public class HeldItemSlotPacket implements PacketPlayOut, PacketPlayIn {

    int i;

    public HeldItemSlotPacket() {}

    public HeldItemSlotPacket(int i) {
        this.i = i;
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeByte(i);
    }

    @Override
    public void read(PacketDataSerializer serializer) {
        i = serializer.readByte();
    }

    public int getSlot() {
        return i;
    }
}
