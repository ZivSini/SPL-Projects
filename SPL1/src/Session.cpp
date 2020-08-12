//
// Created by idansch14 on 20/11/2019.
//
#include <string>
#include <vector>
#include "../include/Action.h"
#include <iterator>
#include "../include/json.hpp"
#include <unordered_map>
#include <fstream>
#include "../include/User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"
#include "../include/Action.h"


using json = nlohmann::json;
using namespace std;


class User;
class Watchable;
class Action;


Session::Session(const std::string &configFilePath): configFilePath(configFilePath), content(),actionsLog(),userMap(),activeUser() {

    using json = nlohmann::json;
    std::ifstream i(configFilePath);
    json j;
    i >> j;
    j.dump(1);
    i.close();
    long id = 1;
    Watchable* w;
    content.push_back(w);
    for (auto &movie: j["movies"].items()) {
        string name = movie.value()["name"];
        int length = movie.value()["length"];
        vector<string> tags;
        for (auto &tag: movie.value()["tags"].items()) {
            tags.push_back(tag.value());
        }
        Movie *m = new Movie(id, name, length, tags);
        content.push_back(m);
        id++;
    }
    for (auto &x: j["tv_series"].items()) {
        string name = x.value()["name"];
        int length = x.value()["episode_length"];
        vector<string> tags;
        for (auto &tag: x.value()["tags"].items()) {
            tags.push_back(tag.value());
        }
        int seasonNum = 1;
        for (auto &y: x.value()["seasons"].items()) {
            int episodes = y.value();
            for (int p = 1; p <= episodes; p++) {
                //   Episode *episode = ;
                content.push_back(new Episode(id, name, length, seasonNum, p, tags));
                id++;
            }
            seasonNum += 1;
        }
    }
    const string& name = "default";
    const string& algo = "len";
    CreateUser* create_default_user =  new CreateUser(name, algo);
    create_default_user->act(*this);
    activeUser = userMap.find("default")->second;
    delete create_default_user;
}

void Session::addToUserMap(User *userToAdd){
    string name = userToAdd->getName();
    userMap.insert({name,userToAdd});
}

bool Session::UserExists(std::string name) {
    return (userMap.find(name)!=userMap.end());
}

void Session::changeActiveUser(User *newActiveUser) {activeUser = newActiveUser;}

User* Session:: returnUser (string& name){
    User* toReturn = userMap.find(name)->second;
    return toReturn;
}

void Session::deleteFromMap(std::string &name) {userMap.erase(name);}

std::vector<Watchable *> Session::getContent(){return content;}

User *Session::getActiveUser() {return activeUser;}

void Session:: start(){
    std::cout<<"SPLFLIX is now on!"<<std::endl;
    string terminal_input;
    cin >> terminal_input;
    while (terminal_input != "exit"){
        if (terminal_input == "createuser"){ // command to create a new user
            cin >> terminal_input; // gets the name of new user
            string userName = terminal_input;
            cin >> terminal_input; // gets the rec algo of new user
            string userAlgo = terminal_input;
            CreateUser* createNewUser = new CreateUser(userName,userAlgo);
            createNewUser->act(*this);
            this->addToActionLog(createNewUser);
        } else if (terminal_input == "changeuser"){ // command to change active user
            cin >> terminal_input;
            string& userName = terminal_input;
            ChangeActiveUser* chgn = new ChangeActiveUser(userName);
            chgn->act(*this);
            this->addToActionLog(chgn);
        } else if (terminal_input == "deleteuser"){ // command to delete a user
            cin >> terminal_input;
            string userName = terminal_input;
            DeleteUser* del = new DeleteUser(userName);
            del->act(*this);
            this->addToActionLog(del);
        } else if(terminal_input=="dupuser"){
            cin >> terminal_input; // gets the name of exist user to be duplicated
            string existName = terminal_input;
            cin >> terminal_input; // gets the name of new user to be construct
            string newName = terminal_input;
            DuplicateUser* dup = new DuplicateUser(existName, newName);
            dup->act(*this);
            this->addToActionLog(dup);
        } else if (terminal_input=="content"){
            PrintContentList *prnt = new PrintContentList();
            prnt->act(*this);
            this->addToActionLog(prnt);
        } else if (terminal_input=="watchhist"){
            PrintWatchHistory* prnt = new PrintWatchHistory();
            prnt->act(*this);
            this->addToActionLog(prnt);
        }else if (terminal_input=="watch"){
            cin >> terminal_input;
            string contentID = terminal_input;
            Watch* watch = new Watch(contentID);
            watch->act(*this);
        }else if (terminal_input=="log"){
            PrintActionsLog* prnt = new PrintActionsLog();
            prnt->act(*this);
            this->addToActionLog(prnt);
        }
        cin >> terminal_input; // gets the next command and continues the main loop
    }
    Exit* ex = new Exit();
    ex->act(*this);
    this->addToActionLog(ex);
}

