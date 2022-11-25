package net.S2C;

import net.*;

public class PacketExit extends Packet {
    public String username;

    public PacketExit() {
        super();
    }

    public PacketExit(String _username) {
        this.username = _username;
    }

    public void serialize(FriendlyBuffer _buffer) {
        _buffer.putString(username);
    }

    public void deserialize(FriendlyBuffer _buffer) {
        this.username = _buffer.getString();
    }


    public int getPacketId() {
        return ServerToClient.getId(ServerToClient.EXIT);
    }
}
