package com.ewoudje.carborite.networking;

//Made by ewoudje

import com.ewoudje.carborite.Properties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ProtocolState state = ProtocolState.HANDSHAKE;
    private Properties properties;
    private int protocol;
    private String address;
    private int port;

    public ConnectionHandler(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println(ctx.channel().remoteAddress() + " connected to the server.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println(ctx.channel().remoteAddress() + " disconnected from the server.");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        final PacketDataSerializer msg = new PacketDataSerializer(buf);
        final int id = msg.readVarInt();
        if (state == ProtocolState.PLAY) {
            switch (id) {
                case 0x00:

                    break;
                default:
                    ctx.channel().close();
                    throw new DecoderException("Unknown play message type: " + id);

            }
        } else if (state == ProtocolState.HANDSHAKE) {
            switch (id) {
                case 0x00:
                    handshake(msg);
                    break;
                default:
                    ctx.channel().close();
                    throw new DecoderException("Unknown handshake message type: " + id);

            }
        } else if (state == ProtocolState.STATUS) {
            switch (id) {
                case 0x00:
                    status(ctx);
                    break;
                case 0x01:
                    PacketDataSerializer serializer1 = new PacketDataSerializer(ctx.alloc().buffer());
                    serializer1.writeVarInt(0x01);
                    serializer1.writeLong(msg.readLong());
                    ctx.writeAndFlush(serializer1);
                    ctx.channel().close();
                    break;
                default:
                    ctx.channel().close();
                    throw new DecoderException("Unknown status message type: " + id);
            }
        } else if (state == ProtocolState.LOGIN) {
            switch (id) {
                case 0x00:
                    PacketDataSerializer serializer1 = new PacketDataSerializer(ctx.alloc().buffer());
                    serializer1.writeVarInt(0x00);
                    serializer1.writeString("Not ready yet");
                    ctx.writeAndFlush(serializer1);
                    ctx.channel().close();
                    break;
                case 0x01:

                    break;
                default:
                    ctx.channel().close();
                    throw new DecoderException("Unknown login message type: " + id);
            }
        }
    }

    private void handshake(PacketDataSerializer serializer) {

        protocol = serializer.readVarInt();
        address = serializer.readString(255);
        port = serializer.readUnsignedShort();
        state = ProtocolState.fromId(serializer.readVarInt());
        System.out.println("handshake {protocol: " + protocol + " state: "  + state + "}");
    }

    private void status(ChannelHandlerContext ctx) {

        JSONObject json = new JSONObject();
        JSONObject versionJson = new JSONObject();
        versionJson.put("name", properties.getVersion());
        versionJson.put("protocol", 340);
        JSONObject playersJson = new JSONObject();
        playersJson.put("max", properties.getMaxPlayers());
        playersJson.put("online", 5);
        JSONArray sampleJsonArray = new JSONArray();
        for (int i = 0; i < 1; i++) {
            JSONObject sampleJson = new JSONObject();
            sampleJson.put("name", "EIA");
            sampleJson.put("id", UUID.randomUUID().toString());
            sampleJsonArray.put(sampleJson);
        }
        playersJson.put("sample", sampleJsonArray);
        json.put("players", playersJson);
        json.put("description", properties.getMOTD());
        json.put("version", versionJson);
        if (properties.getFaviconData().isPresent()) json.put("favicon", "data:image/png;base64," + properties.getFaviconData());
        PacketDataSerializer serializer1 = new PacketDataSerializer(ctx.alloc().buffer());
        serializer1.writeVarInt(0x00);
        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        serializer1.writeVarInt(bytes.length);
        serializer1.writeBytes(bytes);
        ctx.writeAndFlush(serializer1.a);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
