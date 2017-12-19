package com.ewoudje.carborite.networking;

//Made by ewoudje

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.util.List;

public class PacketDecryptor extends MessageToMessageDecoder<ByteBuf> {

    public final EncryptionTranslator translator;

    public PacketDecryptor(Cipher cipher) {
        this.translator = new EncryptionTranslator(cipher);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(translator.decipher(ctx, byteBuf));
    }
}
