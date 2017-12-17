package com.ewoudje.carborite.networking;

//Made by ewoudje

import com.ewoudje.carborite.MinecraftEncryption;
import com.ewoudje.carborite.Properties;
import com.ewoudje.carborite.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class ConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static Class<? extends PacketPlayIn>[] serverBoundPackages = new Class[] {

    };

    private Channel channel;
    private ProtocolState state = ProtocolState.HANDSHAKE;
    private Properties properties;
    private int protocol;
    private String address;
    private int port;
    private PrivateKey serverKey;
    private SecretKey secretKey;
    private byte[] verify;
    private String username;
    private UUID uuid;

    public ConnectionHandler(Properties properties, Channel channel) {
        this.properties = properties;
        this.channel = channel;
        Random random = new Random();
        verify = new byte[5];
        random.nextBytes(verify);
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
                    sendStatus(ctx);
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
                    username = msg.readString(16);
                    sendEncryptRequest(ctx);
                    break;
                case 0x01:
                    PrivateKey privateKey = Server.getKeyPair().getPrivate();
                    byte[] sharedSecret = msg.a();
                    secretKey = MinecraftEncryption.getSecretKey(privateKey, sharedSecret);
                    byte[] verify = msg.a();
                    verify = MinecraftEncryption.b(privateKey, verify);
                    if (!Arrays.equals(verify, this.verify)) {
                        System.out.println("Wrong verify key, retrying...");
                        sendEncryptRequest(ctx);
                    }
                    encrypt();
                    PacketDataSerializer serializer = new PacketDataSerializer(ctx.alloc().buffer());
                    uuid = UUID.randomUUID();
                    serializer.writeString(uuid.toString());
                    serializer.writeString(username);
                    ctx.writeAndFlush(serializer);
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

    private void sendStatus(ChannelHandlerContext ctx) {

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

    public void sendEncryptRequest(ChannelHandlerContext ctx) {
        PacketDataSerializer serializer1 = new PacketDataSerializer(ctx.alloc().buffer());
        serializer1.writeVarInt(0x01);
        serializer1.writeString("");
        byte[] publickey = Server.getKeyPair().getPublic().getEncoded();
        serializer1.writeVarInt(publickey.length);
        serializer1.writeBytes(publickey);
        serializer1.writeVarInt(5);
        serializer1.writeBytes(verify);
        ctx.writeAndFlush(serializer1);
    }

    public void encrypt() {
        this.channel.pipeline().addAfter("timeout", "decrypt", new PacketDecryptor(MinecraftEncryption.a(2, secretKey)));
        this.channel.pipeline().addAfter("timeout", "encrypt", new PacketEncryptor(MinecraftEncryption.a(1, secretKey)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