vector<BaseAction *> Session::getActionsLog()  {
    vector<BaseAction*> copyLA = this->actionsLog;
    return copyLA;
}

void Session::addToActionLog(BaseAction* action) {
    actionsLog.push_back(action);
}
//
Session::~Session() {
    clean();}

//Copy Constructor
Session::Session( const Session &other):configFilePath(),content(),actionsLog(),userMap(),activeUser() {
    this->copy(other);
}
//Move Constructor
Session::Session(Session &&other):configFilePath(),content(),actionsLog(),userMap(),activeUser() {
    this->move(other);
    // set null
    makeOtherNull(other);
}
//Copy Assignment
Session& Session::operator=(const Session &other) {
    if (this != &other) {
        clean();
        copy(other);
    }
    return *this;
}
//Move Assignment
Session& Session::operator=(Session &&other) {
    if (this != &other) {
        clean();
        this->move(other);
        makeOtherNull(other);
    }
    return *this;
}

void Session::clean() {
    activeUser = nullptr;
    auto it = (content.begin()+1);
    if (*it!= nullptr) {
        while (it != content.end()) {
            delete *it;
            it++;
        }
    }
    for (auto &logs: actionsLog) {
        delete logs;
    }
    for (auto &users : userMap) {
        delete users.second;
    }
    userMap.clear();
    actionsLog.clear();
    content.clear();
}
void Session::copy(const Session &other) {
    auto it = (other.content.begin()+1);
    Watchable* w;
    content.push_back(w);
    while (it!=other.content.end()){
        this->content.push_back((*it)->duplicate());
        it++;
    }
    for (auto &log : other.actionsLog) {
        this->actionsLog.push_back(log->duplicate());
    }
    for (auto &user1 : other.userMap) {
        User* newUser = user1.second->duplicate();
        std::string newName = user1.first;
        newUser->clearHistory();
        userMap[newName] = newUser;
        for (auto &h: user1.second->get_history()){
            long id = h->getId();
            Watchable* toAdd = content.at(id);
            this->userMap.at(user1.first)->addToHistory(toAdd);
        }
    }
    for (auto &user: userMap) {
        if (user.first == (other.activeUser->getName())) {
            this->activeUser = user.second;

        }
    }
}

void Session::makeOtherNull(Session &other) {
    for (auto &log : other.actionsLog) {
        log = nullptr;
    }
    other.activeUser = nullptr;
    for (auto &cont : other.content) {
        cont = nullptr;
    }
    other.content.clear();
    other.actionsLog.clear();
    for (auto &user: other.userMap) {
        user.second = nullptr;
    }
    other.userMap.clear();
}
void Session::move(Session &other) {
    for (auto &cont: other.content) {
        this->content.push_back(cont);
    }
    for (auto &logs: other.actionsLog) {
        this->actionsLog.push_back(logs);
    }
    for (auto &user: other.userMap) {
        userMap[user.first] = user.second;
        if (other.activeUser->getName()==user.first){
            this->activeUser=user.second;
        }
        for (auto &h: user.second->get_history()){
            long id = h->getId();
            Watchable* toAdd = content.at(id);
            if(user.first!=this->activeUser->getName())
                this->userMap.at(user.first)->addToHistory(toAdd);

        }
    }
}
