package io.frghackers.messenger.server;

import io.frghackers.messenger.api.net.C2S.ClientToServer;
import io.frghackers.messenger.api.net.C2S.PacketLogin;
import io.frghackers.messenger.api.net.C2S.PacketRegister;
import io.frghackers.messenger.api.net.C2S.PacketServerboundMessage;
import io.frghackers.messenger.api.net.FriendlyBuffer;
import io.frghackers.messenger.api.net.S2C.*;
import io.frghackers.messenger.api.netzklassen.List;
import io.frghackers.messenger.api.netzklassen.Server;
import io.frghackers.messenger.server.db.Argon2;
import io.frghackers.messenger.server.db.User;
import io.frghackers.messenger.server.db.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Optional;

public class MessengerServer extends Server {
    private static final Logger logger = LoggerFactory.getLogger(MessengerServer.class);

    // TODO: Convert this to a hashmap of client ids and member data
    private List<Member> members;
    private final UserRepository userRepository;

    public MessengerServer() {
        this(20017);
    }

    public MessengerServer(int _port) {
        super(_port);
        if (!isOpen()) {
            JOptionPane.showMessageDialog(null, "Fehler beim Starten des Servers auf Port 20017!");
            System.exit(1);
        } else {
            members = new List<>();
            logger.info("Server ist gestartet auf Port " + _port + ".");
        }

        userRepository = new UserRepository();
        userRepository.createTable();
        logger.info("Datenbank initialisiert");
    }

    @Override
    public void processNewConnection(String _clientIp, int _clientPort) {
        PacketWelcome welcome = new PacketWelcome("Willkommen auf dem Messenger-Server!");
        send(
                _clientIp,
                _clientPort,
                new FriendlyBuffer().putInt(welcome.getPacketId()).putPacketData(welcome));
    }

    @Override
    public synchronized void processMessage(
            String _clientIp, int _clientPort, FriendlyBuffer _buffer) {
        ClientToServer msgId = ClientToServer.fromId(_buffer.getInt());
        logger.info("Server:" + msgId);

        if (!isClientLoggedIn(_clientIp, _clientPort)) {
            switch (msgId) {
                case LOGIN -> {
                    PacketLogin login = _buffer.getPacketData(PacketLogin.class);
                    String name = login.username;
                    String password = login.password;

                    Optional<User> user = userRepository.getUser(name);
                    if (user.isEmpty()) {
                        PacketError error = new PacketError("Benutzer existiert nicht!");
                        send(
                                _clientIp,
                                _clientPort,
                                new FriendlyBuffer()
                                        .putInt(error.getPacketId())
                                        .putPacketData(error));
                    } else {
                        if (!Argon2.INSTANCE.verify(
                                user.get().getPassword(), password.toCharArray())) {
                            PacketError error = new PacketError("Falsches Passwort");
                            send(
                                    _clientIp,
                                    _clientPort,
                                    new FriendlyBuffer()
                                            .putInt(error.getPacketId())
                                            .putPacketData(error));
                        } else {
                            if (isNameUsed(name)) {
                                PacketError error =
                                        new PacketError("Du bist bereits woanders eingeloggt.");
                                send(
                                        _clientIp,
                                        _clientPort,
                                        new FriendlyBuffer()
                                                .putInt(error.getPacketId())
                                                .putPacketData(error));
                            } else {
                                loginMember(_clientIp, _clientPort, name);
                                PacketLoginOk loginOk = new PacketLoginOk(name);
                                send(
                                        _clientIp,
                                        _clientPort,
                                        new FriendlyBuffer()
                                                .putInt(loginOk.getPacketId())
                                                .putPacketData(loginOk));
                            }
                        }
                    }
                }

                case REGISTER -> {
                    PacketRegister register = _buffer.getPacketData(PacketRegister.class);
                    String name = register.username;
                    String password = register.password;

                    if (userRepository.createUser(name, Argon2.hash(password))) {
                        // TODO: Create a packet for register ok
                        PacketError error =
                                new PacketError("Benutzer " + name + " erfolgreich erstellt");
                        send(
                                _clientIp,
                                _clientPort,
                                new FriendlyBuffer()
                                        .putInt(error.getPacketId())
                                        .putPacketData(error));
                    } else {
                        PacketError error = new PacketError("Fehler bei der Registrierung");
                        send(
                                _clientIp,
                                _clientPort,
                                new FriendlyBuffer()
                                        .putInt(error.getPacketId())
                                        .putPacketData(error));
                    }
                }
                default -> {
                    PacketError error = new PacketError("Sie sind nicht loggedIn!");
                    send(
                            _clientIp,
                            _clientPort,
                            new FriendlyBuffer().putInt(error.getPacketId()).putPacketData(error));
                }
            }
        } else {
            switch (msgId) {
                case LOGIN -> {
                    PacketError error = new PacketError("Sie sind bereits loggedIn!");
                    send(
                            _clientIp,
                            _clientPort,
                            new FriendlyBuffer().putInt(error.getPacketId()).putPacketData(error));
                }
                case GIVE_ALL_MEMBER -> {
                    PacketAllMembers allMembers =
                            new PacketAllMembers(getAllMember().toArray(new String[0]));
                    send(
                            _clientIp,
                            _clientPort,
                            new FriendlyBuffer()
                                    .putInt(allMembers.getPacketId())
                                    .putPacketData(allMembers));
                }
                case SEND_NAME_TO_ALL -> {
                    PacketAccess access =
                            new PacketAccess(memberFromIpAndPort(_clientIp, _clientPort));
                    sendToAll(
                            new FriendlyBuffer()
                                    .putInt(access.getPacketId())
                                    .putPacketData(access));
                }
                case LOGOUT -> {
                    PacketExit exit = new PacketExit(memberFromIpAndPort(_clientIp, _clientPort));
                    sendToAll(new FriendlyBuffer().putInt(exit.getPacketId()).putPacketData(exit));
                    logoutMember(_clientIp, _clientPort);
                    closeConnection(_clientIp, _clientPort);
                }
                case MESSAGE_TEXT -> {
                    PacketServerboundMessage.Text message = _buffer.getPacketData(PacketServerboundMessage.Text.class);

                    String[] receivers = message.receivers;
                    String content = message.message;

                    for (String s : receivers) {
                        String receiverIp = findIpFromMember(s);
                        int reciverPort = findPortFromMember(s);
                        String senderName = memberFromIpAndPort(_clientIp, _clientPort);

                        PacketClientboundMessage.Text text = new PacketClientboundMessage.Text(senderName, content);
                        send(
                                receiverIp,
                                reciverPort,
                                new FriendlyBuffer()
                                        .putInt(text.getPacketId())
                                        .putPacketData(text));
                    }
                }
                case MESSAGE_IMAGE -> {
                    PacketServerboundMessage.Image message = _buffer.getPacketData(PacketServerboundMessage.Image.class);

                    String[] receivers = message.receivers;
                    byte[] imageData = message.imageData;


                    for (String receiver : receivers) {
                        String receiverIp = findIpFromMember(receiver);
                        int receiverPort = findPortFromMember(receiver);
                        String senderName = memberFromIpAndPort(_clientIp, _clientPort);

                        PacketClientboundMessage.Image msg = new PacketClientboundMessage.Image(senderName, imageData);
                        send(receiverIp, receiverPort, new FriendlyBuffer()
                                .putInt(msg.getPacketId())
                                .putPacketData(msg));
                    }
                }
            }
        }
    }

