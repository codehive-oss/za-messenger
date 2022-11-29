package net.S2C;

import net.*;

public abstract class PacketMessage extends Packet {
    public String author;

    public PacketMessage() {
        super();
    }

    public PacketMessage(String _author) {
        super();
        this.author = _author;
    }


    public static class Text extends PacketMessage {

        public String message;

        public Text() {
            super();
        }

        public Text(String _author, String _message) {
            super(_author);
            this.message = _message;
        }

        public void serialize(FriendlyBuffer _buffer) {
            _buffer.putString(author);
            _buffer.putString(message);
        }

        public void deserialize(FriendlyBuffer _buffer) {
            this.author = _buffer.getString();
            this.message = _buffer.getString();
        }

        @Override
        public int getPacketId() {
            return ServerToClient.MESSAGE_TEXT.getId();
        }
    }

    public static class Image extends PacketMessage {

        public byte[] imageData;

        public Image() {
            super();
        }

        public Image(String _author, byte[] imageData) {
            super(_author);
            this.imageData = imageData;
        }

        public void serialize(FriendlyBuffer _buffer) {
            _buffer.putString(author);
            _buffer.put(imageData);
        }

        public void deserialize(FriendlyBuffer _buffer) {
            this.author = _buffer.getString();
            this.imageData = _buffer.get();
        }

        @Override
        public int getPacketId() {
            return ServerToClient.MESSAGE_IMAGE.getId();
        }

    }

}
