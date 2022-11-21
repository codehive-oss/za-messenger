package net.S2C;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import net.*;

public class PacketWelcome extends Packet {
  public String message;

  public PacketWelcome(String _message) { this.message = _message; }
}
