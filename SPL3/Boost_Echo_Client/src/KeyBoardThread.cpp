//
// Created by zivsini@wincs.cs.bgu.ac.il on 07/01/2020.
//

#include "../include/KeyBoardThread.h"
#include <iostream>
#include <mutex>
#include <thread>
#include <boost/algorithm/string.hpp>

using namespace std;
class KeyBoardThread{
private:
    int _id;
    std::mutex & _mutex;
public:
    KeyBoardThread (int id, std::mutex& mutex) : _id(id), _mutex(mutex) {}

    void runKeyBoard(){
        string input;
        cin>> input;
        std::vector<std::string> msg_input ;
        boost::split(msg_input, input, boost::is_any_of(" "));
        switch (msg_input.at(0)){
            case "login": login();
            case "join": join();
            case "exit": exit();
            case "add": add();
            case "borrow": borrow();
            case "return": fReturn();
            case "status":status();
            case "logout":logout();
        }



}



};

void KeyBoardThread::login() {


}
