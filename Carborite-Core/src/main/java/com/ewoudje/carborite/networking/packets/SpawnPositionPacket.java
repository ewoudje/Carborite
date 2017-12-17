package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;
import com.ewoudje.carborite.world.BlockPosition;

public class SpawnPositionPacket implements PacketPlayOut {

    private BlockPosition position;

    public SpawnPositionPacket(BlockPosition position) {
        this.position = position;
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeBlockPosition(position);
    }
}
