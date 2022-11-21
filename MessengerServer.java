import db.Argon2;
import db.User;
import db.UserRepository;

import net.*;
import net.C2S.*;
import net.S2C.*;

import netzklassen.List;
import netzklassen.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.*;

/**
 * Ein Messenger-Server
 *
 * @author QUA-LiS NRW
 * @version 1.0
 */
public class MessengerServer extends Server {
    private static final Logger logger = LoggerFactory.getLogger(MessengerServer.class);

    private List<Teilnehmer> members;
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
            logger.info("Server ist gestartet.");
        }

        userRepository = new UserRepository();
        userRepository.createTable();
        logger.info("Datenbank initialisiert");
    }

    @Override
    public void processNewConnection(String _clientIp, int _clientPort) {
        PacketWelcome welcome = new PacketWelcome("Willkommen auf dem Messenger-Server!");
        byte[] welcomeData =
                Packet.serialize(ServerToClient.getId(ServerToClient.WELCOME), welcome);
        send(_clientIp, _clientPort, welcomeData);
    }

    @Override
    public synchronized void processMessage(String _clientIp, int _clientPort, ByteBuffer _buffer) {
        ClientToServer msgId = ClientToServer.fromId(_buffer.getInt());
        logger.info("Server:" + msgId);

        if (!istTeilnehmerAngemeldet(_clientIp, _clientPort)) {
            switch (msgId) {
                case LOGIN -> {
                    PacketLogin login = Packet.deserialize(_buffer.array());
                    String name = login.username;
                    String password = login.password;

                    Optional<User> user = userRepository.getUser(name);
                    if (user.isEmpty()) {
                        PacketError error = new PacketError("Benutzer existiert nicht!");
                        byte[] errorData =
                                Packet.serialize(ServerToClient.getId(ServerToClient.ERROR), error);
                        send(_clientIp, _clientPort, errorData);
                    } else {
                        if (!Argon2.INSTANCE.verify(
                                user.get().getPassword(), password.toCharArray())) {
                            PacketError error = new PacketError("Falsches Passwort");
                            byte[] errorData =
                                    Packet.serialize(
                                            ServerToClient.getId(ServerToClient.ERROR), error);
                            send(_clientIp, _clientPort, errorData);
                        } else {
                            if (istNameVergeben(name)) {
                                PacketError error =
                                        new PacketError("Du bist bereits woanders eingeloggt.");
                                byte[] errorData =
                                        Packet.serialize(
                                                ServerToClient.getId(ServerToClient.ERROR), error);
                                send(_clientIp, _clientPort, errorData);
                            } else {
                                meldeTeilnehmerAn(_clientIp, _clientPort, name);
                                PacketLoginOk loginOk = new PacketLoginOk(name);
                                byte[] loginOkData =
                                        Packet.serialize(
                                                ServerToClient.getId(ServerToClient.LOGIN_OK),
                                                loginOk);
                                send(_clientIp, _clientPort, loginOkData);
                            }
                        }
                    }
                }

                case REGISTER -> {
                    PacketRegister register = Packet.deserialize(_buffer.array());
                    String name = register.username;
                    String password = register.password;

                    if (userRepository.createUser(name, Argon2.hash(password))) {
                        // TODO: Create a packet for register ok
                        PacketError error =
                                new PacketError("Benutzer " + name + " erfolgreich erstellt");
                        byte[] errorData =
                                Packet.serialize(ServerToClient.getId(ServerToClient.ERROR), error);
                        send(_clientIp, _clientPort, errorData);
                    } else {
                        PacketError error = new PacketError("Fehler bei der Registrierung");
                        byte[] errorData =
                                Packet.serialize(ServerToClient.getId(ServerToClient.ERROR), error);
                        send(_clientIp, _clientPort, errorData);
                    }
                }
                default -> {
                    PacketError error = new PacketError("Sie sind nicht angemeldet!");
                    byte[] errorData =
                            Packet.serialize(ServerToClient.getId(ServerToClient.ERROR), error);
                    send(_clientIp, _clientPort, errorData);
                }
            }
        } else {
            switch (msgId) {
                case LOGIN -> {
                    PacketError error = new PacketError("Sie sind bereits angemeldet!");
                    byte[] errorData =
                            Packet.serialize(ServerToClient.getId(ServerToClient.ERROR), error);
                    send(_clientIp, _clientPort, errorData);
                }
                case GIVE_ALL_MEMBER -> {
                    PacketAllMembers allMembers =
                            new PacketAllMembers(getAllMember().toArray(new String[0]));
                    byte[] allMembersData =
                            Packet.serialize(
                                    ServerToClient.getId(ServerToClient.ALL_MEMBERS), allMembers);
                    send(_clientIp, _clientPort, allMembersData);
                }
                case SEND_NAME_TO_ALL -> {
                    PacketAccess access =
                            new PacketAccess(memberFromIpAndPort(_clientIp, _clientPort));
                    byte[] accessData =
                            Packet.serialize(ServerToClient.getId(ServerToClient.ACCESS), access);
                    sendToAll(accessData);
                }
                case LOGOUT -> {
                    PacketExit exit = new PacketExit(memberFromIpAndPort(_clientIp, _clientPort));
                    byte[] exitData =
                            Packet.serialize(ServerToClient.getId(ServerToClient.EXIT), exit);
                    sendToAll(exitData);
                    meldeTeilnehmerAb(_clientIp, _clientPort);
                    closeConnection(_clientIp, _clientPort);
                }
                case MESSAGE -> {
                    PacketMessage message = Packet.deserialize(_buffer.array());

                    String[] receivers = message.receivers;
                    String content = message.message;

                    for (String s : receivers) {
                        String receiverIp = findeIPAdresseZuTeilnehmer(s);
                        int reciverPort = findePortZuTeilnehmer(s);
                        String senderName = memberFromIpAndPort(_clientIp, _clientPort);

                        PacketText text = new PacketText(senderName, content);
                        byte[] textData =
                                Packet.serialize(ServerToClient.getId(ServerToClient.TEXT), text);
                        send(receiverIp, reciverPort, textData);
                    }
                }
            }
        }
    }

    @Override
    public synchronized void processClosingConnection(String _clientIp, int _clientPort) {
        if (isConnectedTo(_clientIp, _clientPort)) {
            byte[] byeData = Packet.serialize(ServerToClient.getId(ServerToClient.BYE));
            send(_clientIp, _clientPort, byeData);
        }
    }

    private void meldeTeilnehmerAn(String _clientIp, int _clientPort, String pName) {
        Teilnehmer neuerTeilnehmer = new Teilnehmer(_clientIp, _clientPort, pName);
        neuerTeilnehmer.setzeAngemeldet(true);
        members.append(neuerTeilnehmer);
    }

    private void meldeTeilnehmerAb(String _clientIp, int _clientPort) {
        boolean gefunden = false;
        members.toFirst();
        while (members.hasAccess() && !gefunden) {
            if (members.getContent().gibIPAdresse().equals(_clientIp)
                    && members.getContent().gibPort() == _clientPort) {
                members.getContent().setzeAngemeldet(false);
                members.getContent().setzeName(null);
                members.remove();
                gefunden = true;
            } else {
                members.next();
            }
        }
    }

    private boolean istTeilnehmerAngemeldet(String _clientIp, int _clientPort) {
        boolean gefunden = false;
        boolean angemeldet = false;
        members.toFirst();
        while (members.hasAccess() && !gefunden) {
            if (members.getContent().gibIPAdresse().equals(_clientIp)
                    && members.getContent().gibPort() == _clientPort) {
                angemeldet = members.getContent().istAngemeldet();
                gefunden = true;
            } else {
                members.next();
            }
        }
        return angemeldet;
    }

    private boolean istNameVergeben(String pName) {
        boolean gefunden = false;
        members.toFirst();
        while (members.hasAccess() && !gefunden) {
            if (members.getContent().gibName().equals(pName)) {
                gefunden = true;
            } else {
                members.next();
            }
        }
        return gefunden;
    }

    private String findeIPAdresseZuTeilnehmer(String pName) {
        String ipAdresse = null;
        boolean gefunden = false;
        members.toFirst();
        while (members.hasAccess() && !gefunden) {
            if (members.getContent().gibName().equals(pName)) {
                ipAdresse = members.getContent().gibIPAdresse();
                gefunden = true;
            } else {
                members.next();
            }
        }
        return ipAdresse;
    }

    private int findePortZuTeilnehmer(String pName) {
        int port = -1;
        boolean gefunden = false;
        members.toFirst();
        while (members.hasAccess() && !gefunden) {
            if (members.getContent().gibName().equals(pName)) {
                port = members.getContent().gibPort();
                gefunden = true;
            } else {
                members.next();
            }
        }
        return port;
    }

    private String memberFromIpAndPort(String _clientIp, int _clientPort) {
        String gefundenerTeilnehmername = null;
        boolean gefunden = false;
        members.toFirst();
        while (members.hasAccess() && !gefunden) {
            if (members.getContent().gibIPAdresse().equals(_clientIp)
                    && members.getContent().gibPort() == _clientPort) {
                gefundenerTeilnehmername = members.getContent().gibName();
                gefunden = true;
            } else {
                members.next();
            }
        }
        return gefundenerTeilnehmername;
    }

    private ArrayList<String> getAllMember() {
        ArrayList<String> allMembers = new ArrayList<String>();

        members.toFirst();
        while (members.hasAccess()) {
            allMembers.add(members.getContent().gibName());
            members.next();
        }
        return allMembers;
    }

    public static void main(String[] args) {
        new MessengerServer();
    }
}
