package bgu.spl.net.api;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;

import java.util.Map;

public class StompProtocol<T> implements StompMessagingProtocol<T> {

    private boolean shouldTerminate =false;
    private Map<String,Integer> topicsSubIdsMap;
    private Connections<String> connections;
    private int connectionId;

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId=connectionId;
        this.connections=connections;


    }

    @Override
    public T process(T msg) {
        String[] stringMsg=((String) msg).split("\n");
        String msgReply = "";
        switch (stringMsg[0]){
            case  ("CONNECT"):{

            }
            case  ("SUBSCRIBE"):{
                int  colonIndex = stringMsg[1].indexOf(":");
                String topic = stringMsg[1].substring(colonIndex+1);
                //TODO: deal with the sub id shit - a.k.a. stringMsg[2]
                colonIndex = stringMsg[2].indexOf(":");
                Integer subId = Integer.parseInt(stringMsg[2].substring(colonIndex));
                this.topicsSubIdsMap.put(topic,subId);
                colonIndex = stringMsg[3].indexOf(":");
                String receiptId = stringMsg[3].substring(colonIndex);
                DataBase.getInstance().addToTopicSubsMap(topic,connectionId);
                msgReply ="RECEIPT \n" +
                        "receipt-id:"+receiptId+"\n\n"+
                        "\u0000";
            }
            case  ("SEND"):{
                String topic = stringMsg[1];
                String sendType = stringMsg[2];
                if (sendType.contains("has added the book")){
                    msgReply="MESSAGE\n" +
                            "subscription:"+this.topicsSubIdsMap.get(topic).toString()+"\n"+
                            "Messege-id:"+getUniqueIdSomehow()+"\n"+
                            "destination:"+topic+"\n\n"+
                            stringMsg[3]+"\n \u0000";

                }

            }
            case ("DISCONNECT"):{

            }


            break;
            default:
                throw new IllegalStateException("Unexpected value: " + stringMsg[0]);
        }



        return (T) msgReply;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }



    public Connections<String> getConnections() {
        return connections;
    }

}
