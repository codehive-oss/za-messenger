package net.S2C;

import net.*;

public class PacketText extends Packet {
  public String author;
  public String message;

  public PacketText(String _author, String _message) {
    this.author = _author;
    this.message = _message;
  }
}
