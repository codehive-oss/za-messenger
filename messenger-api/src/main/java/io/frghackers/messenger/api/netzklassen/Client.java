package io.frghackers.messenger.api.netzklassen;

import io.frghackers.messenger.api.net.FriendlyBuffer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Materialien zu den zentralen NRW-Abiturpruefungen im Fach Informatik ab 2018
 *
 * <p>Klasse Client
 *
 * <p>Objekte von Unterklassen der abstrakten Klasse Client ermoeglichen Netzwerkverbindungen zu
 * einem Server mittels TCP/IP-Protokoll. Nach Verbindungsaufbau koennen Zeichenketten (Strings) zum
 * Server gesendet und von diesem empfangen werden, wobei der Nachrichtenempfang nebenlaeufig
 * geschieht. Zur Vereinfachung finden Nachrichtenversand und -empfang zeilenweise statt, d. h.,
 * beim Senden einer Zeichenkette wird ein Zeilentrenner ergaenzt und beim Empfang wird dieser
 * entfernt. Jede empfangene Nachricht wird einer Ereignisbehandlungsmethode uebergeben, die in
 * Unterklassen implementiert werden muss. Es findet nur eine rudimentaere Fehlerbehandlung statt,
 * so dass z.B. Verbindungsabbrueche nicht zu einem Programmabbruch fuehren. Eine einmal
 * unterbrochene oder getrennte Verbindung kann nicht reaktiviert werden.
 *
 * @author Qualitaets- und UnterstuetzungsAgentur - Landesinstitut fuer Schule
 * @version 30.08.2016
 */
public abstract class Client {
    private MessageHandler messageHandler;

    private class MessageHandler extends Thread {
        private SocketWrapper socketWrapper;
        private boolean active;

        private class SocketWrapper {
            private Socket socket;
            private DataInputStream fromServer;
            private DataOutputStream toServer;

            public SocketWrapper(String pServerIP, int pServerPort) {
                try {
                    socket = new Socket(pServerIP, pServerPort);
                    toServer = new DataOutputStream(socket.getOutputStream());
                    fromServer = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    socket = null;
                    toServer = null;
                    fromServer = null;
                }
            }

            public byte[] receive() {
                try {
                    int length = fromServer.readInt();
                    byte[] data = new byte[length];
                    fromServer.readFully(data);

                    return data;
                } catch (IOException e) {
                }

                return null;
            }

            public void send(byte[] data) {
                int len = data.length;

                if (len <= 0) {
                    throw new IllegalArgumentException("Data needs to have some data");
                }

                try {
                    // write the length of the buffer
                    toServer.writeInt(len);

                    // Adjust the start index when needed
                    toServer.write(data, 0, len);
                } catch (IOException e) {
                }
            }

            public void close() {
                if (socket != null)
                    try {
                        socket.close();
                    } catch (IOException e) {
                        /*
                         * Falls eine Verbindung getrennt werden soll, deren Endpunkt
                         * nicht mehr existiert bzw. ihrerseits bereits beendet worden ist,
                         * geschieht nichts.
                         */
                    }
            }
        }

        private MessageHandler(String pServerIP, int pServerPort) {
            socketWrapper = new SocketWrapper(pServerIP, pServerPort);
            start();
            if (socketWrapper.socket != null) active = true;
        }

        public void run() {
            byte[] message;
            while (active) {
                message = socketWrapper.receive();
                processMessage(new FriendlyBuffer(message));
            }
        }

        private void send(byte[] pMessage) {
            if (active) socketWrapper.send(pMessage);
        }

        private void close() {
            if (active) {
                active = false;
                socketWrapper.close();
            }
        }
    }

    public Client(String pServerIP, int pServerPort) {
        messageHandler = new MessageHandler(pServerIP, pServerPort);
    }

    public boolean isConnected() {
        return (messageHandler.active);
    }

    public void send(FriendlyBuffer _buffer) {
        send(_buffer.toByteArray());
    }

    public void send(byte[] pMessage) {
        messageHandler.send(pMessage);
    }

    public void close() {
        messageHandler.close();
    }

    public abstract void processMessage(FriendlyBuffer _message);
}
