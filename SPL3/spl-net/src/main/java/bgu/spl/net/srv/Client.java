package bgu.spl.net.srv;

public class Client {

    private String name;

    public Client(String name, String password, int connectioId) {
        this.name = name;
        this.password = password;
        this.connectioId = connectioId;
        this.loggedIn = false;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn){
        this.loggedIn=loggedIn;
    }

    private String password;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getConnectioId() {
        return connectioId;
    }

    private int connectioId;
    private boolean loggedIn;
}
