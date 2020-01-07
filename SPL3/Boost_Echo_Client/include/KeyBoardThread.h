//
// Created by zivsini@wincs.cs.bgu.ac.il on 07/01/2020.
//

#ifndef SPL3_TASK_H
#define SPL3_TASK_H
#include "../include/KeyBoardThread.h"
#include <iostream>
#include <mutex>
#include <thread>
#include <unordered_map>
#include <boost/algorithm/string.hpp>
#include "../include/connectionHandler.h"


using namespace std;


class KeyBoardThread {
private:
    int unordered_map<string,int> subs_id_map;
    int subscription_id; // might need to lock this, DK
    int disconnect_id; // might need to lock this, DK
    std::mutex & _mutex;
    ConnectionHandler handler;
    void login(vector<string>);
    string userName;
    void join(vector<string>);
    void exit(vector<string>);
    void logout();
    void add(vector<string>);
    void borrow(vector<string>);
    void fReturn(vector<string>);
    void status(vector<string>);
    string userName;

public:
    KeyBoardThread (int id, std::mutex& mutex) : _id(id), _mutex(mutex) {}
    void login();
};


#endif //SPL3_TASK_H
