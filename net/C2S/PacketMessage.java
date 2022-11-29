package net.C2S;

import net.*;

public abstract class PacketMessage extends Packet {
    public String[] receivers;

    public PacketMessage() {
        super();
    }

    public PacketMessage(String[] receivers) {
        this.receivers = receivers;
    }

    public static class Text extends PacketMessage {

        public String message;

        public Text() {
            super();
        }

        public Text(String[] _receivers, String _message) {
            super(_receivers);
            this.message = _message;
        }

        public void serialize(FriendlyBuffer _buffer) {
            _buffer.putStringArray(receivers);
            _buffer.putString(message);
        }

        public void deserialize(FriendlyBuffer _buffer) {
            this.receivers = _buffer.getStringArray();
            this.message = _buffer.getString();
        }

        public int getPacketId() {
            return ClientToServer.getId(ClientToServer.MESSAGE_TEXT);
        }
    }

    public static class Image extends PacketMessage {

        public byte[] imageData;

        public Image() {
            super();
        }

        public Image(String[] _receivers, byte[] _imageData) {
            this.receivers = _receivers;
            this.imageData = _imageData;
        }

        @Override
        public void serialize(FriendlyBuffer _buffer) {
            _buffer.putStringArray(receivers);
            _buffer.put(imageData);
        }

        @Override
        public void deserialize(FriendlyBuffer _buffer) {
            this. receivers = _buffer.getStringArray();
            this.imageData = _buffer.get();
        }

        @Override
        public int getPacketId() {
            return ClientToServer.getId(ClientToServer.MESSAGE_IMAGE);
        }
    }

}
