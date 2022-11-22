package net.C2S;

public enum ClientToServer {
    LOGIN,
    REGISTER,
    GIVE_ALL_MEMBER,
    SEND_NAME_TO_ALL,
    MESSAGE,
    LOGOUT;

    public final int value = ordinal();
    public static ClientToServer[] messages = ClientToServer.values();

    public static ClientToServer fromId(int value) {
        return messages[value];
    }

    public static int getId(ClientToServer msg) {
        return msg.value;
    }
}