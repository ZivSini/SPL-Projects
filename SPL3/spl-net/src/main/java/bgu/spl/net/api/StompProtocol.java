package bgu.spl.net.api;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;

public class StompProtocol implements StompMessagingProtocol {


    private ConnectionsImpl connections;

    @Override
    public void start(int connectionId, Connections<String> connections) {

    }

    @Override
    public void process(String message) {

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }



    public ConnectionsImpl getConnections() {
        return connections;
    }

}
