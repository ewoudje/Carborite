package com.ewoudje.carborite.networking.listeners;

import com.ewoudje.carborite.networking.ConnectionHandler;
import com.ewoudje.carborite.networking.packets.PacketPlayIn;

public interface PacketPlayInListener {

    void onReceive(PacketPlayIn packet, ConnectionHandler handler);

}
