#ifndef USER_H_
#define USER_H_

#include <vector>
#include <cstdlib>
#include <string>
#include <unordered_set>
#include <unordered_map>
#include <algorithm>
#include <queue>
#include <string>
#include <iostream>
#include "../include/Watchable.h"
#include "../include/Session.h"
#include <set>
#include <utility>

class Watchable;
class Session;

class User{
public:

    User(std::string  name);
    virtual Watchable* getRecommendation(Session& s) = 0;
    std::string getName() const;
    std::vector<Watchable*> get_history() const;
    virtual void addToQ(Watchable*)=0;
    virtual const string &getAlgo() const=0;
    void addToHistory(Watchable*);
    virtual void addTagstoSet(Watchable*)=0;
    virtual User* duplicate()=0;
    User(const User& other);// copy constructor
    User(User &&other);// Move constructor
    User& operator=(const User &other); //Copy Assigment
    User& operator=(User&& other); //Move Assigment
    virtual ~User();//destructor
    void clearHistory();

protected:
    std::vector<Watchable*> history;
private:
    const std::string name;
    std::unordered_map<string,int> GenreMap;
};

class LengthRecommenderUser : public User {
public:
    LengthRecommenderUser(const std::string &name);
    virtual Watchable* getRecommendation(Session& s);
    const string &getAlgo() const;
    void addToQ(Watchable*);
    void addTagstoSet(Watchable*);
    virtual User* duplicate();
private:
    const std::string algo;

};

class RerunRecommenderUser : public User {
public:
    RerunRecommenderUser(const std::string& name);
    virtual Watchable* getRecommendation(Session& s);
    const string &getAlgo() const;
    void addToQ(Watchable*);
    void addTagstoSet(Watchable*);
    virtual User* duplicate();
private:
    queue<Watchable*> q;
    const std::string algo;
};

class GenreRecommenderUser : public User {
public:
    GenreRecommenderUser(const std::string &name);
    void addToQ(Watchable*);
    virtual Watchable *getRecommendation(Session &s);
    const string &getAlgo() const;
    void addTagstoSet(Watchable*);
    virtual User* duplicate();
private:
    std::set<pair<int,string>> GenreSet;
    const std::string algo;
};
#endif
