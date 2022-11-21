package net.S2C;

public enum ServerToClient {
  WELCOME,
  LOGIN_OK,
  ACCESS,
  ALL_MEMBERS,
  TEXT,
  BYE,
  EXIT,
  ERROR;

  public final int value = ordinal();
  public static ServerToClient[] messages =
      ServerToClient.values();

  public static ServerToClient fromId(int value) {
    return messages[value];
  }

  public static int getId(ServerToClient msg) { return msg.value; }
}
