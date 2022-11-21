package net.C2S;

import net.*;

public class PacketRegister extends Packet {
  public String username;
  public String password;

  public PacketRegister(String _username, String _password) {
    this.username = _username;
    this.password = _password;
  }
}
