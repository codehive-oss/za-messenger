package net.C2S;

import net.*;

public class PacketMessage extends Packet {
    public String[] receivers;
    public String message;

    public PacketMessage() {
        super();
    }

    public PacketMessage(String[] _receivers, String _message) {
        this.receivers = _receivers;
        this.message = _message;
    }

    public void serialize(FriendlyBuffer _buffer) {
        _buffer.putStringArray(receivers);
        _buffer.putString(message);
    }

    public void deserialize(FriendlyBuffer _buffer) {
        this.receivers = _buffer.getStringArray();
        this.message = _buffer.getString();
    }

    public int getPacketId() {
        return ClientToServer.getId(ClientToServer.MESSAGE);
    }
}
