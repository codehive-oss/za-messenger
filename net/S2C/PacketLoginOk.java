package net.S2C;

import net.*;

public class PacketLoginOk extends Packet {
    public String username;

    public PacketLoginOk(String _username) {
        this.username = _username;
    }

    public int getPacketId() {
        return ServerToClient.getId(ServerToClient.LOGIN_OK);
    }
}
