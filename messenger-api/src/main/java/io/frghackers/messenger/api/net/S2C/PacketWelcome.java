package io.frghackers.messenger.api.net.S2C;

import io.frghackers.messenger.api.net.FriendlyBuffer;
import io.frghackers.messenger.api.net.Packet;

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
