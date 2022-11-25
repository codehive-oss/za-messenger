package net.S2C;

import net.*;

public class PacketText extends Packet {
    public String author;
    public String message;

    public PacketText() {
        super();
    }

    public PacketText(String _author, String _message) {
        this.author = _author;
        this.message = _message;
    }

    public void serialize(FriendlyBuffer _buffer) {
        _buffer.putString(author);
        _buffer.putString(message);
    }

    public void deserialize(FriendlyBuffer _buffer) {
        this.author = _buffer.getString();
        this.message = _buffer.getString();
    }


    public int getPacketId() {
        return ServerToClient.getId(ServerToClient.TEXT);
    }
}
