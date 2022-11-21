package net.S2C;

import net.*;

public class PacketLoginOk extends Packet {
  public String username;

  public PacketLoginOk(String _username) { this.username = _username; }
}
