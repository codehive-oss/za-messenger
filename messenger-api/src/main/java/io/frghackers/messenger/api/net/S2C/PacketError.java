package io.frghackers.messenger.api.net.S2C;

import io.frghackers.messenger.api.net.FriendlyBuffer;
import io.frghackers.messenger.api.net.Packet;

public class PacketError extends Packet {
    public String message;

    public PacketError() {
        super();
    }

    public PacketError(String _message) {
        this.message = _message;
    }

    public void serialize(FriendlyBuffer _buffer) {
        _buffer.putString(message);
    }

    public void deserialize(FriendlyBuffer _buffer) {
        this.message = _buffer.getString();
    }

    public int getPacketId() {
        return ServerToClient.getId(ServerToClient.ERROR);
    }
}
