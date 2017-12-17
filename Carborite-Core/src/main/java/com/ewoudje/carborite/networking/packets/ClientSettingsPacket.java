package com.ewoudje.carborite.networking.packets;

//Made by ewoudje

import com.ewoudje.carborite.networking.PacketDataSerializer;

public class ClientSettingsPacket implements PacketPlayIn {

    private String lang;
    private byte view;
    private ChatSetting chat;
    private boolean colors;
    private byte skin;
    private boolean rightHanded;

    @Override
    public void read(PacketDataSerializer serializer) {
        lang = serializer.readString(16);
        view = serializer.readByte();
        chat = ChatSetting.fromId(serializer.readVarInt());
        colors = serializer.readBoolean();
        skin = serializer.readByte();
        rightHanded = serializer.readVarInt() == 0;
    }

    public String getLang() {
        return lang;
    }

    public byte getView() {
        return view;
    }

    public ChatSetting getChat() {
        return chat;
    }

    public boolean isColorsEnabled() {
        return colors;
    }

    public byte getSkin() {
        return skin;
    }

    public boolean isRightHanded() {
        return rightHanded;
    }

    public enum ChatSetting {
        ENABLED,
        COMMANDS_ONLY,
        HIDDEN;

        private final static ChatSetting[] values = values();

        public static ChatSetting fromId(int id) {
            return id < 0 || id >= values.length ? null : values[id];
        }
    }
}
