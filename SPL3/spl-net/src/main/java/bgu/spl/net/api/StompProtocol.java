package bgu.spl.net.api;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;

public class StompProtocol<T> implements StompMessagingProtocol<T> {


    private ConnectionsImpl connections;

    @Override
    public void start(int connectionId, Connections<String> connections) {

    }

    @Override
    public void process(String message) {




    }

    @Override
    public T process(T msg) {
        return null;
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }



    public ConnectionsImpl getConnections() {
        return connections;
    }

}
