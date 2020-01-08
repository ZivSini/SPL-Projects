package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessageEncoderDecoder;
import bgu.spl.net.api.StompProtocol;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.srv.TPCserver;

public class StompServer {

    public static void main(String[] args) {

        int port = 7777; // args[]


       // if (args[the right number]=="tpc){

        TPCserver tpcServer = new TPCserver(
                port,
                ()-> new StompProtocol(),
                StompMessageEncoderDecoder::new
        );

        tpcServer.serve();

    }


}