    @Override
    public synchronized void processClosingConnection(String _clientIp, int _clientPort) {
        if (isConnectedTo(_clientIp, _clientPort)) {
            send(_clientIp, _clientPort, new FriendlyBuffer().putInt(ServerToClient.BYE.getId()));
        }
    }

    private void loginMember(String _clientIp, int _clientPort, String pName) {
        Member newMember = new Member(_clientIp, _clientPort, pName);
        newMember.setLoggedIn(true);
        members.append(newMember);
    }

    private void logoutMember(String _clientIp, int _clientPort) {
        boolean found = false;
        members.toFirst();
        while (members.hasAccess() && !found) {
            if (members.getContent().getIp().equals(_clientIp)
                    && members.getContent().getPort() == _clientPort) {
                members.getContent().setLoggedIn(false);
                members.getContent().setName(null);
                members.remove();
                found = true;
            } else {
                members.next();
            }
        }
    }

    private boolean isClientLoggedIn(String _clientIp, int _clientPort) {
        boolean found = false;
        boolean loggedIn = false;
        members.toFirst();
        while (members.hasAccess() && !found) {
            if (members.getContent().getIp().equals(_clientIp)
                    && members.getContent().getPort() == _clientPort) {
                loggedIn = members.getContent().isLoggedIn();
                found = true;
            } else {
                members.next();
            }
        }
        return loggedIn;
    }

    private boolean isNameUsed(String pName) {
        boolean found = false;
        members.toFirst();
        while (members.hasAccess() && !found) {
            if (members.getContent().getName().equals(pName)) {
                found = true;
            } else {
                members.next();
            }
        }
        return found;
    }

    private String findIpFromMember(String pName) {
        String ipAdresse = null;
        boolean found = false;
        members.toFirst();
        while (members.hasAccess() && !found) {
            if (members.getContent().getName().equals(pName)) {
                ipAdresse = members.getContent().getIp();
                found = true;
            } else {
                members.next();
            }
        }
        return ipAdresse;
    }

    private int findPortFromMember(String pName) {
        int port = -1;
        boolean found = false;
        members.toFirst();
        while (members.hasAccess() && !found) {
            if (members.getContent().getName().equals(pName)) {
                port = members.getContent().getPort();
                found = true;
            } else {
                members.next();
            }
        }
        return port;
    }

    private String memberFromIpAndPort(String _clientIp, int _clientPort) {
        String foundMember = null;
        boolean found = false;
        members.toFirst();
        while (members.hasAccess() && !found) {
            if (members.getContent().getIp().equals(_clientIp)
                    && members.getContent().getPort() == _clientPort) {
                foundMember = members.getContent().getName();
                found = true;
            } else {
                members.next();
            }
        }
        return foundMember;
    }

    private ArrayList<String> getAllMember() {
        ArrayList<String> allMembers = new ArrayList<String>();

        members.toFirst();
        while (members.hasAccess()) {
            allMembers.add(members.getContent().getName());
            members.next();
        }
        return allMembers;
    }

    public static void main(String[] args) {
        new MessengerServer();
    }
}
