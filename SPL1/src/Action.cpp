//
// Created by idansch14 on 20/11/2019.
//
#include "../include/Action.h"
#include <string>
#include <iostream>
#include "../include/User.h"
#include <utility>
#include "../include/User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"
#include "../include/Action.h"

using namespace std;


BaseAction::BaseAction():errorMsg(), status(PENDING) {}

void BaseAction:: complete(){
    status=COMPLETED;
}

void BaseAction::error(const std::string &errorMsg) {
    status=ERROR;
    this->errorMsg = errorMsg;
    cout<<errorMsg<<endl;
}

std::string BaseAction::getErrorMsg() const {
    return errorMsg;
}

ActionStatus BaseAction::getStatus() const {
    return status;
}

BaseAction::~BaseAction() = default;


CreateUser::CreateUser(string name, string algo): name(std::move(name)),algo(std::move(algo)){}

string CreateUser:: toString() const
{return "CreateUser "+getErrorMsg();}

void CreateUser::act(Session &sess) {
    if (sess.UserExists(name)){ // user name already exist
        string errorMsg="ERROR - the new user name is already taken";
        error(errorMsg);
    } else {
        if (algo == "len") {
            auto *lenUser ( new LengthRecommenderUser(name));
            sess.addToUserMap(lenUser);
            complete();
        } else if (algo == "rer") {
            auto *rerUser  (new RerunRecommenderUser(name));
            sess.addToUserMap(rerUser);
            complete();
        } else if (algo == "gen") {
            auto *genUser  (new GenreRecommenderUser(name));
            sess.addToUserMap(genUser);
            complete();
        } else { // algo string is wrong
            string errorMsg="ERROR - the recommendation algorithm name is wrong";
            error(errorMsg);
        }
    }
}

BaseAction *CreateUser::duplicate() {
    return new CreateUser(*this);
}

ChangeActiveUser::ChangeActiveUser(string& userName): name(userName) {}

string ChangeActiveUser::toString() const
{return "ChangeActiveUser "+getErrorMsg();}

void ChangeActiveUser::act(Session &sess) {
    if (!sess.UserExists(name)){
        string errorMsg="ERROR - user name doesn't exist";
        error(errorMsg);
    } else {
        User* newUser = sess.returnUser(const_cast<string &>(name));
        sess.changeActiveUser(newUser);
        complete();
    }
}

BaseAction *ChangeActiveUser::duplicate() {
    return new ChangeActiveUser(*this);
}

DeleteUser::DeleteUser(std::string &name):
        name(name){}

std::string DeleteUser::toString() const {
    return "DeleteUser "+getErrorMsg();
}

void DeleteUser::act(Session &sess) {
    if (!sess.UserExists(name)) {
        string erroeMsg = "ERROR - user name doesn't exist";
        error(erroeMsg);
    } else {
        sess.deleteFromMap(const_cast<string &>(name));
        complete();
    }
}

BaseAction *DeleteUser::duplicate() {
    return new DeleteUser(*this);
}

DuplicateUser::DuplicateUser(std::string &existName,std::string &newName):
        existName(existName),newName(newName){}

std::string DuplicateUser::toString() const {return "DuplicateUser  "+getErrorMsg();}

void DuplicateUser::act(Session &sess) {
    if (!sess.UserExists(existName)) { // old user doesn't exist
        string errorMsg = "ERROR - older user name doesn't exist";
        error(errorMsg);
    } else if (sess.UserExists(newName)){ // new user already exist
        string errorMsg = "ERROR - new user name already exist";
        error(errorMsg);
    }else{
        string algo =  sess.returnUser(const_cast<string &>(existName))->getAlgo();
        if (algo == "len") {
            LengthRecommenderUser *lenUser ( new LengthRecommenderUser(newName));
            for (auto& hist: sess.returnUser(existName)->get_history()){
                lenUser->addToHistory(hist);
            }
            sess.addToUserMap(lenUser);
        } else if (algo == "rer") {
            auto *rerUser  (new RerunRecommenderUser(newName));
            for (auto& hist: sess.returnUser(existName)->get_history()){
                rerUser->addToHistory(hist);
            }
            sess.addToUserMap(rerUser);
        } else if (algo == "gen") {
            auto *genUser  (new GenreRecommenderUser(newName));
            for (auto& hist: sess.returnUser(existName)->get_history()){
                genUser->addToHistory(hist);
            }
            sess.addToUserMap(genUser);
            complete();
        } else { // algo string is wrong
            string errorMsg="ERROR - the recommendation algorithm name is wrong";
            error(errorMsg);
        }
    }
}

