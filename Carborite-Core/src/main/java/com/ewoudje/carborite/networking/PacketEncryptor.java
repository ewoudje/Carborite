package com.ewoudje.carborite.networking;

//Made by ewoudje

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;

import static com.ewoudje.carborite.networking.FramingHandler.bytesToHex;

public class PacketEncryptor extends MessageToByteEncoder<ByteBuf> {

    public final EncryptionTranslator translator;

    public PacketEncryptor(Cipher cipher) {
        this.translator = new EncryptionTranslator(cipher);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf byteBuf, ByteBuf output) throws Exception {
        try {
            translator.cipher(byteBuf, output);
            output.markReaderIndex();
            String utf8 = output.toString(StandardCharsets.UTF_8);
            byte[] bytes = utf8.getBytes(StandardCharsets.UTF_8);
            System.out.println("encrypted {length: " + bytes.length + " data: " + bytesToHex(bytes) + "\n utf-8: " + utf8.replace("\n", "\\n") + "}");
            output.resetReaderIndex();
            ByteBuf test = translator.decipher(ctx, byteBuf);
            int size = test.readableBytes();
            bytes = new byte[size];
            test.getBytes(size, bytes);
            System.out.println("decrypted {length: " + bytes.length + " data: " + bytesToHex(bytes) + "}");
            output.resetReaderIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
