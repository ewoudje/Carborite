/*
 * This file is part of Pingy, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, andor sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ewoudje.carborite.networking;

import com.ewoudje.carborite.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.nio.charset.StandardCharsets;
import java.util.List;

public final class FramingHandler extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buf0, ByteBuf output) throws Exception {
        PacketDataSerializer msg = new PacketDataSerializer(output);
        int size = buf0.readableBytes();
        msg.writeVarInt(size);
        msg.writeBytes(buf0);
        if (Server.isDebuggingOn()) {
            msg.markReaderIndex();
            int length = new PacketDataSerializer(msg).readVarInt();
            String utf8 = msg.toString(StandardCharsets.UTF_8);
            byte[] bytes = utf8.getBytes(StandardCharsets.UTF_8);
            System.out.println("package-out {length: " + length + " data: " + bytesToHex(bytes) + "\n utf-8: " + utf8.replace("\n", "\\n") + "}");
            msg.resetReaderIndex();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> output) throws Exception {
        PacketDataSerializer msg = new PacketDataSerializer(buf);
        while (readableVarInt(msg)) {
            msg.markReaderIndex();

            final int length = msg.readVarInt();
            if (msg.readableBytes() < length) {
                msg.resetReaderIndex();
                break;
            }

            final ByteBuf result = ctx.alloc().buffer(length);
            msg.readBytes(result, length);
            if (Server.isDebuggingOn()) {
                result.markReaderIndex();
                String utf8 = result.toString(StandardCharsets.UTF_8);
                byte[] bytes = utf8.getBytes(StandardCharsets.UTF_8);
                System.out.println("package-in {length: " + length + " data: " + bytesToHex(bytes) + "\n utf-8: " + utf8.replace("\n", "\\n") + "}");
                result.resetReaderIndex();
            }
            output.add(result);
        }
    }

    private static boolean readableVarInt(ByteBuf buf) {
        if (buf.readableBytes() > 5) {
            return true;
        }

        int idx = buf.readerIndex();
        byte in;
        do {
            if (buf.readableBytes() < 1) {
                buf.readerIndex(idx);
                return false;
            }
            in = buf.readByte();
        } while ((in & 0x80) != 0);

        buf.readerIndex(idx);
        return true;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }
}
