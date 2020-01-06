package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.List;

public class Client {

    private String name;
    private int connectioId;
    private boolean loggedIn;
    private String password;

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

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getConnectioId() {
        return connectioId;
    }


}
