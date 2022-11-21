package net.S2C;

import net.*;

public class PacketWelcome extends Packet {
    public String message;

    public PacketWelcome(String _message) {
        this.message = _message;
    }
}
