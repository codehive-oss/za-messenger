/**
 * Ein Messenger-Client
 *
 * @author QUA-LiS NRW
 * @version 1.0
 */

import netzklassen.Client;

import javax.swing.*;

public class MessengerClient extends Client {
    MessengerClient _this;

    private MessengerClientGUI messengerClientGUI;
    String eigenerName;
    boolean angemeldet;

    public MessengerClient(String pServerIP, int pServerPort, MessengerClientGUI pGUI) {
        super(pServerIP, pServerPort);

        _this = this;

        messengerClientGUI = pGUI;
        eigenerName = null;
        angemeldet = false;

        if (!isConnected()) {
            JOptionPane.showMessageDialog(null,
                    "Fehler beim Herstellen der Verbindung!\nDas Programm wird jetzt beendet.");
            System.exit(1);
        }

    }

    @Override
    public void processMessage(String pMessage) {
        String[] pMessageZerteilt = pMessage.split(PROT.TRENNER);
        System.out.println("C0:" + pMessage + "!");

        if (!angemeldet) {
            switch (pMessageZerteilt[0]) {
                case PROT.SC_WK:
                    String willkommensnachricht = "";
                    for (int index = 1; index < pMessageZerteilt.length; index++) {
                        willkommensnachricht += pMessageZerteilt[index] + " ";
                    }
                    JOptionPane.showMessageDialog(null, willkommensnachricht);
                    break;

                case PROT.SC_AO:
                    eigenerName = pMessageZerteilt[1];
                    angemeldet = true;
                    send(PROT.CS_GA);
                    send(PROT.CS_NA);
                    messengerClientGUI.initialisiereNachAnmeldung();
                    break;
                case PROT.SC_ER:
                    JOptionPane.showMessageDialog(null, pMessageZerteilt[1]);
                    break;

            }
        } else {
            switch (pMessageZerteilt[0]) {
                case PROT.SC_TX:
                    messengerClientGUI.ergaenzeNachrichten(pMessageZerteilt[1] + " schreibt:\n" + pMessageZerteilt[2]);
                    break;

                case PROT.SC_ZU:
                    if (!pMessageZerteilt[1].equals(eigenerName)) {
                        messengerClientGUI.ergaenzeTeilnehmerListe(pMessageZerteilt[1]);
                    }
                    break;

                case PROT.SC_AB:
                    if (!pMessageZerteilt[1].equals(eigenerName)) {
                        messengerClientGUI.loescheNameAusTeilnehmerListe(pMessageZerteilt[1]);
                    } else {
                        messengerClientGUI.leereNachLogout();
                    }
                    ;
                    break;

                case PROT.SC_AT:
                    for (int index = 1; index < pMessageZerteilt.length; index++) {
                        if (!pMessageZerteilt[index].equals(eigenerName)) {
                            messengerClientGUI.ergaenzeTeilnehmerListe(pMessageZerteilt[index]);
                        }
                    }
                    break;

                case PROT.SC_BY:
                    eigenerName = null;
                    angemeldet = false;
                    JOptionPane.showMessageDialog(null, "Verbindung durch den Messenger-Server geschlossen.\nDas Programm wird jetzt beendet.");
                    System.exit(0);
                    break;

                case PROT.SC_ER:
                    JOptionPane.showMessageDialog(null, pMessageZerteilt[1]);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Unzulässige Anweisung empfangen: '" + pMessage + "'");
                    break;
            }
        }
    }

    public void registrieren(String pName, String pPasswort) {
        send(PROT.CS_RG + PROT.TRENNER + pName + PROT.TRENNER + pPasswort);
    }

    public void anmelden(String pName, String pPasswort) {
        send(PROT.CS_AN + PROT.TRENNER + pName + PROT.TRENNER + pPasswort);
    }

    public void abmelden() {
        send(PROT.CS_AB);
    }

    public void nachrichtSenden(String pEmpfaenger, String pNachricht) {
        if (!pNachricht.equals("")) {
            send(PROT.CS_TX + PROT.TRENNER + pEmpfaenger + PROT.TRENNER
                    + pNachricht);
            messengerClientGUI.ergaenzeNachrichten("Du schreibst an " + pEmpfaenger + "\n" + pNachricht);
        }
    }
}
