package com.ewoudje.carborite.world;

public enum Dimension {
    NETHER(null),
    OVERWORLD(new World() {}),
    END(null);

    private World world;

    Dimension(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public int getId() {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == this) return i - 1;
        }
        return 0;
    }

    private final static Dimension[] values = values();

    public static Dimension fromId(int id) {
        return ++id < 0 || id >= values.length - 1 ? null : values[id];
    }
}
