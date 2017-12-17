package com.ewoudje.carborite.networking.listeners;

import com.ewoudje.carborite.networking.ConnectionHandler;
import com.ewoudje.carborite.networking.packets.PacketPlayOut;

public interface PacketPlayOutListener<T extends PacketPlayOut> {

    boolean onSend(T packet, ConnectionHandler handler);

}
