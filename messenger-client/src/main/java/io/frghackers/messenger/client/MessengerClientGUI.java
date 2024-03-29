package io.frghackers.messenger.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serial;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

public class MessengerClientGUI extends JFrame {

    @Serial private static final long serialVersionUID = 2749433956491096074L;

    private final JLabel lProgrammtitel = new JLabel();
    private final JList<String> lstTeilnehmer = new JList<>();
    private final DefaultListModel<String> lstTeilnehmerModel = new DefaultListModel<>();
    private final JScrollPane lstTeilnehmerScrollPane = new JScrollPane(lstTeilnehmer);
    private final JLabel lTeilnehmer = new JLabel();
    private final JTextPane taNachrichten = new JTextPane();
    private final JScrollPane taNachrichtenScrollPane = new JScrollPane(taNachrichten);
    private final JLabel lProtokoll = new JLabel();
    private final JTextField tfNachricht = new JTextField();
    private final JLabel lNachricht = new JLabel();
    private final JButton bSenden = new JButton();
    private final JButton bRegister = new JButton();
    private final JButton bLogIn = new JButton();
    private final JButton bLogOut = new JButton();
    private final JButton bImg = new JButton();
    private final JFileChooser fileChooser = new JFileChooser();

    private static final String WINDOW_TITLE = "Messenger Client GUI";
    private static final String CONST_SERVERIP = "127.0.0.1";
    private static final int CONST_SERVERPORT = 20017;
    private MessengerClient messengerClient;

    public MessengerClientGUI() {
        this(CONST_SERVERIP, CONST_SERVERPORT);
    }

