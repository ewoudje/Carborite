package com.ewoudje.carborite.management;

//Made by ewoudje

import com.ewoudje.carborite.entitys.CRPlayer;
import com.ewoudje.carborite.networking.ConnectionHandler;
import com.ewoudje.carborite.networking.packets.*;
import com.ewoudje.carborite.world.BlockPosition;
import com.ewoudje.carborite.world.Difficulty;
import com.ewoudje.carborite.world.Dimension;
import com.ewoudje.carborite.world.Gamemode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayerList {

    public static PlayerList players = new PlayerList();

    private List<CRPlayer> onlinePlayers = new ArrayList<>();

    public synchronized void initiatePlayer(ConnectionHandler connection, String name, UUID uuid) {
        Random random = new Random();
        connection.sendPlayPacket(0x23, new JoinGamePacket(0, Gamemode.SURVIVAL, Dimension.OVERWORLD, Difficulty.PEACEFULL));
        connection.sendPlayPacket(0x18, new PluginPacket("MC|Brand", "carborite".getBytes(StandardCharsets.UTF_8)));
        connection.sendPlayPacket(0x0D, new DifficultyPacket(Difficulty.PEACEFULL));
        connection.sendPlayPacket(0x46, new SpawnPositionPacket(BlockPosition.ZERO));
        connection.sendPlayPacket(0x2C, new PlayerAbilitiesPacket((byte) 0, 1f, 1f));
        connection.sendPlayPacket(0x3A, new HeldItemSlotPacket(0));
        connection.sendPlayPacket(0x1B, new EntityStatusPacket(0, (byte) 0));
        int tpId = random.nextInt();
        PlayerPositionAndLookPacket tpPacket = new PlayerPositionAndLookPacket(0, 0, 0, 0, 0, (byte) 0, tpId);
        connection.sendPlayPacket(0x2F, tpPacket);
        CRPlayer player = new CRPlayer(connection, random);
        player.addTeleport(tpId, tpPacket.getLocation());
        onlinePlayers.add(player);
    }

    public CRPlayer getOnlinePlayer(String username) {
        for (CRPlayer player : onlinePlayers) {
            if (player.getName().equals(username)) return player;
        }
        return null;
    }


}
