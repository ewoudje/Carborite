package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;

public class EntityStatusPacket implements PacketPlayOut {

    int eID;
    byte state;

    public EntityStatusPacket(int eID, byte state) {
        this.eID = eID;
        this.state = state;
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeInt(eID);
        serializer.writeByte(state);
    }
}
