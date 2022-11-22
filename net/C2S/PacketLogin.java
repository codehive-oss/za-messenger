package net.C2S;

import net.*;

public class PacketLogin extends Packet {
    public String username;
    public String password;

    public PacketLogin(String _username, String _password) {
        this.username = _username;
        this.password = _password;
    }

    public int getPacketId() {
        return ClientToServer.getId(ClientToServer.LOGIN);
    }
}
