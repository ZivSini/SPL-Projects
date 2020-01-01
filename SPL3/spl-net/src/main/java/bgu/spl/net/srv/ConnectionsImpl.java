package bgu.spl.net.srv;

import java.util.List;
import java.util.Map;

public class ConnectionsImpl <T>implements Connections<T> {
    private Map<Integer,ConnectionHandler> clients;
    private Map<String, List<Integer>> topics;


    @Override
    public boolean send(int connectionId, T msg) {

        return false;
    }

    @Override
    public void send(String channel, T msg) {
        for (int i : topics.get(channel)){
            send(i,msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {

    }
}
