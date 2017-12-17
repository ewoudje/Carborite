package com.ewoudje.carborite.world;

//Made by ewoudje

public class BlockPosition {

    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);

    int x, y, z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public long asLong() {
        return ((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF);
    }

    public static BlockPosition fromLong(long i) {
        return new BlockPosition((int) i >> 38, (int) (i >> 26) & 0xFFF, (int) i << 38 >> 38);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof BlockPosition)) {
            return false;
        } else {
            BlockPosition baseblockposition = (BlockPosition) object;

            return this.getX() != baseblockposition.getX() ? false : (this.getY() != baseblockposition.getY() ? false : this.getZ() == baseblockposition.getZ());
        }
    }
}
