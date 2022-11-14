 /**
 *
 * Beschreibung
 *
 * @version 1.0 
 * @author QUA-LiS NRW
 */
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

public class MessengerClientGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2749433956491096074L;
// Anfang Attribute
	private JLabel lProgrammtitel = new JLabel();
	private JList<String> lstTeilnehmer = new JList<String>();
	private DefaultListModel lstTeilnehmerModel = new DefaultListModel();
	private JScrollPane lstTeilnehmerScrollPane = new JScrollPane(lstTeilnehmer);
	private JLabel lTeilnehmer = new JLabel();
	private JTextArea taNachrichten = new JTextArea("");
	private JScrollPane taNachrichtenScrollPane = new JScrollPane(taNachrichten);
	private JLabel lProtokoll = new JLabel();
	private JTextField tfNachricht = new JTextField();
	private JLabel lNachricht = new JLabel();
	private JButton bSenden = new JButton();
	private JTextField tfName = new JTextField();
	private JButton bLogIn = new JButton();
	private JButton bLogOut = new JButton();
	// Ende Attribute

	// Eigene Attribute
	private final String CONST_SERVERIP = "127.0.0.1";
	private final int CONST_SERVERPORT = 20017;
	private MessengerClient messengerClient;

	public MessengerClientGUI() {
		// Frame-Initialisierung
		super("Messenger ClientGUI");
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
		lstTeilnehmer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cp.add(lstTeilnehmerScrollPane);
		
		lTeilnehmer.setBounds(416, 83, 133, 20);
		lTeilnehmer.setText("Teilnehmer");
		lTeilnehmer.setFont(new Font("Dialog", Font.BOLD, 12));
		cp.add(lTeilnehmer);
		
		taNachrichtenScrollPane.setBounds(16, 107, 385, 217);
		taNachrichtenScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		taNachrichten.setLineWrap(true);
		cp.add(taNachrichtenScrollPane);
		DefaultCaret caret = (DefaultCaret)taNachrichten.getCaret();
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
		bSenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bSenden_ActionPerformed(evt);
			}
		});
		cp.add(bSenden);
		
		tfName.setBounds(208, 19, 193, 33);
		tfName.setText("EigenerName");
		cp.add(tfName);
		
		bLogIn.setBounds(416, 19, 67, 33);
		bLogIn.setText("Login");
		bLogIn.setMargin(new Insets(2, 2, 2, 2));
		bLogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bLogIn_ActionPerformed(evt);
			}
		});
		cp.add(bLogIn);
		
		bLogOut.setBounds(488, 19, 75, 33);
		bLogOut.setText("Logout");
		bLogOut.setMargin(new Insets(2, 2, 2, 2));
		bLogOut.setEnabled(false);
		bLogOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bLogOut_ActionPerformed(evt);
			}
		});
		cp.add(bLogOut);
		// Ende Komponenten

		// Eigene Initialisierungen:
		messengerClient = new MessengerClient(CONST_SERVERIP, CONST_SERVERPORT, this);

		setVisible(true);
	} // end of public MessengerClientGUI

	// Anfang Methoden

	// Anfang ActionListener-Methoden

	public void bSenden_ActionPerformed(ActionEvent evt) {
		if (lstTeilnehmer.getSelectedValue() != null) {
			messengerClient.nachrichtSenden(lstTeilnehmer.getSelectedValue(), tfNachricht.getText());
			tfNachricht.setText("");
		}
	} // end of bSenden_ActionPerformed

	public void bLogIn_ActionPerformed(ActionEvent evt) {
		messengerClient.anmelden(tfName.getText().replaceAll(" ", ""));

	} // end of bLogIn_ActionPerformed

	public void bLogOut_ActionPerformed(ActionEvent evt) {
		messengerClient.abmelden();
	} // end of bLogOut_ActionPerformed

	// Ende ActionListener-Methoden

	// Anfang eigene Methoden

	public void initialisiereNachAnmeldung() {
		bLogIn.setEnabled(false);
		bLogOut.setEnabled(true);
		tfName.setEnabled(false);
	}

	public void leereNachLogout() {
		taNachrichten.setText("");
		lstTeilnehmerModel.clear();
		bLogIn.setEnabled(true);
		bLogOut.setEnabled(false);
		tfName.setEnabled(true);
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
