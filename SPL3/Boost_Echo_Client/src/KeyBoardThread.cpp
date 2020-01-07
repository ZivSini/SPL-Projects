//
// Created by zivsini@wincs.cs.bgu.ac.il on 07/01/2020.
//

#include "../include/KeyBoardThread.h"
#include <iostream>
#include <mutex>
#include <thread>
#include <boost/algorithm/string.hpp>
#include "../include/connectionHandler.h"

using namespace std;
class KeyBoardThread{

    void runKeyBoard(){
        string input;
        cin>> input;
        std::vector<std::string> msg_input ;
        boost::split(msg_input, input, boost::is_any_of(" "));
        string first_word = msg_input.at(0);
        switch (first_word){
            case "login":{login(msg_input);}
            case "join": join();
            case "exit": exit();
            case "add": add(msg_input);
            case "borrow": borrow(msg_input);
            case "return": fReturn(msg_input);
            case "status":status(msg_input);
            case "logout":logout();
        }



    }




    void KeyBoardThread::login(vector<string> msg_input) {
        string input_host_port = msg_input.at(1)
        vector<string> host_port;
        boost::split(host_port, input_host_port, boost::is_any_of(":"));
        string host = host_port.at(0);
        string port = host_port.at(1);
        userName = msg_input.at(2);
        string password = msg_input.at(3);
        handler = ConnectionHandler(host, port);
        if (!handler.connect()) {
            cout >> "Could not connect to server" >> endl;
        } else {
            string connect_stomp_message = "CONNECT\n" +
                                           "accept-version:1/2\n" +
                                           "host:stomp.cs.bgu.ac.il\n" +
                                           "login:" + userName + "\n" +
                                           "passcode:" + password + "\n\n" +
                                           "\0";
            handler.send(connect_stomp_message);
        }
    }

    void KeyBoardThread::join(vector<string> msg_input){
        string topic = msg_input.at(1);
        string subscribe_stomp_message = "SUBSCRIBE\n" +
                                         "destination:"+ topic+"\n" +
                                         "id:"+subscription_id+"\n\n" +
                                         "\0";
        handler.send(subscribe_stomp_message);
        this.subs_id_map.insert(topic,subscription_id);
        this.subs_id_map.insert[topic]=subs_id_map;
        subscription_id++;
    }




    void KeyBoardThread::exit(vector<string> msg_input) {
        string topic = msg_input.at(1);
        unordered_map<string,int>const_iterator got = subs_id_map.find (topic);
        int id = got.second;
        string unsubscribe_stomp_message = "UNSUBSCRIBE\n" +
                                           "id:" + id + "\n\n" +
                                           "\0";
        handler.send(unsubscribe_stomp_message);
        this.subs_id_map.erase(topic);
    }

void KeyBoardThread::add(vector<string> msg) {
string sendMsg = "SEND\n"+
        "destination:"+ msg.at(1)+"\n\n"+
        +userName+" has added the book "+msg.at(2)+"\n"+
        "\0";
    handler.send(msg);
}

void KeyBoardThread::borrow(vector<string> msg) {
    string sendMsg = "SEND\n"+
                     "destination:"+ msg.at(1)+"\n\n"+
                     +userName+" wish to borrow "+msg.at(2)+"\n"+
                     "\0";
    handler.send(msg);
}


void KeyBoardThread::fReturn(vector<string> msg) {
    string sendMsg = "SEND\n"+
                     "destination:"+ msg.at(1)+"\n\n"+
                     "Returning "+msg.at(2)+" to"+handler.getBookPrev(msg.at(2)) +"\n"+   /** get the userName we took the book from  */
                     "\0";
    handler.send(msg);
}

void KeyBoardThread::status(vector<string> msg) {
    string sendMsg = "SEND\n"+
                     "destination:"+ msg.at(1)+"\n\n"+
                     "book status"+"\n"+
                     "\0";
    handler.send(msg);
}

};




    void KeyBoardThread::logout() {
        string disconnect_stomp_message = "UNSUBSCRIBE\n" +
                                          "id:" + disconnect_id + "\n\n" +
                                          "\0";
        handler.send(disconnect_stomp_message);
        this->disconnect_id++;


    }
};