package io.frghackers.messenger.api.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class FriendlyBuffer {
    private static final Logger logger = LoggerFactory.getLogger(FriendlyBuffer.class);

    public ByteBuffer buffer;
    public int usedSize;
    public int allocatedSize;

    public FriendlyBuffer() {
        this(0);
    }

    public FriendlyBuffer(int _size) {
        buffer = ByteBuffer.allocate(_size);
        this.allocatedSize = _size;
    }

    public FriendlyBuffer(byte[] _data) {
        this.buffer = ByteBuffer.wrap(_data);
        this.allocatedSize = _data.length;
    }

    public FriendlyBuffer(ByteBuffer _buffer) {
        this.buffer = _buffer;
        this.allocatedSize = _buffer.array().length;
    }

    public boolean checkSize(int _bytes) {
        return buffer.remaining() >= _bytes;
    }

    public void upgradeSize(int _bytes) {
        if (!checkSize(_bytes)) {
            allocatedSize += _bytes;
            allocatedSize += allocatedSize / 2;
            ByteBuffer newBuffer = ByteBuffer.allocate(allocatedSize);

            newBuffer.put(buffer.array());
            newBuffer.position(usedSize);
            buffer = newBuffer;
        }
        usedSize += _bytes;
    }

    public FriendlyBuffer putInt(int _value) {
        upgradeSize(Integer.SIZE / 8);
        buffer.putInt(_value);
        return this;
    }

    public FriendlyBuffer putIntArray(int[] _values) {
        putInt(_values.length);
        for (int i = 0; i < _values.length; i++) {
            putInt(_values[i]);
        }
        return this;
    }

    public FriendlyBuffer putFloat(float _value) {
        upgradeSize(Float.SIZE / 8);
        buffer.putDouble(_value);
        return this;
    }

    public FriendlyBuffer putFloatArray(float[] _values) {
        putInt(_values.length);
        for (int i = 0; i < _values.length; i++) {
            putFloat(_values[i]);
        }
        return this;
    }

    public FriendlyBuffer putDouble(double _value) {
        upgradeSize(Double.SIZE / 8);
        buffer.putDouble(_value);
        return this;
    }

    public FriendlyBuffer putDoubleArray(double[] _values) {
        putInt(_values.length);
        for (int i = 0; i < _values.length; i++) {
            putDouble(_values[i]);
        }
        return this;
    }

    public FriendlyBuffer put(byte[] _value) {
        putInt(_value.length);

        upgradeSize(_value.length);
        buffer.put(_value);

        return this;
    }

    public FriendlyBuffer putString(String _value) {
        put(_value.getBytes());
        return this;
    }

    public FriendlyBuffer putStringArray(String[] _values) {
        putInt(_values.length);
        for (int i = 0; i < _values.length; i++) {
            putString(_values[i]);
        }
        return this;
    }

    public <T extends Packet> FriendlyBuffer putPacketData(T _data) {
        _data.serialize(this);
        return this;
    }

    public int getInt() {
        return buffer.getInt();
    }

    public int[] getIntArray() {
        int length = getInt();
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = getInt();
        }
        return result;
    }

    public float getFloat() {
        return buffer.getFloat();
    }

    public float[] getFloatArray() {
        int length = getInt();
        float[] result = new float[length];

        for (int i = 0; i < length; i++) {
            result[i] = getFloat();
        }
        return result;
    }

    public double getDouble() {
        return buffer.getDouble();
    }

    public double[] getDoubleArray() {
        int length = getInt();
        double[] result = new double[length];

        for (int i = 0; i < length; i++) {
            result[i] = getDouble();
        }
        return result;
    }

    public byte[] get() {
        int length = getInt();
        byte[] result = new byte[length];
        buffer.get(result);

        return result;
    }

    public String getString() {
        byte[] result = get();
        return new String(result);
    }

    public String[] getStringArray() {
        int length = getInt();
        String[] result = new String[length];

        for (int i = 0; i < length; i++) {
            result[i] = getString();
        }
        return result;
    }

    public <T extends Packet> T getPacketData(Class<T> c) {
        try {
            T data = c.getDeclaredConstructor().newInstance();
            data.deserialize(this);
            return data;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

    public byte[] toByteArray() {
        return Arrays.copyOfRange(buffer.array(), 0, usedSize);
    }
}
