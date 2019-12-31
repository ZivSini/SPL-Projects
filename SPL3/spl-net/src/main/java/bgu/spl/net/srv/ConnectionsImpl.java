package bgu.spl.net.srv;

import java.util.Map;

public class ConnectionsImpl <T>implements Connections<T> {
    private Map<Integer,ConnectionHandler> clients;



    @Override
    public boolean send(int connectionId, T msg) {

        return false;
    }

    @Override
    public void send(String channel, T msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }
}
