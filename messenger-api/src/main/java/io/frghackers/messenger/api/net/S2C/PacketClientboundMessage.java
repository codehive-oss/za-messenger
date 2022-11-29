package io.frghackers.messenger.api.net.S2C;

import io.frghackers.messenger.api.net.FriendlyBuffer;
import io.frghackers.messenger.api.net.Packet;

public abstract class PacketClientboundMessage extends Packet {
    public String author;

    public PacketClientboundMessage() {
        super();
    }

    public PacketClientboundMessage(String _author) {
        super();
        this.author = _author;
    }


    public static class Text extends PacketClientboundMessage {

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

    public static class Image extends PacketClientboundMessage {

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
