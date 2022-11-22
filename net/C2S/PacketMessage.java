package net.C2S;

import net.*;

public class PacketMessage extends Packet {
    public String[] receivers;
    public String message;

    public PacketMessage(String[] _receivers, String _message) {
        this.receivers = _receivers;
        this.message = _message;
    }

    public int getPacketId() {
        return ClientToServer.getId(ClientToServer.MESSAGE);
    }
}
