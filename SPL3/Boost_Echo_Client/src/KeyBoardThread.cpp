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
        string keyboard_command = msg_input.at(0);
        switch (keyboard_command){
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
            thread handler_thread(ConnectionHandler::run(),handler);
            handler.setUserName(userName);
            string connect_stomp_message = "CONNECT\n" +
                                           "accept-version:1/2\n" +
                                           "host:stomp.cs.bgu.ac.il\n" +
                                           "login:" + userName + "\n" +
                                           "passcode:" + password + "\n\n" +
                                           "\0";
            handler.sendLine(connect_stomp_message);
        }
    }

    void KeyBoardThread::join(vector<string> msg_input){
        string topic = msg_input.at(1);
        string subscribe_stomp_message = "SUBSCRIBE\n" +
                                         "destination:"+ topic+"\n" +
                                         "id:"+subscription_id+"\n" +
                                         "receipt:"+receipt_id+"\n\n"+
                                         "\0";
        handler.sendLine(subscribe_stomp_message);
        this.topic_id_map.insert[topic]=subscription_id;
        this->topic__receiptId_map.insert[topic]=receipt_id;
        handler.add_to_topic_rcpt_map(topic,receipt_id);
        handler.add_to_rcptId_cmmnd_map(receipt_id,"sub");
        subscription_id++;
        receipt_id++;
    }



    void KeyBoardThread::exit(vector<string> msg_input) {
        string topic = msg_input.at(1);
        unordered_map<string,int>::const_iterator iter = topic_id_map.find(topic);
        int id = iter.second;
        unordered_map<string,int>::const_iterator it= topic__receiptId_map.find(topic);
        int receiptId = it.second;
        string unsubscribe_stomp_message = "UNSUBSCRIBE\n" +
                                           "id:" + id + "\n" +
                                           "receipt-id:"+receiptId+"\n\n"+
                                           "\0";
        handler.sendLine(unsubscribe_stomp_message);
        handler.remove_from_rcptId_cmmnd_map(receiptId);
        handler.add_to_rcptId_cmmnd_map(receiptId,"unsub");
        this.topic_id_map.erase(topic);
        this->topic__receiptId_map.erase(topic);
    }

void KeyBoardThread::add(vector<string> msg) {
        string topic = msg.at(1);
        string book_name - msg.at(2);
string sendMsg = "SEND\n"+
        "destination:"+ topic+"\n\n"+
        +userName+" has added the book "+book_name+"\n"+
        "\0";
    handler.sendLine(sendMsg);
    handler.addBook(topic,book_name);
}

    void KeyBoardThread::borrow(vector<string> msg) {
        string topic = msg.at(1);
        string book_name - msg.at(2);
        string sendMsg = "SEND\n"+
                         "destination:"+ topic+"\n\n"+
                         +userName+" wish to borrow "+book_name+"\n"+
                         "\0";
        handler.sendLine(sendMsg);
        handler.addBookToBorrow(book_name);
    }


    void KeyBoardThread::fReturn(vector<string> msg) {
        string sendMsg = "SEND\n"+
                         "destination:"+ msg.at(1)+"\n\n"+
                         "Returning "+msg.at(2)+" to"+handler.getBookPrev(msg.at(2)) +"\n"+   /** get the userName we took the book from  */
                         "\0";
        handler.sendLine(sendMsg);
    }

    void KeyBoardThread::status(vector<string> msg) {
        string sendMsg = "SEND\n"+
                         "destination:"+ msg.at(1)+"\n\n"+
                         "book status"+"\n"+
                         "\0";
        handler.sendLine(sendMsg);
    }

    void KeyBoardThread::logout() {
        string disconnect_stomp_message = "DISCONNECT\n" +
                                          "receipt-id:" + receipt_id + "\n\n" +
                                          "\0";
        handler.add_to_rcptId_cmmnd_map(receipt_id,"discon");
        handler.sendLine(disconnect_stomp_message);
        this->receipt_id++;


    }
};