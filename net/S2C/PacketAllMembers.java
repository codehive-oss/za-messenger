package net.S2C;

import net.*;

public class PacketAllMembers extends Packet {
    public String[] usernames;

    public PacketAllMembers(String[] _usernames) {
        usernames = _usernames;
    }

    public int getPacketId() {
        return ServerToClient.getId(ServerToClient.ALL_MEMBERS);
    }
}