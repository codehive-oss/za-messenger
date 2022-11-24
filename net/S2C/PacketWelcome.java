package net.S2C;

import net.*;

public class PacketWelcome extends Packet {
    public String message;

    public PacketWelcome() {
        super();
    }

    public PacketWelcome(String _message) {
        this.message = _message;
    }

    public void serialize(FriendlyBuffer _buffer) {
        _buffer.putString(message);
    }

    public void deserialize(FriendlyBuffer _buffer) {
        this.message = _buffer.getString();
    }

    public int getPacketId() {
        return ServerToClient.getId(ServerToClient.WELCOME);
    }
}
