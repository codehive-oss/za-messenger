package io.frghackers.messenger.api.net.C2S;

import io.frghackers.messenger.api.net.FriendlyBuffer;
import io.frghackers.messenger.api.net.Packet;

public abstract class PacketServerboundMessage extends Packet {
    public String[] receivers;

    public PacketServerboundMessage() {
        super();
    }

    public PacketServerboundMessage(String[] receivers) {
        this.receivers = receivers;
    }

    public static class Text extends PacketServerboundMessage {

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

    public static class Image extends PacketServerboundMessage {

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
