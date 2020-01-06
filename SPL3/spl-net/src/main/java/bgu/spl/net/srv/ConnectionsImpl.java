package bgu.spl.net.srv;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl <T>implements Connections<T> {

    private Map<Integer, ConnectionHandler> clients_ConsHandMap;
    private Map<Integer,Client> clientsMap;
    private Map<String, List<Integer>> topics_subsMap;
    private static ConnectionsImpl single_instance=null;

    private ConnectionsImpl(){
        clients_ConsHandMap=new ConcurrentHashMap<>();
        clientsMap = new ConcurrentHashMap<>();
        topics_subsMap = new ConcurrentHashMap<>();
    }

    public static ConnectionsImpl getInstance(){
        if (single_instance==null)
            single_instance = new ConnectionsImpl();
        return single_instance;
    }




    @Override
    /** uses the ConnectionHandler's send methods of this {@param} connectoinId to flush() the {@param} msg  */
    public boolean send(int connectionId, T msg) throws IOException {
        clients_ConsHandMap.get(connectionId).send(msg);
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

    public Map<Integer,ConnectionHandler> getClients_ConsHandMap() {
        return clients_ConsHandMap;
    }




    public Map<Integer, Client> getClientsMap() {
        return clientsMap;
    }

    public void setClientsMap(Map<Integer, Client> clientsMap) {
        this.clientsMap = clientsMap;
    }


    public Map<String, List<Integer>> getTopics_subsMap() {
        return topics_subsMap;
    }
}
