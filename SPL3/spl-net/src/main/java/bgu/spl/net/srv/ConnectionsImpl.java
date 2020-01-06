package bgu.spl.net.srv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConnectionsImpl <T>implements Connections<T> {

    private Map<Integer, ConnectionHandler> clients;

    private Map<String, List<Integer>> topics_subsMap;


    @Override
    /** uses the ConnectionHandler's send methods of this {@param} connectoinId to flush() the {@param} msg  */
    public boolean send(int connectionId, T msg) throws IOException {
        clients.get(connectionId).send(msg);
        return false;
    }

    @Override
    /**
     * sends the {@param} msg to every subId that is subscribes to the {@param} channel. * */
    public void send(String channel, T msg) throws IOException {
        for (int subId : topics_subsMap.get(channel)) {
            send(subId, msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {

    }

    public Map<Integer, ConnectionHandler> getClients() {
        return clients;
    }

    public void setClients(Map<Integer, ConnectionHandler> clients) {
        this.clients = clients;


    }

    public Map<String, List<Integer>> getTopics_subsMap() {
        return topics_subsMap;
    }
}
