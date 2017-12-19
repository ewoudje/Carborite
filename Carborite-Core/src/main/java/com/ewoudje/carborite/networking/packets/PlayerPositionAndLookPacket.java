package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;
import org.bukkit.Location;

public class PlayerPositionAndLookPacket implements PacketPlayOut, PacketPlayIn {

    private double x, y, z;
    private float yaw, pitch;
    private byte flags;
    private int teleportID;

    public PlayerPositionAndLookPacket(double x, double y, double z, float yaw, float pitch, byte flags, int teleportID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
        this.teleportID = teleportID;
    }

    public PlayerPositionAndLookPacket(Location location, byte flags, int teleportID) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.flags = flags;
        this.teleportID = teleportID;
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeDouble(x);
        serializer.writeDouble(y);
        serializer.writeDouble(z);
        serializer.writeFloat(yaw);
        serializer.writeFloat(pitch);
        serializer.writeByte(flags);
        serializer.writeVarInt(teleportID);
    }

    @Override
    public void read(PacketDataSerializer serializer) {
        x = serializer.readDouble();
        y = serializer.readDouble();
        z = serializer.readDouble();
        yaw = serializer.readFloat();
        pitch = serializer.readFloat();
        flags = serializer.readByte();
        teleportID = serializer.readVarInt();
    }

    public Location getLocation() {
        return new Location(null, x, y, z, yaw, pitch);
    }

    public byte getFlags() {
        return flags;
    }

    public int getTeleportID() {
        return teleportID;
    }
}
