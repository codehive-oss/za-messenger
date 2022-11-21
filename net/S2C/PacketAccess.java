package net.S2C;

import net.*;

public class PacketAccess extends Packet {
  public String username;

  public PacketAccess(String _username) { this.username = _username; }
}
