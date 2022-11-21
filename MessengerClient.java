/**
 * Ein Messenger-Client
 *
 * @author QUA-LiS NRW
 * @version 1.0
 */

import java.nio.ByteBuffer;
import java.util.List;
import javax.swing.*;
import net.*;
import net.C2S.*;
import net.S2C.*;
import netzklassen.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessengerClient extends Client {
  private static final Logger logger = LoggerFactory.getLogger(MessengerClient.class);

  // TODO: remove this circular dependency, separate UI from logic
  private final MessengerClientGUI messengerClientGUI;
  private String eigenerName;
  private boolean angemeldet;

  public MessengerClient(String pServerIP, int pServerPort,
                         MessengerClientGUI pGUI) {
    super(pServerIP, pServerPort);

    messengerClientGUI = pGUI;
    eigenerName = null;
    angemeldet = false;

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

    if (!angemeldet) {
      switch (msgId) {
        case WELCOME -> {
          PacketWelcome welcome = Packet.deserialize(_buffer.array());
          JOptionPane.showMessageDialog(null, welcome.message);
        }

        case LOGIN_OK -> {
          PacketLoginOk loginOk = Packet.deserialize(_buffer.array());
          eigenerName = loginOk.username;
          angemeldet = true;

          // TODO: Create a friendly byte buffer
          byte[] giveAllMember = Packet.serialize(ClientToServer.getId(ClientToServer.GIVE_ALL_MEMBER));
          send(giveAllMember);

          byte[] sendNameToAll = Packet.serialize(ClientToServer.getId(ClientToServer.SEND_NAME_TO_ALL));
          send(sendNameToAll);

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
          messengerClientGUI.ergaenzeNachrichten(text.author +
                                                 " schreibt:\n" +
                                                 text.message);
        }

        case ACCESS -> {
          PacketAccess access = Packet.deserialize(_buffer.array());
          if (!access.username.equals(eigenerName)) {
            messengerClientGUI.ergaenzeTeilnehmerListe(
                access.username);
          }

        }

        case EXIT -> {
          PacketExit exit = Packet.deserialize(_buffer.array());
          if (!exit.username.equals(eigenerName)) {
            messengerClientGUI.loescheNameAusTeilnehmerListe(
                exit.username);
          } else {
            messengerClientGUI.leereNachLogout();
          }
        }

        case ALL_MEMBERS -> {
          PacketAllMembers allMembers = Packet.deserialize(_buffer.array());
          for (int i = 0; i < allMembers.usernames.length; i++) {
            if (!allMembers.usernames[i].equals(eigenerName)) {
              messengerClientGUI.ergaenzeTeilnehmerListe(
                  allMembers.usernames[i]);
            }
          }
        }

        case BYE -> {
          eigenerName = null;
          angemeldet = false;
          JOptionPane.showMessageDialog(
              null,
              "Verbindung durch den Messenger-Server geschlossen.\nDas Programm wird jetzt beendet.");
          System.exit(0);
        }

        case ERROR -> {
          PacketError error = new PacketError(new String(_buffer.array()));
          JOptionPane.showMessageDialog(null, error.message);
        }

        default -> {
          JOptionPane.showMessageDialog(
              null,
              "Unzulässige Anweisung empfangen: '" + msgId + "'");
        }
      }
    }
  }

  public void registrieren(String pName, String pPasswort) {
    PacketRegister register = new PacketRegister(pName, pPasswort);
    byte[] registerData = Packet.serialize(ClientToServer.getId(ClientToServer.REGISTER), register);
    send(registerData);
  }

  public void anmelden(String pName, String pPasswort) {
    PacketLogin login = new PacketLogin(pName, pPasswort);
    byte[] loginData = Packet.serialize(ClientToServer.getId(ClientToServer.LOGIN), login);
    send(loginData);
  }

  public void abmelden() {
    byte[] logoutData = Packet.serialize(ClientToServer.getId(ClientToServer.LOGOUT));
    send(logoutData);
  }

  public void nachrichtSenden(List<String> _receivers, String _message) {
    if (!_message.equals("")) {
      PacketMessage message = new PacketMessage(_receivers.toArray(new String[0]), _message);
      byte[] messageData = Packet.serialize(ClientToServer.getId(ClientToServer.MESSAGE), message);
      send(messageData);

      messengerClientGUI.ergaenzeNachrichten(
          "Du schreibst an " +
          String.join(", ", _receivers) + "\n" + _message);
    }
  }
}
