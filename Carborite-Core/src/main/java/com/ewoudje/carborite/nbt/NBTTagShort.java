package com.ewoudje.carborite.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTNumber {

    private short data;

    public NBTTagShort() {}

    public NBTTagShort(short short0) {
        this.data = short0;
    }

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeShort(this.data);
    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(80L);
        this.data = datainput.readShort();
    }

    public byte getTypeId() {
        return (byte) 2;
    }

    public String toString() {
        return this.data + "s";
    }

    public NBTTagShort c() {
        return new NBTTagShort(this.data);
    }

    public boolean equals(Object object) {
        return super.equals(object) && this.data == ((NBTTagShort) object).data;
    }

    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public long asLong() {
        return (long) this.data;
    }

    public int asInt() {
        return this.data;
    }

    public short asShort() {
        return this.data;
    }

    public byte asByte() {
        return (byte) (this.data & 255);
    }

    public double asDouble() {
        return (double) this.data;
    }

    public float asFloat() {
        return (float) this.data;
    }

    public NBTBase clone() {
        return this.c();
    }
}
