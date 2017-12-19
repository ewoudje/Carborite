package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;
import com.ewoudje.carborite.world.Difficulty;

public class DifficultyPacket implements PacketPlayOut {

    Difficulty difficulty;

    public DifficultyPacket(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void write(PacketDataSerializer serializer) {
        serializer.writeByte(difficulty.getId());
    }
}
