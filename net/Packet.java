package net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Packet implements Serializable {
  private static final Logger logger = LoggerFactory.getLogger(Packet.class);

  public static byte[] serialize(int _packetId) {
    return serialize(_packetId, null);
  }

  public static <T extends Packet> byte[] serialize(int _packetId, T _obj) {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = null;
      try {
        if (_obj != null) {
          out = new ObjectOutputStream(bos);
          out.writeObject(_obj);
          out.flush();
          out.close();

          byte[] data = bos.toByteArray();

          ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
          buffer.putInt(_packetId);
          buffer.put(data);

          return buffer.array();
        } else {
          ByteBuffer buffer = ByteBuffer.allocate(4);
          buffer.putInt(_packetId);
          return buffer.array();
        }
      } finally {
        bos.close();
      }
    } catch (Exception e) {
      logger.error(e.toString());
    }

    return null;
  }

  public static <T extends Packet> T deserialize(byte[] _data) {
    try {
      byte[] objectData = Arrays.copyOfRange(_data, 4, _data.length);

      ByteArrayInputStream bis = new ByteArrayInputStream(objectData);
      ObjectInputStream in = new ObjectInputStream(bis);
      try {
        T obj = (T)in.readObject();
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
}
