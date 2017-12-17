package com.ewoudje.carborite.networking;

//Made by ewoudje

import com.ewoudje.carborite.Properties;
import com.ewoudje.carborite.Server;
import com.ewoudje.carborite.networking.listeners.PacketPlayInListener;
import com.ewoudje.carborite.networking.listeners.PacketPlayOutListener;
import com.ewoudje.carborite.networking.packets.*;
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
import java.util.*;

public class ConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static Class<? extends PacketPlayIn>[] playInPackages = new Class[256];
    private static Class<? extends PacketPlayOut>[] playOutPackages = new Class[256];
    private static Map<Class<? extends PacketPlayIn>, List<PacketPlayInListener>> playInListeners = new HashMap<>();
    private static Map<Class<? extends PacketPlayOut>, List<PacketPlayOutListener>> playOutListeners = new HashMap<>();

    static {
        playInPackages[0x04] = ClientSettingsPacket.class;
        playInPackages[0x13] = PlayerAbilities.class;

        playOutPackages[0x23] = JoinGamePacket.class;
        playOutPackages[0x46] = SpawnPositionPacket.class;
        playOutPackages[0x2C] = PlayerAbilities.class;
    }

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
            PacketPlayIn packet = playInPackages[id].newInstance();
            packet.read(msg);
            List<PacketPlayInListener> listeners = playInListeners.get(packet.getClass());
            if (listeners != null) {
                for (PacketPlayInListener listener : listeners) {
                    listener.onReceive(packet, this);
                }
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
                    state = ProtocolState.PLAY;
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
        ctx.writeAndFlush(serializer1.buf);
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

    public void sendPlayPacket(ChannelHandlerContext ctx, int id, PacketPlayOut packet) {
        boolean cancel = false;
        List<PacketPlayOutListener> listeners = playOutListeners.get(packet.getClass());
        if (listeners != null) {
            for (PacketPlayOutListener listener : listeners) {
                if (listener.onSend(packet, this)) cancel = true;
            }
        }
        if (!cancel) {
            PacketDataSerializer serializer = new PacketDataSerializer(ctx.alloc().buffer());
            serializer.writeVarInt(id);
            packet.write(serializer);
            ctx.writeAndFlush(serializer);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
