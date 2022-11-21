package net;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class StringSerializer {
  public static String readBuffer(ByteBuffer _buffer) {
    int length = _buffer.getInt();
    String value = new String(_buffer.array());

    return value.substring(0, length);
  }

  public static void writeBuffer(ObjectOutputStream _buffer, String value) {
    try {
      _buffer.writeInt(value.length());
      _buffer.write(value.getBytes());
    } catch (Exception e) {
    }
  }
}
