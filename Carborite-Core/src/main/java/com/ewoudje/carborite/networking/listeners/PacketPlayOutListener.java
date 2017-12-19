package com.ewoudje.carborite.networking.listeners;

import com.ewoudje.carborite.networking.ConnectionHandler;
import com.ewoudje.carborite.networking.packets.PacketPlayOut;

public interface PacketPlayOutListener {

    boolean onSend(PacketPlayOut packet, ConnectionHandler handler);

}
