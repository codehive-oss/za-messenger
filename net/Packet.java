package net;

import net.C2S.*;
import net.S2C.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class Packet {
    private static final Logger logger = LoggerFactory.getLogger(Packet.class);

    public Packet() {}

    public abstract void serialize(FriendlyBuffer _buffer);
    public abstract void deserialize(FriendlyBuffer _buffer);

    public abstract int getPacketId();
}
