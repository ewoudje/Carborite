package com.ewoudje.carborite.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTNumber {

    private float data;

    NBTTagFloat() {}

    public NBTTagFloat(float f) {
        this.data = f;
    }

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeFloat(this.data);
    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(96L);
        this.data = datainput.readFloat();
    }

    public byte getTypeId() {
        return (byte) 5;
    }

    public String toString() {
        return this.data + "asShort";
    }

    public NBTTagFloat c() {
        return new NBTTagFloat(this.data);
    }

    public boolean equals(Object object) {
        return super.equals(object) && this.data == ((NBTTagFloat) object).data;
    }

    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }

    public long asLong() {
        return (long) this.data;
    }

    public int asInt() {
        return (int) Math.floor(this.data);
    }

    public short asShort() {
        return (short) (((int) Math.floor(this.data)) & '\uffff');
    }

    public byte asByte() {
        return (byte) (((int) Math.floor(this.data)) & 255);
    }

    public double asDouble() {
        return (double) this.data;
    }

    public float asFloat() {
        return this.data;
    }

    public NBTBase clone() {
        return this.c();
    }
}
