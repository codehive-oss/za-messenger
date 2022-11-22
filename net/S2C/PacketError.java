package net.S2C;

import net.*;

public class PacketError extends Packet {
    public String message;

    public PacketError(String _message) {
        this.message = _message;
    }

    public int getPacketId() {
        return ServerToClient.getId(ServerToClient.ERROR);
    }
}
