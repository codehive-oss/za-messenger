package net.C2S;

import net.*;

public class PacketRegister extends Packet {
    public String username;
    public String password;

    public PacketRegister() {
        super();
    }

    public PacketRegister(String _username, String _password) {
        this.username = _username;
        this.password = _password;
    }

    public void serialize(FriendlyBuffer _buffer) {
        _buffer.putString(username);
        _buffer.putString(password);
    }

    public void deserialize(FriendlyBuffer _buffer) {
        this.username = _buffer.getString();
        this.password = _buffer.getString();
    }

    public int getPacketId() {
        return ClientToServer.getId(ClientToServer.REGISTER);
    }
}
