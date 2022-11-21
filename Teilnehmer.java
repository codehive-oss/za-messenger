/**
 * Ein Teilnehmer am Messenger-Dienst
 *
 * @author QUA-LiS NRW
 * @version 1.0
 */
public class Teilnehmer {
    private String ipAdresse;
    private int port;
    private String name;
    private boolean angemeldet;

    public Teilnehmer(String pIPAdresse, int pPort, String pName) {
        ipAdresse = pIPAdresse;
        port = pPort;
        name = pName;
        angemeldet = false;
    }

    public String gibIPAdresse() {
        return ipAdresse;
    }

    public int gibPort() {
        return port;
    }

    public String gibName() {
        return (name);
    }

    public boolean istAngemeldet() {
        return (angemeldet);
    }

    public void setzeName(String pName) {
        name = pName;
    }

    public void setzeAngemeldet(boolean pAngemeldet) {
        angemeldet = pAngemeldet;
    }
}
