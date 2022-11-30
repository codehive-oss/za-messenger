package io.frghackers.messenger.api.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Packet {
    private static final Logger logger = LoggerFactory.getLogger(Packet.class);

    public Packet() {
    }

    public abstract void serialize(FriendlyBuffer _buffer);

    public abstract void deserialize(FriendlyBuffer _buffer);

    public abstract int getPacketId();
}
