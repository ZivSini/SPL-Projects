package bgu.spl.net.srv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Connections<T> {

    boolean send(int connectionId, T msg) throws IOException;

    void send(String channel, T msg) throws IOException;

    void disconnect(int connectionId);

    Map<Client, ConnectionHandler> getClients_ConsHandMap() ;

    Map<String, List<Integer>> getTopics_subsMap() ;

    public Map<Integer, Client> getClientMap();



    }
