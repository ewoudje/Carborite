package com.ewoudje.carborite.world;

//Made by ewoudje

public abstract class World {

    public Gamemode getDefaultGamemode() {
        return Gamemode.SURVIVAL;
    }

    public boolean isHardcore() {
        return false;
    }

}
