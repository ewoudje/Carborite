package com.ewoudje.carborite;

//Made by ewoudje

import com.ewoudje.carborite.world.Gamemode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Properties {

    private String ip = "";
    private int port = 25565;
    private String messageOfTheDay = "Carborite";
    private String legacyMessageOfTheDay = "Carborite - Legacy Message";
    private String outdatedMessage = "Use the right version!";
    private String favicon = "";
    private String version = "1.12.2";
    private String serverType = "carborite";
    private int protocol = 340;
    private int maxPlayers = 20;
    private boolean isOnline = false;

    private String faviconData;

    public void loadFavicon(Path directory) throws IOException {

        if (this.favicon.isEmpty()) {
            return;
        }

        final Path faviconPath = directory.resolve(this.favicon);
        if (!Files.exists(faviconPath)) {
            throw new IOException("Favicon file does not exist.");
        }

        final BufferedImage image;
        try {
            image = ImageIO.read(faviconPath.toFile());
        } catch (IOException e) {
            throw new IOException("Unable to read the favicon file.");
        }

        if (image.getWidth() != 64) {
            throw new IOException("Favicon must be 64 pixels wide.");
        }

        if (image.getHeight() != 64) {
            throw new IOException("Favicon must be 64 pixels high.");
        }
        
        final ByteBuf buf = Unpooled.buffer();
        try {
            ImageIO.write(image, "PNG", new ByteBufOutputStream(buf));
            final ByteBuf base64 = Base64.encode(buf);

            try {
                this.faviconData = "data:image/png;base64," + base64.toString(StandardCharsets.UTF_8);
            } finally {
                base64.release();
            }
        } finally {
            buf.release();
        }
    }

    public String getServerType() {
        return this.serverType;
    }

    public int getPort() {
        return this.port;
    }

    public String getMOTD() {
        return this.messageOfTheDay;
    }

    public String getOutdatedMessage() {
        return this.outdatedMessage;
    }

    public String getIp() {
        return this.ip;
    }

    public String getLegacyMOTD() {
        return this.legacyMessageOfTheDay;
    }

    public Optional<String> getFaviconData() {
        return Optional.ofNullable(this.faviconData);
    }

    public String getVersion() {
        return version;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isDebuggingOn() {
        return true;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public Gamemode getDefaultGamemode() {
        return Gamemode.SURVIVAL;
    }

    public boolean isHardcore() {
        return false;
    }

}
