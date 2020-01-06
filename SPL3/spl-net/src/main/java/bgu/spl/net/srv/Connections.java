package bgu.spl.net.srv;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, T msg) throws IOException;

    void send(String channel, T msg) throws IOException;

    void disconnect(int connectionId);
}
