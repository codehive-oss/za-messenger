package net;

import net.C2S.*;
import net.S2C.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class Packet implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Packet.class);

    public static <T extends Packet> byte[] serialize(T _obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(_obj);
                out.flush();
                out.close();

                byte[] data = bos.toByteArray();

                ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
                buffer.putInt(_obj.getPacketId());
                buffer.put(data);

                return buffer.array();
            } finally {
                bos.close();
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;
    }

    public static byte[] serialize(int _packetId) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            try {
                ByteBuffer buffer = ByteBuffer.allocate(4);
                buffer.putInt(_packetId);
                return buffer.array();
            } finally {
                bos.close();
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;
    }

    public static byte[] serialize(ClientToServer _packetId) {
        return serialize(ClientToServer.getId(_packetId));
    }

    public static byte[] serialize(ServerToClient _packetId) {
        return serialize(ServerToClient.getId(_packetId));
    }

    public static <T extends Packet> T deserialize(byte[] _data) {
        try {
            byte[] objectData = Arrays.copyOfRange(_data, 4, _data.length);

            ByteArrayInputStream bis = new ByteArrayInputStream(objectData);
            ObjectInputStream in = new ObjectInputStream(bis);
            try {
                T obj = (T) in.readObject();
                return obj;
            } finally {
                if (in != null) {
                    in.close();
                }
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;
    }

    public static <T extends Packet> T deserialize(ByteBuffer _buffer) {
        return deserialize(_buffer.array());
    }

    public abstract int getPacketId();
}
