package io.frghackers.messenger.api.netzklassen;

import io.frghackers.messenger.api.net.FriendlyBuffer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;


public abstract class Client extends WebSocketClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Client(String pServerIP, int pServerPort) {
        super(URI.create("ws://%s:%d".formatted(pServerIP, pServerPort)));
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connection opened");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        logger.info("new bmsg: " + bytes.toString());
        processMessage(new FriendlyBuffer(bytes));
    }

    @Override
    public void onMessage(String message) {
        logger.info("new msg: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {

    }

    public boolean isConnected() {
        return true;
    }

    public void send(FriendlyBuffer _buffer) {
        send(_buffer.toByteArray());
    }

    public void send(byte[] pMessage) {
        super.send(pMessage);
    }

    public void close() {
        super.close();
    }

    public abstract void processMessage(FriendlyBuffer _message);
}
