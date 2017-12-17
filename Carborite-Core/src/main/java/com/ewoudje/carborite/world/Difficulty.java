package com.ewoudje.carborite.world;

//Made by ewoudje

public enum  Difficulty {
    PEACEFULL,
    EASY,
    NORMAL,
    HARD;

    public byte getId() {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == this) return (byte) i;
        }
        return 0;
    }

    private final static Difficulty[] values = values();

    public static Difficulty fromId(int id) {
        return id < 0 || id >= values.length ? null : values[id];
    }
}
