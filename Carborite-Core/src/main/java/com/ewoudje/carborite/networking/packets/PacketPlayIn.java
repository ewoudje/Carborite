package com.ewoudje.carborite.networking.packets;

import com.ewoudje.carborite.networking.PacketDataSerializer;

public interface PacketPlayIn {

    void read(PacketDataSerializer serializer);

}
