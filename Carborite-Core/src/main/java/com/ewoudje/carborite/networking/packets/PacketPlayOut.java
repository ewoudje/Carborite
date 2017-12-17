package com.ewoudje.carborite.networking.packets;

import com.ewoudje.carborite.networking.PacketDataSerializer;

public interface PacketPlayOut {

    void write(PacketDataSerializer serializer);

}
