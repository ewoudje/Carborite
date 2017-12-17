package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;

public class PlayerAbilities implements PacketPlayOut, PacketPlayIn {

    private byte flags;
    private float flyingSpeed;
    private float fovModfier;

    public PlayerAbilities() {

    }

    public PlayerAbilities(byte flags, float flyingSpeed, float fovModfier) {
        this.flags = flags;
        this.flyingSpeed = flyingSpeed;
        this.fovModfier = fovModfier;
    }

    @Override
    public void read(PacketDataSerializer serializer) {
        flags = serializer.readByte();
        flyingSpeed = serializer.readFloat();
        fovModfier = serializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeByte(flags);
        serializer.writeFloat(flyingSpeed);
        serializer.writeFloat(fovModfier);
    }

    public byte getFlags() {
        return flags;
    }

    public float getFlyingSpeed() {
        return flyingSpeed;
    }

    public float getFovModfier() {
        return fovModfier;
    }
}
