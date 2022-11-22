/**
 * Ein Teilnehmer am Messenger-Dienst
 *
 * @author QUA-LiS NRW
 * @version 1.0
 */
public class Member {
    private String ip;
    private int port;
    private String name;
    private boolean loggedIn;

    public Member(String _ip, int _port, String _name) {
        this.ip = _ip;
        this.port = _port;
        this.name = _name;
        loggedIn = false;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public void setLoggedIn(boolean _loggedIn) {
        this.loggedIn = _loggedIn;
    }
}
