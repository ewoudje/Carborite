package com.ewoudje.carborite.networking.listeners;

import com.ewoudje.carborite.networking.ConnectionHandler;
import com.ewoudje.carborite.networking.packets.PacketPlayIn;

public interface PacketPlayInListener<T extends PacketPlayIn> {

    void onReceive(T packet, ConnectionHandler handler);

}