    public MessengerClientGUI(String pServerIp, int pPort) {
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

        lProgrammtitel.setBounds(16, 19, 116, 28);
        lProgrammtitel.setText("Messenger");
        lProgrammtitel.setFont(new Font("Dialog", Font.BOLD, 20));
        cp.add(lProgrammtitel);

        lstTeilnehmer.setModel(lstTeilnehmerModel);
        lstTeilnehmerScrollPane.setBounds(416, 112, 145, 297);
        lstTeilnehmer.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lstTeilnehmer.addListSelectionListener(
                e -> {
                    if (lstTeilnehmer.getSelectedValuesList().isEmpty()) {
                        bSenden.setEnabled(false);
                        bImg.setEnabled(false);
                    } else {
                        bSenden.setEnabled(true);
                        bImg.setEnabled(true);
                    }
                });
        cp.add(lstTeilnehmerScrollPane);

        lTeilnehmer.setBounds(416, 83, 133, 20);
        lTeilnehmer.setText("Teilnehmer");
        lTeilnehmer.setFont(new Font("Dialog", Font.BOLD, 12));
        cp.add(lTeilnehmer);

        taNachrichtenScrollPane.setBounds(16, 107, 385, 217);
        taNachrichtenScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        cp.add(taNachrichtenScrollPane);
        taNachrichten.setEditable(false);
        DefaultCaret caret = (DefaultCaret) taNachrichten.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        lProtokoll.setBounds(16, 83, 142, 20);
        lProtokoll.setText("Nachrichtenverlauf");
        cp.add(lProtokoll);

        tfNachricht.setBounds(16, 363, 258, 33);
        cp.add(tfNachricht);

        lNachricht.setBounds(16, 339, 142, 20);
        lNachricht.setText("Eigene Nachricht");
        cp.add(lNachricht);

        FileNameExtensionFilter imageFilter =
                new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(imageFilter);
        cp.add(fileChooser);

        bImg.setBounds(279, 363, 50, 33);
        bImg.setText("IMG");
        bImg.setMargin(new Insets(2, 2, 2, 2));
        bImg.setEnabled(false);
        bImg.addActionListener(
                e -> {
                    int res = fileChooser.showDialog(this, "Bild Wählen");
                    if (res == JFileChooser.APPROVE_OPTION) {
                        try {
                            BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(image, "png", baos);
                            byte[] imageInByte = baos.toByteArray();
                            messengerClient.bildSenden(
                                    lstTeilnehmer.getSelectedValuesList(), imageInByte);
                            JOptionPane.showMessageDialog(this, "Bild gesendet");

                        } catch (IOException | IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(this, "FEHLER");
                        }
                    }
                });
        cp.add(bImg);

        bSenden.setBounds(341, 363, 65, 33);
        bSenden.setText("Senden");
        bSenden.setMargin(new Insets(2, 2, 2, 2));
        bSenden.setEnabled(false);
        bSenden.addActionListener(this::bSenden_ActionPerformed);
        cp.add(bSenden);

        bRegister.setBounds(324, 19, 87, 33);
        bRegister.setText("Registrieren");
        bRegister.setMargin(new Insets(2, 2, 2, 2));
        bRegister.addActionListener(
                evt -> {
                    JPanel registerPanel = new JPanel();
                    JTextField nameField = new JTextField(10);
                    JTextField passwordField = new JTextField(10);
                    registerPanel.add(new JLabel("Name:"));
                    registerPanel.add(nameField);
                    registerPanel.add(Box.createHorizontalStrut(15));
                    registerPanel.add(new JLabel("Passwort:"));
                    registerPanel.add(passwordField);

                    int result =
                            JOptionPane.showConfirmDialog(
                                    this,
                                    registerPanel,
                                    "Registrierung",
                                    JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION
                            && !nameField.getText().equals("")
                            && !passwordField.getText().equals("")) {
                        messengerClient.registrieren(nameField.getText(), passwordField.getText());
                    }
                });
        cp.add(bRegister);

        bLogIn.setBounds(416, 19, 67, 33);
        bLogIn.setText("Login");
        bLogIn.setMargin(new Insets(2, 2, 2, 2));
        bLogIn.addActionListener(
                evt -> {
                    JPanel registerPanel = new JPanel();
                    JTextField nameField = new JTextField(10);
                    JTextField passwordField = new JTextField(10);
                    registerPanel.add(new JLabel("Name:"));
                    registerPanel.add(nameField);
                    registerPanel.add(Box.createHorizontalStrut(15));
                    registerPanel.add(new JLabel("Passwort:"));
                    registerPanel.add(passwordField);

                    int result =
                            JOptionPane.showConfirmDialog(
                                    this, registerPanel, "LogIn", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION
                            && !nameField.getText().equals("")
                            && !passwordField.getText().equals("")) {
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

        messengerClient = new MessengerClient(pServerIp, pPort, this);

        setVisible(true);
    }

    public void bSenden_ActionPerformed(ActionEvent evt) {
        if (!lstTeilnehmer.getSelectedValuesList().isEmpty()) {
            messengerClient.nachrichtSenden(
                    lstTeilnehmer.getSelectedValuesList(), tfNachricht.getText());
            tfNachricht.setText("");
        }
    }

    public void bLogOut_ActionPerformed(ActionEvent evt) {
        messengerClient.abmelden();
    }

    public void initialisiereNachAnmeldung() {
        bLogIn.setEnabled(false);
        bRegister.setEnabled(false);
        bLogOut.setEnabled(true);
        bSenden.setEnabled(true);
        bImg.setEnabled(true);
    }

    public void leereNachLogout() {
        taNachrichten.setText("");
        lstTeilnehmerModel.clear();
        bLogIn.setEnabled(true);
        bLogOut.setEnabled(false);
        bSenden.setEnabled(false);
        bImg.setEnabled(false);
    }

    public void ergaenzeNachrichten(String pNachricht) {
        try {
            Document docs = taNachrichten.getDocument();
            docs.insertString(docs.getLength(), pNachricht + "\n\n", null);
        } catch (Exception ignored) {
        }
    }

    public void ergaenzeBild(byte[] bild) {
        try {
            Document docs = taNachrichten.getDocument();
            ImageIcon scaled = new ImageIcon(new ImageIcon(bild).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            taNachrichten.setCaretPosition(docs.getLength());
            taNachrichten.insertIcon(scaled);
        } catch (Exception ignored) {
        }

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
}
