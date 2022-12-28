package io.frghackers.messenger.api.netzklassen;

import io.frghackers.messenger.api.net.FriendlyBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * An abstract class which takes care of handshaking and storing WebSocket connections. Message handling has to be implemented by the subclass.
 */
public abstract class Server extends WebSocketServer {

    private final Set<WebSocket> connections;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int port;

    public Server(int port) {
        super(new InetSocketAddress("0.0.0.0", port));
        this.port = port;
        connections = new HashSet<>();
        this.start();
    }

    @Override
    public void onStart() {
        logger.info("WebSocket Server started on port" + port);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake request) {
        connections.add(conn);
        processNewConnection(
                conn.getRemoteSocketAddress().getHostName(),
                conn.getRemoteSocketAddress().getPort());
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        processMessage(
                conn.getRemoteSocketAddress().getHostName(),
                conn.getRemoteSocketAddress().getPort(),
                new FriendlyBuffer(message)
        );
        super.onMessage(conn, message);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        processNewConnection(
                conn.getRemoteSocketAddress().getHostName(),
                conn.getRemoteSocketAddress().getPort());
        connections.add(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error(ex.getMessage());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        processClosingConnection(
                conn.getRemoteSocketAddress().getHostName(),
                conn.getRemoteSocketAddress().getPort());
        connections.remove(conn);
    }

    public abstract void processNewConnection(String ip, int port);

    public abstract void processMessage(String ip, int port, FriendlyBuffer message);

    public abstract void processClosingConnection(String ip, int port);

    protected boolean isOpen() {
        return true;
    }

    protected Optional<WebSocket> getConnection(String ip, int port) {
        return connections.stream().filter(webSocket -> webSocket.getRemoteSocketAddress().equals(new InetSocketAddress(ip, port))).findAny();
    }

    private void send(WebSocket webSocket, byte[] data) {
        webSocket.send(data);
    }

    protected void send(String ip, int port, FriendlyBuffer message) {
        logger.info("Sending to: " +ip + ":" + port);
        getConnection(ip, port).ifPresent(webSocket -> send(webSocket, message.toByteArray()));
    }

    protected void sendToAll(FriendlyBuffer message) {
        connections.forEach(webSocket -> send(webSocket, message.toByteArray()));
    }

    protected void closeConnection(String ip, int port) {
        getConnection(ip, port).ifPresent(webSocket -> this.onClose(webSocket, 0, "", false));
    }
}
