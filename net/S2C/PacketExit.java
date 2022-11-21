package net.S2C;

import net.*;

public class PacketExit extends Packet {
    public String username;

    public PacketExit(String _username) {
        this.username = _username;
    }
}
