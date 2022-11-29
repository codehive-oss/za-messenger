package io.frghackers.messenger.client;

import io.frghackers.messenger.api.net.C2S.ClientToServer;
import io.frghackers.messenger.api.net.C2S.PacketLogin;
import io.frghackers.messenger.api.net.C2S.PacketRegister;
import io.frghackers.messenger.api.net.C2S.PacketServerboundMessage;
import io.frghackers.messenger.api.net.FriendlyBuffer;
import io.frghackers.messenger.api.net.S2C.*;
import io.frghackers.messenger.api.netzklassen.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

public class MessengerClient extends Client {
    private static final Logger logger = LoggerFactory.getLogger(MessengerClient.class);

    // TODO: remove this circular dependency, separate UI from logic, maybe using the observer
    // pattern?
    private final MessengerClientGUI messengerClientGUI;
    private String myName;
    private boolean isLoggedIn;

    public MessengerClient(String pServerIP, int pServerPort, MessengerClientGUI pGUI) {
        super(pServerIP, pServerPort);

        messengerClientGUI = pGUI;
        myName = null;
        isLoggedIn = false;

        throw new RuntimeException("Fehler beim Herstellen der Verbindung! Das Programm wird jetzt beendet.");
    }


    @Override
    public void processMessage(FriendlyBuffer buffer) {
        ServerToClient msgId = ServerToClient.fromId(buffer.getInt());
        logger.info("Client " + myName + ": " + msgId);

        if (!isLoggedIn) {
            switch (msgId) {
                case WELCOME -> {
                    PacketWelcome welcome = buffer.getPacketData(PacketWelcome.class);
                    JOptionPane.showMessageDialog(null, welcome.message);
                }

                case LOGIN_OK -> {
                    PacketLoginOk loginOk = buffer.getPacketData(PacketLoginOk.class);
                    myName = loginOk.username;
                    isLoggedIn = true;

                    send(new FriendlyBuffer().putInt(ClientToServer.GIVE_ALL_MEMBER.getId()));
                    send(new FriendlyBuffer().putInt(ClientToServer.SEND_NAME_TO_ALL.getId()));

                    messengerClientGUI.initialisiereNachAnmeldung();
                }

                case ERROR -> {
                    PacketError error = buffer.getPacketData(PacketError.class);
                    JOptionPane.showMessageDialog(null, error.message);
                }
            }
        } else {
            switch (msgId) {
                case MESSAGE_TEXT -> {
                    PacketClientboundMessage.Text text = buffer.getPacketData(PacketClientboundMessage.Text.class);
                    messengerClientGUI.ergaenzeNachrichten(
                            text.author + " schreibt:\n" + text.message);
                }

                case MESSAGE_IMAGE -> {
                    PacketClientboundMessage.Image image = buffer.getPacketData(PacketClientboundMessage.Image.class);
                    messengerClientGUI.ergaenzeNachrichten(image.author + " schickt ein Bild:\n");
                    messengerClientGUI.ergaenzeBild(image.imageData);
                    messengerClientGUI.ergaenzeNachrichten("\n\n");
                }

                case ACCESS -> {
                    PacketAccess access = buffer.getPacketData(PacketAccess.class);
                    if (!access.username.equals(myName)) {
                        messengerClientGUI.ergaenzeTeilnehmerListe(access.username);
                    }
                }

                case EXIT -> {
                    PacketExit exit = buffer.getPacketData(PacketExit.class);
                    if (!exit.username.equals(myName)) {
                        messengerClientGUI.loescheNameAusTeilnehmerListe(exit.username);
                    } else {
                        messengerClientGUI.leereNachLogout();
                    }
                }

                case ALL_MEMBERS -> {
                    PacketAllMembers allMembers = buffer.getPacketData(PacketAllMembers.class);
                    for (int i = 0; i < allMembers.usernames.length; i++) {
                        if (!allMembers.usernames[i].equals(myName)) {
                            messengerClientGUI.ergaenzeTeilnehmerListe(allMembers.usernames[i]);
                        }
                    }
                }

                case BYE -> {
                    myName = null;
                    isLoggedIn = false;

                    throw new RuntimeException("Verbindung durch den Messenger-Server geschlossen. Das Programm wird jetzt beendet.");
                }

                case ERROR -> {
                    PacketError error = buffer.getPacketData(PacketError.class);
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
        send(new FriendlyBuffer().putInt(register.getPacketId()).putPacketData(register));
    }

    public void anmelden(String pName, String pPasswort) {
        PacketLogin login = new PacketLogin(pName, pPasswort);
        send(new FriendlyBuffer().putInt(login.getPacketId()).putPacketData(login));
    }

    public void abmelden() {
        send(new FriendlyBuffer().putInt(ClientToServer.LOGOUT.getId()));
    }

    public void nachrichtSenden(List<String> _receivers, String _message) {
        if (!_message.equals("")) {
            PacketServerboundMessage.Text message =
                    new PacketServerboundMessage.Text(_receivers.toArray(new String[0]), _message);
            send(new FriendlyBuffer().putInt(message.getPacketId()).putPacketData(message));

            messengerClientGUI.ergaenzeNachrichten(
                    "Du schreibst an " + String.join(", ", _receivers) + "\n" + _message);
        }
    }

    public void bildSenden(List<String> _receivers, byte[] _imageData) {
        if (_imageData.length != 0) {
            messengerClientGUI.ergaenzeNachrichten("Du schickst ein Bild:\n");
            messengerClientGUI.ergaenzeBild(_imageData);
            messengerClientGUI.ergaenzeNachrichten("\n\n");

            PacketServerboundMessage.Image message =
                    new PacketServerboundMessage.Image(_receivers.toArray(new String[0]), _imageData);
            send(new FriendlyBuffer().putInt(message.getPacketId()).putPacketData(message));
        }
    }
}
