package bgu.spl.net.api;

import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.IdGetter;
import bgu.spl.net.srv.ReplyMessage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StompProtocol<T> implements StompMessagingProtocol<T> {

    private boolean shouldTerminate =false;
    private Map<Integer,String> subsId_topics_Map; /** Hold the subscription id for each topic subscribed to */
    private Connections<T> connections;
    private int connectionId;

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connectionId=connectionId;
        this.connections=connections;
        this.subsId_topics_Map =new ConcurrentHashMap<>();
    }

    //TODO: should identify the client by name and not id so we cam reconnect him with diffrente connectionId -
    // deal in line 34-end
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
                if (connections.getClientsMap().containsKey(connectionId)) {

                    // wrong password
                    if (connections.getClientsMap().get(connectionId).getPassword() != clientPW) {
                        /** error message "message: " MUST be in second line */
                        msgToReply = "ERROR\n" +
                                "message: Wrong password\n" +
                                "\n" + // end of headers - start of body
                                "MESSAGE\n" +
                                "the password does not match the user account name" +
                                "\u0000";
                        //TODO: DISCONNECT SOMEHOW

                        // user is already logged in
                    } else if (connections.getClientsMap().get(connectionId).isLoggedIn()) {
                        /** error message "message: " MUST be in second line */
                        msgToReply = "ERROR\n" +
                                "message: User already logged in\n" +
                                "\n" + // end of headers - start of body
                                "MESSAGE\n" +
                                "this user is already logged in" +
                                "\u0000";
                        //TODO: DISCONNECT SOMEHOW
                    }

                    // user exist & not logged in & password is correct
                    else {
                        msgToReply = "CONNECTED\n" +
                                version + "\n\n" +
                                "\u0000";
                    }
                }

                // client doesn't exist
                else {
                    Client c = new Client(clientName,clientPW,connectionId);
                    connections.getClientsMap().put(connectionId,c);
                    msgToReply = "CONNECTED\n" +
                            version + "\n\n" +
                            "\u0000";
                }

                msgReply.setMsg(msgToReply);
                connections.send(connectionId,(T)msgToReply);


                break;
            }
            case  ("SUBSCRIBE"):{
                int  colonIndex = stringMsg[1].indexOf(":");
                String topic = stringMsg[1].substring(colonIndex+1);
//                msgReply.setTopic(topic);
//                //TODO: deal with the sub id shit - a.k.a. stringMsg[2]
                colonIndex = stringMsg[2].indexOf(":");
                Integer subscriptionId = Integer.parseInt(stringMsg[2].substring(colonIndex+1));
                if(!connections.getConnId_topic_subId_map().containsKey(connectionId)){ // if the connId doesn't have a topic he is registered to yet
                    Map<String,Integer> topics_subsId_map = new ConcurrentHashMap<>();
                    connections.getConnId_topic_subId_map().put(connectionId,topics_subsId_map); // initialize it
                }
                connections.getConnId_topic_subId_map().get(connectionId).put(topic,subscriptionId); // put the topic and his subsId to the connId map in connections
                this.subsId_topics_Map.put(subscriptionId,topic);
                /** need to add the subscription id of this user to the connectionImpl map that hold all of the subscrip' id of all the client so we can add this id to the message */
           //     connections.getConnId_topic_subId_map().put()
                colonIndex = stringMsg[3].indexOf(":");
                String receiptId = stringMsg[3].substring(colonIndex+1);
                if (!connections.getTopics_subsMap().containsKey(topic)){
                    List<Integer> conn_id_list = new ArrayList<>();
                    connections.getTopics_subsMap().put(topic,conn_id_list);
                }
                connections.getTopics_subsMap().get(topic).add(connectionId);
                /** seccond line MUST be receipt id */
                msgToReply ="RECEIPT\n" +
                        "receipt-id:"+receiptId+"\n\n"+
                        "\u0000";
//                msgReply.setMsg(msgToReply);
                connections.send(connectionId, (T) msgToReply);
                break;

            }
            case  ("UNSUBSCRIBE"):{

                int  colonIndex = stringMsg[1].indexOf(":");
                Integer subscriptionId = Integer.parseInt(stringMsg[1].substring(colonIndex+1));
                String topic = subsId_topics_Map.get(subscriptionId);
                connections.getConnId_topic_subId_map().get(connectionId).remove(topic); // removes this topic from the map that hold all this connectionId topic and their subId numbers.
                colonIndex = stringMsg[2].indexOf(":");
                Integer receiptId = Integer.parseInt(stringMsg[2].substring(colonIndex+1));
                /** seccond line MUST be receipt id */
                msgToReply ="RECEIPT\n" +
                        "receipt:"+receiptId+"\n\n"+
                        "\u0000";
//                msgReply.setMsg(msgToReply);
                Integer connId = connectionId; // so we can use the remove() method with connId as Object and not as index
                this.connections.getTopics_subsMap().get(topic).remove(connId);/** unsubscribe from the topic */
                System.out.println("got to line 141 in stomp protocol unsub");
                connections.send(connectionId, (T) msgToReply);
                break;
            }
            case  ("SEND"):{
                String topic = stringMsg[1];
                int colonIndex = topic.indexOf(":");
                topic = topic.substring(colonIndex+1);
                msgReply.setTopic(topic);
                Integer subsctipId = connections.getConnId_topic_subId_map().get(connectionId).get(topic);
                String sendType = stringMsg[2];
                msgToReply="MESSAGE\n" +
                        "general_subscipsId\n"+
                        "Messege-id:"+ IdGetter.getInstance().getMsgId() +"\n"+
                        "destination:"+topic+"\n\n"+
                        stringMsg[3]+"\n \u0000";


                msgReply.setMsg(msgToReply);
                connections.send(msgReply.getTopic(), (T) msgReply.getMsg());
                break;
            }
            case ("DISCONNECT"):{
                for(String topic: this.connections.getTopics_subsMap().keySet())
                {
                    if (connections.getTopics_subsMap().get(topic).contains(connectionId)) {
                        Integer connId = connectionId; // so we can use the remove() method with connId as Object and not as index
                        this.connections.getTopics_subsMap().get(topic).remove(connId);
                    }
                    connections.getClientsMap().get(connectionId).setLoggedIn(false);   // change loggedIn for this client to false so we can relog him once again with the same name and password
                }
                int colonIndex = stringMsg[1].indexOf(":");
                String receiptId = stringMsg[1].substring(colonIndex+1);
                /** seccond line MUST be receipt id */
                msgToReply="RECEIPT\n" +
                        "receipt-id:"+receiptId+"\n\n"
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



    public Connections<T> getConnections() {
        return connections;
    }

}
