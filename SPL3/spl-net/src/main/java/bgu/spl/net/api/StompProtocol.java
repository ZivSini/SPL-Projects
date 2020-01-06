package bgu.spl.net.api;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ReplyMessage;


import java.io.IOException;
import java.util.Map;

public class StompProtocol<T> implements StompMessagingProtocol<T> {

    private boolean shouldTerminate =false;
    private Map<String,Integer> topics_IdsMap; /** Hold the subscription id for each topic subscribed to */
    private Connections<T> connections;
    private int connectionId;

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connectionId=connectionId;
        this.connections=connections;
    }

    @Override
    public T process(T msg) throws IOException {
        String[] stringMsg=((String) msg).split("\n");
        ReplyMessage msgReply = new ReplyMessage();
        String msgToReply = "";
        switch (stringMsg[0]){
            case  ("CONNECT"):{


            }
            case  ("SUBSCRIBE"):{
                int  colonIndex = stringMsg[1].indexOf(":");
                String topic = stringMsg[1].substring(colonIndex+1);
                msgReply.setTopic(topic);
                //TODO: deal with the sub id shit - a.k.a. stringMsg[2]
                colonIndex = stringMsg[2].indexOf(":");
                Integer subscriptionId = Integer.parseInt(stringMsg[2].substring(colonIndex));
                this.topics_IdsMap.put(topic,subscriptionId);
                colonIndex = stringMsg[3].indexOf(":");
                String receiptId = stringMsg[3].substring(colonIndex);
                connections.getTopics_subsMap().get(topic).add(connectionId);
                msgToReply ="RECEIPT \n" +
                        "receipt-id:"+receiptId+"\n\n"+
                        "\u0000";
                msgReply.setMsg(msgToReply);

            }
            case  ("SEND"):{
                String topic = stringMsg[1];
                msgReply.setTopic(topic);
                String sendType = stringMsg[2];
                if (sendType.contains("has added the book")){
                    msgToReply="MESSAGE\n" +
                            "subscription:"+this.topics_IdsMap.get(topic).toString()+"\n"+
                            "Messege-id:"+getUniqueIdSomehow()+"\n"+
                            "destination:"+topic+"\n\n"+
                            stringMsg[3]+"\n \u0000";

                }
                msgReply.setMsg(msgToReply);

            }
            case ("DISCONNECT"):{

            }


            break;
            default:
                throw new IllegalStateException("Unexpected value: " + stringMsg[0]);
        }

        connections.send(msgReply.getTopic(),msgReply.getMsg());

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
