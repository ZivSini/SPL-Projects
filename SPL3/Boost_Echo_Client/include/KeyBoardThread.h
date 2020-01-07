//
// Created by zivsini@wincs.cs.bgu.ac.il on 07/01/2020.
//

#ifndef SPL3_TASK_H
#define SPL3_TASK_H
#include "../include/KeyBoardThread.h"
#include <iostream>
#include <mutex>
#include <thread>
#include <boost/algorithm/string.hpp>
#include "../include/connectionHandler.h"

using namespace std;


class KeyBoardThread {
private:
    int _id;
    std::mutex & _mutex;
    ConnectionHandler handler;
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
