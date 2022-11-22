/**
 * Ein Messenger-Client
 *
 * @author QUA-LiS NRW
 * @version 1.0
 */
import net.*;
import net.C2S.*;
import net.S2C.*;

import netzklassen.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

import javax.swing.*;

public class MessengerClient extends Client {
    private static final Logger logger = LoggerFactory.getLogger(MessengerClient.class);

    // TODO: remove this circular dependency, separate UI from logic, maybe using the observer pattern?
    private final MessengerClientGUI messengerClientGUI;
    private String myName;
    private boolean isLoggedIn;

    public MessengerClient(String pServerIP, int pServerPort, MessengerClientGUI pGUI) {
        super(pServerIP, pServerPort);

        messengerClientGUI = pGUI;
        myName = null;
        isLoggedIn = false;

        if (!isConnected()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Fehler beim Herstellen der Verbindung!\nDas Programm wird jetzt beendet.");
            System.exit(1);
        }
    }

    @Override
    public void processMessage(ByteBuffer _buffer) {
        ServerToClient msgId = ServerToClient.fromId(_buffer.getInt());
        logger.info("Client:" + msgId);

        if (!isLoggedIn) {
            switch (msgId) {
                case WELCOME -> {
                    PacketWelcome welcome = Packet.deserialize(_buffer);
                    JOptionPane.showMessageDialog(null, welcome.message);
                }

                case LOGIN_OK -> {
                    PacketLoginOk loginOk = Packet.deserialize(_buffer);
                    myName = loginOk.username;
                    isLoggedIn = true;

                    send(Packet.serialize(ClientToServer.GIVE_ALL_MEMBER));
                    send(Packet.serialize(ClientToServer.SEND_NAME_TO_ALL));

                    messengerClientGUI.initialisiereNachAnmeldung();
                }

                case ERROR -> {
                    PacketError error = Packet.deserialize(_buffer.array());
                    JOptionPane.showMessageDialog(null, error.message);
                }
            }
        } else {
            switch (msgId) {
                case TEXT -> {
                    PacketText text = Packet.deserialize(_buffer.array());
                    messengerClientGUI.ergaenzeNachrichten(
                            text.author + " schreibt:\n" + text.message);
                }

                case ACCESS -> {
                    PacketAccess access = Packet.deserialize(_buffer.array());
                    if (!access.username.equals(myName)) {
                        messengerClientGUI.ergaenzeTeilnehmerListe(access.username);
                    }
                }

                case EXIT -> {
                    PacketExit exit = Packet.deserialize(_buffer.array());
                    if (!exit.username.equals(myName)) {
                        messengerClientGUI.loescheNameAusTeilnehmerListe(exit.username);
                    } else {
                        messengerClientGUI.leereNachLogout();
                    }
                }

                case ALL_MEMBERS -> {
                    PacketAllMembers allMembers = Packet.deserialize(_buffer.array());
                    for (int i = 0; i < allMembers.usernames.length; i++) {
                        if (!allMembers.usernames[i].equals(myName)) {
                            messengerClientGUI.ergaenzeTeilnehmerListe(allMembers.usernames[i]);
                        }
                    }
                }

                case BYE -> {
                    myName = null;
                    isLoggedIn = false;
                    JOptionPane.showMessageDialog(
                            null,
                            "Verbindung durch den Messenger-Server geschlossen.\n"
                                    + "Das Programm wird jetzt beendet.");
                    System.exit(0);
                }

                case ERROR -> {
                    PacketError error = new PacketError(new String(_buffer.array()));
                    JOptionPane.showMessageDialog(null, error.message);
                }

                default -> {
                    JOptionPane.showMessageDialog(
                            null, "Unzulässige Anweisung empfangen: '" + msgId + "'");
                }
            }
        }
    }

    public void registrieren(String pName, String pPasswort) {
        PacketRegister register = new PacketRegister(pName, pPasswort);
        send(Packet.serialize(register));
    }

    public void anmelden(String pName, String pPasswort) {
        PacketLogin login = new PacketLogin(pName, pPasswort);
        send(Packet.serialize(login));
    }

    public void abmelden() {
        send(Packet.serialize(ClientToServer.LOGOUT));
    }

    public void nachrichtSenden(List<String> _receivers, String _message) {
        if (!_message.equals("")) {
            PacketMessage message = new PacketMessage(_receivers.toArray(new String[0]), _message);
            send(Packet.serialize(message));

            messengerClientGUI.ergaenzeNachrichten(
                    "Du schreibst an " + String.join(", ", _receivers) + "\n" + _message);
        }
    }
}
