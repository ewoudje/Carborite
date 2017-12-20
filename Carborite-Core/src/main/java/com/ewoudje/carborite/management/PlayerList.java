package com.ewoudje.carborite.management;

//Made by ewoudje

import com.ewoudje.carborite.networking.ConnectionHandler;
import com.ewoudje.carborite.networking.packets.*;
import com.ewoudje.carborite.world.BlockPosition;
import com.ewoudje.carborite.world.Difficulty;
import com.ewoudje.carborite.world.Dimension;
import com.ewoudje.carborite.world.Gamemode;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayerList {

    public static PlayerList players = new PlayerList();

    public void initiatePlayer(ConnectionHandler connection, String name, UUID uuid) {
        connection.sendPlayPacket(0x23, new JoinGamePacket(0, Gamemode.SURVIVAL, Dimension.OVERWORLD, Difficulty.PEACEFULL));
        connection.sendPlayPacket(0x18, new PluginPacket("MC|Brand", "carborite".getBytes(StandardCharsets.UTF_8)));
        connection.sendPlayPacket(0x0D, new DifficultyPacket(Difficulty.PEACEFULL));
        connection.sendPlayPacket(0x46, new SpawnPositionPacket(BlockPosition.ZERO));
        connection.sendPlayPacket(0x2C, new PlayerAbilitiesPacket((byte) 0, 1f, 1f));
        connection.sendPlayPacket(0x3A, new HeldItemSlotPacket(0));
        connection.sendPlayPacket(0x1B, new EntityStatusPacket(0, (byte) 0));
        //connection.sendPlayPacket(0x2F, new PlayerPositionPacket(0, 0, 0, 0, 0, (byte) 0, 0));
    }

}
