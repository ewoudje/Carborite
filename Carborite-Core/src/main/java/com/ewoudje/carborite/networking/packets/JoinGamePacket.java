package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;
import com.ewoudje.carborite.world.Difficulty;
import com.ewoudje.carborite.world.Dimension;
import com.ewoudje.carborite.world.Gamemode;

public class JoinGamePacket implements PacketPlayOut {

    private int eID;
    private Gamemode gamemode;
    private Dimension dimension;
    private Difficulty difficulty;
    private byte maxPlayers = (byte) 0xff;
    private String world_type = "default";
    private boolean debug = true;

    public JoinGamePacket(int eID, Gamemode gamemode, Dimension dimension, Difficulty difficulty) {
        this.eID = eID;
        this.gamemode = gamemode;
        this.dimension = dimension;
        this.difficulty = difficulty;
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeInt(eID);
        serializer.writeByte(gamemode.getId() + (false ? 0x8 : 0x0));
        serializer.writeInt(dimension.getId());
        serializer.writeByte(difficulty.getId());
        serializer.writeByte(maxPlayers);
        serializer.writeString(world_type);
        serializer.writeBoolean(!debug);
    }
}
