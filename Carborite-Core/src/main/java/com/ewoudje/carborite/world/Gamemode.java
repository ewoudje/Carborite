package com.ewoudje.carborite.world;

public enum Gamemode {
    SURVIVAL,
    ADVENTURE,
    CREATIVE,
    SPECTATE;

    public byte getId() {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == this) return (byte) i;
        }
        return 0;
    }

    private final static Gamemode[] values = values();

    public static Gamemode fromId(byte id) {
        return id < 0 || id >= values.length ? null : values[id];
    }
}
