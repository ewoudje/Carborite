package com.ewoudje.carborite;

//Made by ewoudje

import com.ewoudje.carborite.networking.ConnectionHandler;
import com.ewoudje.carborite.networking.FramingHandler;
import com.ewoudje.carborite.networking.LegacyHandler;
import com.ewoudje.carborite.networking.MinecraftEncryption;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.security.KeyPair;

public class Server {

    public static Server instance;

    private static KeyPair pair = MinecraftEncryption.generateKeyPair();

    private Properties properties = new Properties();
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("timeout", new ReadTimeoutHandler(20))
                                    .addLast(new LegacyHandler(properties))
                                    .addLast(new FramingHandler())
                                    .addLast(new ConnectionHandler(properties, socketChannel));
                        }
                    })
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String args[]) {
        Server server = new Server(25565);
        instance = server;
        try {
            server.run();
        } catch (Exception e) {
            error(e, "Error at server");
        }

    }

    public static void error(Exception e, String description) {
        System.out.println("Error: " + description);
        e.printStackTrace();
    }

    public static void error(String error) {
        System.out.println("Error: " + error);
    }

    public static void warn(String warning) {
        System.out.println("Warning: " + warning);
    }

    public static boolean isDebuggingOn() {
        return instance.properties.isDebuggingOn();
    }

    public static KeyPair getKeyPair() {
        return pair;
    }

}
