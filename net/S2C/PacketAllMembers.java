package net.S2C;

import java.nio.ByteBuffer;
import net.*;

public class PacketAllMembers extends Packet {
  public String[] usernames;

  public PacketAllMembers(String[] _usernames) { usernames = _usernames; }
}
