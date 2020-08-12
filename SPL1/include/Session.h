#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <unordered_map>
#include <string>
#include "../include/Action.h"
#include "Action.h"
#include "../include/Watchable.h"

class User;
class Watchable;
class BaseAction;

class Session{
public:
    Session(const std::string &configFilePath);
    ~Session(); //Destructor
    Session( const Session& other);// Copy Constructor
    Session(Session &&other);// Move Constructor
    Session& operator=(const Session &other); //Copy Assigment
    Session& operator=(Session &&other); //Move Assigment
    void move(Session &other);
    void addToUserMap (User* userToAdd);
    bool UserExists(std::string name);
    void changeActiveUser(User *newActiveUser);
    User* returnUser (std::string& name);
    void deleteFromMap(std::string& name);
    std::vector<Watchable*> getContent ()  ;
    User* getActiveUser () ;
    vector<BaseAction *> getActionsLog();
    void addToActionLog(BaseAction*);
    void makeOtherNull(Session &other);

    void start();
private:
    std::string configFilePath;
    void copy(const Session &other);
    void clean();
    std::vector<Watchable*> content;
    std::vector<BaseAction*> actionsLog;
    std::unordered_map<std::string,User*> userMap;
    User* activeUser;



};
#endif