BaseAction *DuplicateUser::duplicate() {
    return new DuplicateUser(*this);
}

PrintContentList::PrintContentList() {}

std::string PrintContentList::toString() const {
    return "PrintContentList "+getErrorMsg();
}

void PrintContentList::act(Session &sess) {
    std::vector<Watchable*> content = sess.getContent();
    int i = 1; // for the Watchable content id
    for (auto it = content.begin()+1 ; it != content.end(); ++it){
        cout<< i <<". "<< dynamic_cast<Watchable*>(*it)->toString() << endl; //! implement methods
        i++;
    }
    complete();
}

BaseAction *PrintContentList::duplicate() {
    return new PrintContentList(*this);
}

PrintWatchHistory::PrintWatchHistory() {
}

std::string PrintWatchHistory::toString() const {
    return "PrintWatchHistory "+getErrorMsg();
}

void PrintWatchHistory::act(Session &sess) {
    cout << "Watch history for " << sess.getActiveUser()->getName() << endl;
    std::vector<Watchable *> history = sess.getActiveUser()->get_history();
    int i=1;
    for (auto it = history.begin(); it != history.end(); ++it) {
        cout<<i<<". "<< (*it)->toStringShort()<<endl;
        i++;
    }
    complete();
}

BaseAction *PrintWatchHistory::duplicate() {
    return new PrintWatchHistory(*this);
}

Watch::Watch(std::string contentID): contentID(stoi(contentID)) {}

std::string Watch::toString() const {
    return "Watch "+getErrorMsg();
}

void Watch::act(Session &sess) {
    Watchable* watching = sess.getContent().at(contentID);
    cout<<"Watching "<<watching->toStringShort()<<endl;
    sess.getActiveUser()->addToHistory(watching);
    sess.getActiveUser()->addTagstoSet(watching);
    sess.getActiveUser()->addToQ(watching);
    sess.addToActionLog(this);
    Watchable* nextWatchable = watching->getNextWatchable(sess);
    cout<<"We recommend watching "<< nextWatchable->toStringShort() <<" Continue Watching? [y/n]"<<endl;
    string terminal_input;
    cin >> terminal_input;
    if(terminal_input == "y"){
        long id = nextWatchable->getId();
        std::string idS = to_string(id);
        Watch* contiWatch = new Watch(idS);
        contiWatch->act(sess);
    }
}

BaseAction *Watch::duplicate() {
    return new Watch(*this);
}

PrintActionsLog::PrintActionsLog() {
}

std::string PrintActionsLog::toString() const {
    return "PrintActionLog "+getErrorMsg();
}

void PrintActionsLog::act(Session &sess) {
    vector<BaseAction *> actionLog =  sess.getActionsLog();
    for (auto it = actionLog.rbegin(); it != actionLog.rend(); it++)
        if (dynamic_cast<BaseAction*> (*it)->getStatus() == ERROR ) {
            cout << (*it)->toString() << endl;
        }
        else {
            cout << (*it)->toString() << "COMPLETED" << endl;
        }
}

BaseAction *PrintActionsLog::duplicate() {
    return new PrintActionsLog(*this);
}

Exit::Exit(){}

std::string Exit::toString() const {
    return "Exit ";
}

void Exit::act(Session &sess) {
    complete();
}

BaseAction *Exit::duplicate() {
    return new Exit(*this);
}
