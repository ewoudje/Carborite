package com.ewoudje.carborite.networking;

public enum ProtocolState {
    HANDSHAKE,
    PLAY,
    STATUS,
    LOGIN,
    ;

    private final static ProtocolState[] values = values();

    public static ProtocolState fromId(int id) {
        return ++id < 0 || id >= values.length ? null : values[id];
    }
}