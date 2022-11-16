package net;

/**
 * Protokoll des Messenger-Dienstes
 * @version 1.0
 * @author QUA-LiS NRW
 */
public class PROT {

	// Nachrichten Server an Client -------------------------------

	public static final String SC_WK = "+WILLKOMMEN";

	public static final String SC_AO = "+Anmeldung_OK";

	public static final String SC_ZU = "+Zugang";

	// Mit Trenner werden Namen aller Teilnehmer, getrennt durch Trenner, angehaengt
	public static final String SC_AT ="+ALLE_TEILNEHMER";
	
	// Mit Trenner wird der Name des Absenders und die Nachricht angehaengt
	public static final String SC_TX = "+TEXT";
	
	public static final String SC_BY = "+BYE";
	
	//Nach Trennzeichen wird der Name des abgemeldeten Teilnehmers angehängt
	public static final String SC_AB = "+ABGANG";

	// Mit Trenner wird Fehlermeldung angehaengt
	public static final String SC_ER = "-FEHLER";
	
	// Nachrichten Client an Server -------------------------------

	// Mit Trenner wird der Teilnehmername und das Passwort angehaengt
	public static final String CS_AN = "+ANMELDEN";

	// Mit Trenner wird der Teilnehmername und das Passwort angehaengt
	public static final String CS_RG = "+REGISTRIEREN";

	public static final String CS_GA ="+GIB_ALLE_TN";
        
	public static final String CS_NA ="+SENDE_NAME_AN_ALLE";
	// Mit Trenner wird der Name des Empfaengers und die Nachricht angehaengt
	public static final String CS_TX = "+TEXT";
	
	public static final String CS_AB ="+ABMELDEN";

	

	

	public static final String TRENNER = ":";
}
