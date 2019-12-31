package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;

import java.util.function.Supplier;

public class TPCserver <T>extends BaseServer<T> {

    private Connections<T> connections;


    public TPCserver(int port, Supplier<MessagingProtocol<T>> protocolFactory, Supplier<MessageEncoderDecoder<T>> encdecFactory,Connections<T> connections) {
        super(port, protocolFactory, encdecFactory);
        this.connections=connections;
    }

    @Override
    protected void execute(BlockingConnectionHandler<T> handler) {
        new Thread(handler).start();

    }
}
