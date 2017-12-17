package com.ewoudje.carborite.networking;

//Made by ewoudje

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.util.List;

public class PacketDecryptor extends MessageToMessageDecoder<ByteBuf> {

    private final Cipher cipher;

    public PacketDecryptor(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int i = byteBuf.readableBytes();
        byte[] abyte = new byte[i];
        byteBuf.getBytes(i, abyte);
        ByteBuf byteBuf1 = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i));

        byteBuf1.writeBytes(this.cipher.update(abyte, 0, i));
        list.add(byteBuf1);
    }
}
