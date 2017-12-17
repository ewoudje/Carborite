package com.ewoudje.carborite.networking;

//Made by ewoudje

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import javax.crypto.Cipher;
import java.util.List;

public class PacketEncryptor extends MessageToMessageEncoder<ByteBuf> {

    private Cipher cipher;

    public PacketEncryptor(Cipher chiper) {
        this.cipher = cipher;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> output) throws Exception {
        int size = byteBuf.readableBytes();
        byte[] bytes = new byte[size];
        byteBuf.getBytes(size, bytes);
        byte[] encrypted = cipher.update(bytes);
        ByteBuf result = ctx.alloc().buffer(size);
        result.writeBytes(encrypted);
        output.add(result);
    }
}
