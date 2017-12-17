package com.ewoudje.carborite.world;

//Made by ewoudje

import com.ewoudje.carborite.networking.listeners.PacketPlayInListener;
import com.ewoudje.carborite.networking.packets.ClientSettingsPacket;

public abstract class World implements PacketPlayInListener<ClientSettingsPacket> {

    public Gamemode getDefaultGamemode() {
        return Gamemode.SURVIVAL;
    }

    public boolean isHardcore() {
        return false;
    }
}
