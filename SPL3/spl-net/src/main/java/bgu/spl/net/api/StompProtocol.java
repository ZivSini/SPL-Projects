package bgu.spl.net.api;

import bgu.spl.net.srv.Client;
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
            case  ("CONNECT"): {
                String version = stringMsg[1].substring(6); // stringMsg[1].substring(0,6)="accept-"
                String clientName = stringMsg[3].substring(5); // stringMsg[3].substring(0,5)="login:"
                String clientPW = stringMsg[4].substring(8); // stringMsg[4].substring(0,8)="password:"


                //TODO: HANDLE SOCKET ERROR


                // client exist
                if (connections.getClientMap().containsKey(connectionId)) {

                    // wrong password
                    if (connections.getClientMap().get(connectionId).getPassword() != clientPW) {
                        msgToReply = "ERROR \n" +
                                "message: wrong password\n" +
                                "\n" + // end of headers - start of body
                                "MESSAGE\n" +
                                "the password does not match the user account name" +
                                "\u0000";
                        //TODO: DISCONNECT SOMEHOW

                        // user is already logged in
                    } else if (connections.getClientMap().get(connectionId).isLoggedIn()) {
                        msgToReply = "ERROR \n" +
                                "message: user is already logged in\n" +
                                "\n" + // end of headers - start of body
                                "MESSAGE\n" +
                                "this user is already logged in" +
                                "\u0000";
                        //TODO: DISCONNECT SOMEHOW
                    }

                    // user exist & not logged in & password is correct
                    else {
                        msgToReply = "CONNECTED \n" +
                                version + "\n\n" +
                                "\u0000";
                    }
                }

                // client doesn't exist
                else {
                    Client c = new Client(clientName,clientPW,connectionId);
                    connections.getClientMap().put(connectionId,c);
                    msgToReply = "CONNECTED \n" +
                            version + "\n\n" +
                            "\u0000";
                }

                        msgReply.setMsg(msgToReply);
                connections.send(connectionId,(T)msgToReply);



            }
            case  ("SUBSCRIBE"):{
                int  colonIndex = stringMsg[1].indexOf(":");
                String topic = stringMsg[1].substring(colonIndex+1);
//                msgReply.setTopic(topic);
//                //TODO: deal with the sub id shit - a.k.a. stringMsg[2]
                colonIndex = stringMsg[2].indexOf(":");
                Integer subscriptionId = Integer.parseInt(stringMsg[2].substring(colonIndex));
                this.topics_IdsMap.put(topic,subscriptionId);
                colonIndex = stringMsg[3].indexOf(":");
                String receiptId = stringMsg[3].substring(colonIndex);
                connections.getTopics_subsMap().get(topic).add(connectionId);
                msgToReply ="RECEIPT \n" +
                        "receipt-id:"+receiptId+"\n\n"+
                        "\u0000";
//                msgReply.setMsg(msgToReply);
                connections.send(connectionId, (T) msgToReply);

            }
            case  ("SEND"):{
                String topic = stringMsg[1];
                msgReply.setTopic(topic);
                String sendType = stringMsg[2];

                msgToReply="MESSAGE\n" +
                        "subscription:"+this.topics_IdsMap.get(topic).toString()+"\n"+
                        "Messege-id:"+getUniqueIdSomehow()+"\n"+
                        "destination:"+topic+"\n\n"+
                        stringMsg[3]+"\n \u0000";


                msgReply.setMsg(msgToReply);
                connections.send(msgReply.getTopic(), (T) msgReply.getMsg());
            }
            case ("DISCONNECT"):{
                for(String topic: this.connections.getTopics_subsMap().keySet())
                {
                    this.connections.getTopics_subsMap().get(topic).remove(connectionId);
                }
                msgToReply="RECIEPT\n" +
                        "receipt-id:"+stringMsg[1].substring(8)
                        +"\u0000";
                connections.send(connectionId, (T) msgToReply);

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
