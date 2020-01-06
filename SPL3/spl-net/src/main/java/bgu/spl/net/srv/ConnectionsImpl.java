package bgu.spl.net.srv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConnectionsImpl <T>implements Connections<T> {

    private Map<Client, ConnectionHandler> clients_ConsHandMap;
    private Map<Integer,Client> clientMap;
    private Map<String, List<Integer>> topics_subsMap;





    @Override
    /** uses the ConnectionHandler's send methods of this {@param} connectoinId to flush() the {@param} msg  */
    public boolean send(int connectionId, T msg) throws IOException {
        Client c = clientMap.get(connectionId);
        clients_ConsHandMap.get(c).send(msg);
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

    public Map<Client,ConnectionHandler> getClients_ConsHandMap() {
        return clients_ConsHandMap;
    }

    public void setClients_ConsHandMap(Map<Client, ConnectionHandler> clients_ConsHandMap) {
        this.clients_ConsHandMap = clients_ConsHandMap;


    }
    public Map<Integer, Client> getClientMap() {
        return clientMap;
    }

    public void setClientMap(Map<Integer, Client> clientMap) {
        this.clientMap = clientMap;
    }


    public Map<String, List<Integer>> getTopics_subsMap() {
        return topics_subsMap;
    }
}
