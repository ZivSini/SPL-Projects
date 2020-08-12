//
// Created by idansch14 on 20/11/2019.
//
#include "../include/User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"
#include "../include/Action.h"

using namespace std;
#include <cstdlib>
#include <algorithm>
#include <utility>
#include <vector>
#include <queue>
#include <string>
//#include <iostream>

//#include <unordered_set>
//#include <unordered_map>
#include <set>
//#include <utility>
#include <cmath>

User::User(string name):history(),name(std::move(name)),GenreMap(){}

string User::getName() const
{
    return  string (name);
}
vector<Watchable *> User::get_history() const
{
    return vector<Watchable*> (history);
}
void User:: addToHistory(Watchable* watched)
{
    history.push_back(watched);
}
void User::clearHistory() {
    history.clear();
    history.shrink_to_fit();
}

//// dasha

//copy constructor
User::User(const User &other):history(),name(other.name),GenreMap(){
    for(auto &cont:other.history){
        this->history.push_back(cont);
    }
}

//move constructor
User::User(User &&other):history(),name(other.name),GenreMap(){
    for(auto &hist: other.history){
        this->history.push_back(hist);
        hist= nullptr;
    }
}
//assignment operator
User& User::operator=(const User &other) {
    if (this != &other) {
        for(auto &hist: history){
            hist= nullptr;
        }
        history.clear();
        for(auto &hist: other.history){
            history.push_back(hist);
        }
    }
    return *this;
}
//move assignment operator
User& User::operator=(User &&other) {
    if (this != &other) {
        for(auto &hist: history){
            hist= nullptr;
        }
        history.clear();
        for(auto &hist: other.history){
            history.push_back(hist);
        }
        for(auto &hist: other.history){
            hist= nullptr;
        }
        other.history.clear();
    }
    return *this;
}
User::~User() {
    for(auto &h: history)
        h= nullptr;
    history.clear();
}

LengthRecommenderUser::LengthRecommenderUser( const std::string &name): User (name),algo("len"){}


Watchable* LengthRecommenderUser::getRecommendation(Session& s)
{
    double average =0;
    vector<Watchable*> content = s.getContent();
    int i=0;
    for(auto& hist :history)
    {
        int length = hist->getLength();
        average = average+length;
        i++;
    }
    average=average/i;

    Watchable* recommend;
    double  x=INFINITY; // so it will get inside the "if" statement in the first time
    content.erase(content.begin());
    for(auto& cont:content)
    {
        if(std::find(history.begin(),history.end(),cont)==history.end())
        {
            double length=cont->getLength();
            if(abs(length-average)<x)
            {
                recommend=cont;
                x=length-average;
            }
        }
    }
    if(x==INFINITY)
    {
        return nullptr;
    }
    return recommend;
}

const string &LengthRecommenderUser::getAlgo() const {
    return algo;
}

void LengthRecommenderUser::addToQ(Watchable *) {}

void LengthRecommenderUser::addTagstoSet(Watchable *) {}

User *LengthRecommenderUser::duplicate() {
    return new LengthRecommenderUser(*this);
}

RerunRecommenderUser::RerunRecommenderUser(const std::string& name): User (name),q(),algo("rer")
{}

Watchable* RerunRecommenderUser::getRecommendation(Session& s)    //add watch to queue
{
    Watchable *recommend = q.front();
    q.pop();
    q.push(recommend);
    return recommend;

}

const string &RerunRecommenderUser::getAlgo() const {
    return algo;
}

void RerunRecommenderUser::addToQ(Watchable* watched) {
    if(std::find(history.begin(),history.end(),watched)!=history.end())
    {
        q.push(watched);
    }
}

void RerunRecommenderUser::addTagstoSet(Watchable *) {

}

User *RerunRecommenderUser::duplicate() {
    return new RerunRecommenderUser(*this);
}


GenreRecommenderUser::GenreRecommenderUser(const std::string& name): User (name),GenreSet(),algo("gen"){}

Watchable* GenreRecommenderUser::getRecommendation(Session& s)
{
    Watchable* recommend;
    vector<Watchable*> content =s.getContent();
    std::set<pair<int,string>>::iterator iter = GenreSet.begin();
    while(iter != GenreSet.end())
    {
        pair<int, string> p = *iter;
        content.erase(content.begin());
        for(auto& cont:content)
        {
            vector<string> tags=cont->getTags();
            if (find(history.begin(), history.end(), cont)==history.end())
            {
                if (find(tags.begin(),tags.end(),p.second)!=tags.end())
                {
                    recommend = cont;
                    return recommend;
                }
            }
        }
        iter.operator++();
    }
    return nullptr;
}

const string &GenreRecommenderUser::getAlgo() const {
    return algo;
}

void GenreRecommenderUser::addToQ(Watchable *) {}

void GenreRecommenderUser::addTagstoSet(Watchable*  w)
{
    vector<string> tags = w->getTags();
//    for(int i=0;i<tags.size();i++)
    for(auto& t: tags)
    {
        bool found=false;
        auto revIt = GenreSet.rbegin();
        while(revIt!=GenreSet.rend())
        {
            pair<int, string> p = *revIt;

            if(p.second==t)
            {
                pair<int,string> updatepair;
                updatepair.first=p.first-1;
                updatepair.second=p.second;
                GenreSet.insert(updatepair);
                GenreSet.erase(p);
                found=true;
            }
            revIt.operator++();
        }
        if(!found)
        {
            pair<int,string> newpair;
            newpair.first=999999999;
            newpair.second=t;
            GenreSet.insert(newpair);
        }
    }
}

User *GenreRecommenderUser::duplicate() {
    return new GenreRecommenderUser(*this);
}
