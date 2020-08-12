#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
#include "User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"

class Session;
class Watchable;

enum ActionStatus{
	PENDING, COMPLETED, ERROR
};


class BaseAction{
public:
	BaseAction();
	 virtual ~BaseAction();
	ActionStatus getStatus() const;
	virtual void act(Session& sess)=0;
	virtual std::string toString() const=0;
	virtual BaseAction* duplicate()=0;
protected:
	void complete();
	void error(const std::string& errorMsg);
	std::string getErrorMsg() const;
private:
	std::string errorMsg;
	ActionStatus status;
};

class CreateUser  : public BaseAction {
public:
    CreateUser(std::string name, std::string algo);
    virtual BaseAction* duplicate();

    virtual void act(Session& sess);
	virtual std::string toString() const;
private:
    std::string name;
    std::string algo;
};

class ChangeActiveUser : public BaseAction {
public:
    ChangeActiveUser(std::string& userName);
    virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* duplicate();

private:
    std::string name;

};

class DeleteUser : public BaseAction {
public:
    DeleteUser(std::string& name);
    virtual BaseAction* duplicate();

    virtual void act(Session & sess);
	virtual std::string toString() const;
private:
    std::string name;

};


class DuplicateUser : public BaseAction {
public:
    DuplicateUser(std::string& existName,std::string& newName);
	virtual void act(Session & sess);
	virtual std::string toString() const;
    virtual BaseAction* duplicate();

private:
    std::string existName;
    std::string newName;

};

class PrintContentList : public BaseAction {
public:
    PrintContentList();
	virtual void act (Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* duplicate();

};

class PrintWatchHistory : public BaseAction {
public:
    PrintWatchHistory();
	virtual void act (Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* duplicate();

};


class Watch : public BaseAction {
public:
    Watch (std::string contentID);
	virtual void act(Session& sess);
	virtual std::string toString() const;

    virtual BaseAction* duplicate();

private:
    int contentID;

};


class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();
	virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* duplicate();

};

class Exit : public BaseAction {
public:
    Exit();
	virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* duplicate();

};
#endif
