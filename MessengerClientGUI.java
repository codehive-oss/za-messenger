/**
 * Beschreibung
 *
 * @version 1.0
 * @author QUA-LiS NRW
 */

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;

public class MessengerClientGUI extends JFrame {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2749433956491096074L;
    // Anfang Attribute
    private final JLabel lProgrammtitel = new JLabel();
    private final JList<String> lstTeilnehmer = new JList<>();
    private final DefaultListModel<String> lstTeilnehmerModel = new DefaultListModel<>();
    private final JScrollPane lstTeilnehmerScrollPane = new JScrollPane(lstTeilnehmer);
    private final JLabel lTeilnehmer = new JLabel();
    private final JTextArea taNachrichten = new JTextArea("");
    private final JScrollPane taNachrichtenScrollPane = new JScrollPane(taNachrichten);
    private final JLabel lProtokoll = new JLabel();
    private final JTextField tfNachricht = new JTextField();
    private final JLabel lNachricht = new JLabel();
    private final JButton bSenden = new JButton();
    private final JButton bRegister = new JButton();
    private final JButton bLogIn = new JButton();
    private final JButton bLogOut = new JButton();
    // Ende Attribute

    // Eigene Attribute
    private static final String WINDOW_TITLE = "Messenger Client GUI";
    private static final String CONST_SERVERIP = "127.0.0.1";
    private static final int CONST_SERVERPORT = 20017;
    private MessengerClient messengerClient;

    public MessengerClientGUI() {
        this(CONST_SERVERIP, CONST_SERVERPORT);
    }

    public MessengerClientGUI(String pServerIp, int pPort) {
        // Frame-Initialisierung
        super(WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int frameWidth = 592;
        int frameHeight = 450;
        setSize(frameWidth, frameHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setResizable(false);
        Container cp = getContentPane();
        cp.setLayout(null);

        // Anfang Komponenten

        lProgrammtitel.setBounds(16, 19, 116, 28);
        lProgrammtitel.setText("Messenger");
        lProgrammtitel.setFont(new Font("Dialog", Font.BOLD, 20));
        cp.add(lProgrammtitel);

        lstTeilnehmer.setModel(lstTeilnehmerModel);
        lstTeilnehmerScrollPane.setBounds(416, 112, 145, 297);
        lstTeilnehmer.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        cp.add(lstTeilnehmerScrollPane);

        lTeilnehmer.setBounds(416, 83, 133, 20);
        lTeilnehmer.setText("Teilnehmer");
        lTeilnehmer.setFont(new Font("Dialog", Font.BOLD, 12));
        cp.add(lTeilnehmer);

        taNachrichtenScrollPane.setBounds(16, 107, 385, 217);
        taNachrichtenScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        taNachrichten.setLineWrap(true);
        cp.add(taNachrichtenScrollPane);
        DefaultCaret caret = (DefaultCaret) taNachrichten.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        lProtokoll.setBounds(16, 83, 142, 20);
        lProtokoll.setText("Nachrichtenverlauf");
        cp.add(lProtokoll);

        tfNachricht.setBounds(16, 363, 313, 33);
        cp.add(tfNachricht);

        lNachricht.setBounds(16, 339, 142, 20);
        lNachricht.setText("Eigene Nachricht");
        cp.add(lNachricht);

        bSenden.setBounds(336, 363, 65, 33);
        bSenden.setText("Senden");
        bSenden.setMargin(new Insets(2, 2, 2, 2));
        bSenden.addActionListener(this::bSenden_ActionPerformed);
        cp.add(bSenden);


        bRegister.setBounds(324, 19, 87, 33);
        bRegister.setText("Registrieren");
        bRegister.setMargin(new Insets(2, 2, 2, 2));
        bRegister.addActionListener(evt -> {
            JPanel registerPanel = new JPanel();
            JTextField nameField = new JTextField(10);
            JTextField passwordField = new JTextField(10);
            registerPanel.add(new JLabel("Name:"));
            registerPanel.add(nameField);
            registerPanel.add(Box.createHorizontalStrut(15));
            registerPanel.add(new JLabel("Passwort:"));
            registerPanel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(this, registerPanel, "Registrierung", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && !nameField.getText().equals("") && !passwordField.getText().equals("")) {
                messengerClient.registrieren(nameField.getText(), passwordField.getText());
            }
        });
        cp.add(bRegister);


        bLogIn.setBounds(416, 19, 67, 33);
        bLogIn.setText("Login");
        bLogIn.setMargin(new Insets(2, 2, 2, 2));
        bLogIn.addActionListener(evt -> {
            JPanel registerPanel = new JPanel();
            JTextField nameField = new JTextField(10);
            JTextField passwordField = new JTextField(10);
            registerPanel.add(new JLabel("Name:"));
            registerPanel.add(nameField);
            registerPanel.add(Box.createHorizontalStrut(15));
            registerPanel.add(new JLabel("Passwort:"));
            registerPanel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(this, registerPanel, "LogIn", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && !nameField.getText().equals("") && !passwordField.getText().equals("")) {
                messengerClient.anmelden(nameField.getText(), passwordField.getText());
            }
        });
        cp.add(bLogIn);

        bLogOut.setBounds(488, 19, 75, 33);
        bLogOut.setText("Logout");
        bLogOut.setMargin(new Insets(2, 2, 2, 2));
        bLogOut.setEnabled(false);
        bLogOut.addActionListener(this::bLogOut_ActionPerformed);
        cp.add(bLogOut);
        // Ende Komponenten

        // Eigene Initialisierungen:
        messengerClient = new MessengerClient(pServerIp, pPort, this);

        setVisible(true);
    } // end of public MessengerClientGUI

    // Anfang Methoden

    // Anfang ActionListener-Methoden

    public void bSenden_ActionPerformed(ActionEvent evt) {
        if (!lstTeilnehmer.getSelectedValuesList().isEmpty()) {
            messengerClient.nachrichtSenden(lstTeilnehmer.getSelectedValuesList(), tfNachricht.getText());
            tfNachricht.setText("");
        }
    } // end of bSenden_ActionPerformed

    public void bLogOut_ActionPerformed(ActionEvent evt) {
        messengerClient.abmelden();
    } // end of bLogOut_ActionPerformed
    //
    //    // Ende ActionListener-Methoden
    //
    //    // Anfang eigene Methoden

    public void initialisiereNachAnmeldung() {
        bLogIn.setEnabled(false);
        bRegister.setEnabled(false);
        bLogOut.setEnabled(true);
    }

    public void leereNachLogout() {
        taNachrichten.setText("");
        lstTeilnehmerModel.clear();
        bLogIn.setEnabled(true);
        bLogOut.setEnabled(false);
    }

    public void ergaenzeNachrichten(String pNachricht) {
        taNachrichten.append(pNachricht + "\n\n");
    }

    public void ergaenzeTeilnehmerListe(String pName) {
        lstTeilnehmerModel.addElement(pName);
    }

    public void loescheNameAusTeilnehmerListe(String pName) {
        lstTeilnehmerModel.removeElement(pName);
    }

    public static void main(String[] args) {
        new MessengerClientGUI();
    }
} // end of class MessengerClientGUI
