import db.Argon2;
import db.User;
import db.UserRepository;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.*;
import net.PROT;
import netzklassen.List;
import netzklassen.Server;

/**
 * Ein Messenger-Server
 *
 * @author QUA-LiS NRW
 * @version 1.0
 */
public class MessengerServer extends Server {
  private List<Teilnehmer> angemeldeteTeilnehmer;
  private final UserRepository userRepository;

  public MessengerServer() {
    super(20017);
    if (!isOpen()) {
      JOptionPane.showMessageDialog(
          null, "Fehler beim Starten des Servers auf Port 20017!");
      System.exit(1);
    } else {
      angemeldeteTeilnehmer = new List<>();
      System.out.println("Server ist gestartet.");
    }

    userRepository = new UserRepository();
    userRepository.createTable();
    System.out.println("Datenbank initialisiert");
  }

  @Override
  public void processNewConnection(String pClientIP, int pClientPort) {
    send(pClientIP, pClientPort,
         PROT.SC_WK + PROT.TRENNER + "Willkommen auf dem Messenger-Server!");
  }

  @Override
  public synchronized void processMessage(String pClientIP, int pClientPort,
                                          String pMessage) {
    String[] pMessageZerteilt = pMessage.split(PROT.TRENNER);
    System.out.println("S0:" + pMessage + "!");
    if (!istTeilnehmerAngemeldet(pClientIP, pClientPort)) {
      if (pMessageZerteilt[0].equals(PROT.CS_AN)) {
        String name = pMessageZerteilt[1];
        String password = pMessageZerteilt[2];
        Optional<User> user = userRepository.getUser(name);
        if (user.isEmpty()) {
          send(pClientIP, pClientPort,
               PROT.SC_ER + PROT.TRENNER + "Benutzer existiert nicht!");
        } else {
          if (!Argon2.INSTANCE.verify(user.get().getPassword(),
                                      password.toCharArray())) {
            send(pClientIP, pClientPort,
                 PROT.SC_ER + PROT.TRENNER + "Falsches Passwort");
          } else {
            if (istNameVergeben(name))
              send(pClientIP, pClientPort,
                   PROT.SC_ER + PROT.TRENNER +
                       "Du bist bereits woanders eingeloggt.");
            else {
              meldeTeilnehmerAn(pClientIP, pClientPort, pMessageZerteilt[1]);
              send(pClientIP, pClientPort,
                   PROT.SC_AO + PROT.TRENNER + pMessageZerteilt[1]);
            }
          }
        }

      } else if (pMessageZerteilt[0].equals(PROT.CS_RG)) {
        String name = pMessageZerteilt[1];
        String password = pMessageZerteilt[2];
        if (userRepository.createUser(name, Argon2.hash(password))) {
          send(pClientIP, pClientPort,
               PROT.SC_ER + PROT.TRENNER + "Benutzer " + name +
                   " erfolgreich erstellt");
        } else {
          send(pClientIP, pClientPort,
               PROT.SC_ER + PROT.TRENNER + "Fehler bei der Registrierung");
        }
      } else {
        System.out.println("FEHLER");
        send(pClientIP, pClientPort,
             PROT.SC_ER + PROT.TRENNER + "Sie sind nicht angemeldet!");
      }
    } else {
      switch (pMessageZerteilt[0]) {
                case PROT.CS_AN ->
                        send(pClientIP, pClientPort, PROT.SC_ER + PROT.TRENNER + "Sie sind bereits angemeldet!");
                case PROT.CS_GA ->
                        send(pClientIP, pClientPort, PROT.SC_AT + PROT.TRENNER + gibAlleAngemeldetenTeilnehmernamen());
                case PROT.CS_NA ->
                        sendToAll(PROT.SC_ZU + PROT.TRENNER + findeTeilnehmernameZuIPAdresseUndPort(pClientIP, pClientPort));
                case PROT.CS_AB -> {
                    sendToAll(PROT.SC_AB + PROT.TRENNER + findeTeilnehmernameZuIPAdresseUndPort(pClientIP, pClientPort));
                    meldeTeilnehmerAb(pClientIP, pClientPort);
                    closeConnection(pClientIP, pClientPort);
                }
                case PROT.CS_TX -> {
                    String[] empfaenger = Arrays.copyOfRange(pMessageZerteilt, 1, pMessageZerteilt.length - 1);
                    for (String s : empfaenger) {
                    String empfaengerIP = findeIPAdresseZuTeilnehmer(s);
                    int empfaengerPort = findePortZuTeilnehmer(s);
                    String senderName = findeTeilnehmernameZuIPAdresseUndPort(
                        pClientIP, pClientPort);
                    send(empfaengerIP, empfaengerPort,
                         PROT.SC_TX + PROT.TRENNER + senderName + PROT.TRENNER +
                             pMessageZerteilt[pMessageZerteilt.length - 1]);
                  }
                }
    }
  }
}

@Override
public synchronized void processClosingConnection(String pClientIP,
                                                  int pClientPort) {
  if (isConnectedTo(pClientIP, pClientPort))
    send(pClientIP, pClientPort, PROT.SC_BY);
}

private void meldeTeilnehmerAn(String pClientIP, int pClientPort,
                               String pName) {
  Teilnehmer neuerTeilnehmer = new Teilnehmer(pClientIP, pClientPort, pName);
  neuerTeilnehmer.setzeAngemeldet(true);
  angemeldeteTeilnehmer.append(neuerTeilnehmer);
}

private void meldeTeilnehmerAb(String pClientIP, int pClientPort) {
  boolean gefunden = false;
  angemeldeteTeilnehmer.toFirst();
  while (angemeldeteTeilnehmer.hasAccess() && !gefunden) {
    if (angemeldeteTeilnehmer.getContent().gibIPAdresse().equals(pClientIP) &&
        angemeldeteTeilnehmer.getContent().gibPort() == pClientPort) {
                angemeldeteTeilnehmer.getContent().setzeAngemeldet(false);
                angemeldeteTeilnehmer.getContent().setzeName(null);
                angemeldeteTeilnehmer.remove();
                gefunden = true;
    } else {
                angemeldeteTeilnehmer.next();
    }
  }
}

private boolean istTeilnehmerAngemeldet(String pClientIP, int pClientPort) {
  boolean gefunden = false;
  boolean angemeldet = false;
  angemeldeteTeilnehmer.toFirst();
  while (angemeldeteTeilnehmer.hasAccess() && !gefunden) {
    if (angemeldeteTeilnehmer.getContent().gibIPAdresse().equals(pClientIP) &&
        angemeldeteTeilnehmer.getContent().gibPort() == pClientPort) {
                angemeldet = angemeldeteTeilnehmer.getContent().istAngemeldet();
                gefunden = true;
    } else {
                angemeldeteTeilnehmer.next();
    }
  }
  return (angemeldet);
}

private boolean istNameVergeben(String pName) {
  boolean gefunden = false;
  angemeldeteTeilnehmer.toFirst();
  while (angemeldeteTeilnehmer.hasAccess() && !gefunden) {
    if (angemeldeteTeilnehmer.getContent().gibName().equals(pName)) {
                gefunden = true;
    } else {
                angemeldeteTeilnehmer.next();
    }
  }
  return (gefunden);
}

private String findeIPAdresseZuTeilnehmer(String pName) {
  String ipAdresse = null;
  boolean gefunden = false;
  angemeldeteTeilnehmer.toFirst();
  while (angemeldeteTeilnehmer.hasAccess() && !gefunden) {
    if (angemeldeteTeilnehmer.getContent().gibName().equals(pName)) {
                ipAdresse = angemeldeteTeilnehmer.getContent().gibIPAdresse();
                gefunden = true;
    } else {
                angemeldeteTeilnehmer.next();
    }
  }
  return (ipAdresse);
}

private int findePortZuTeilnehmer(String pName) {
  int port = -1;
  boolean gefunden = false;
  angemeldeteTeilnehmer.toFirst();
  while (angemeldeteTeilnehmer.hasAccess() && !gefunden) {
    if (angemeldeteTeilnehmer.getContent().gibName().equals(pName)) {
                port = angemeldeteTeilnehmer.getContent().gibPort();
                gefunden = true;
    } else {
                angemeldeteTeilnehmer.next();
    }
  }
  return (port);
}

private String findeTeilnehmernameZuIPAdresseUndPort(String pClientIP,
                                                     int pClientPort) {
  String gefundenerTeilnehmername = null;
  boolean gefunden = false;
  angemeldeteTeilnehmer.toFirst();
  while (angemeldeteTeilnehmer.hasAccess() && !gefunden) {
    if (angemeldeteTeilnehmer.getContent().gibIPAdresse().equals(pClientIP) &&
        angemeldeteTeilnehmer.getContent().gibPort() == pClientPort) {
                gefundenerTeilnehmername =
                    angemeldeteTeilnehmer.getContent().gibName();
                gefunden = true;
    } else {
                angemeldeteTeilnehmer.next();
    }
  }
  return (gefundenerTeilnehmername);
}

private String gibAlleAngemeldetenTeilnehmernamen() {
  StringBuilder teilnehmernamen = new StringBuilder();
  angemeldeteTeilnehmer.toFirst();
  while (angemeldeteTeilnehmer.hasAccess()) {
    teilnehmernamen.append(angemeldeteTeilnehmer.getContent().gibName())
        .append(PROT.TRENNER);
    angemeldeteTeilnehmer.next();
  }
  if (!teilnehmernamen.toString().equals("")) {
    return (teilnehmernamen.substring(0, teilnehmernamen.length() - 1));
  } else {
    return ("");
  }
}

public static void main(String[] args) { new MessengerServer(); }
}
