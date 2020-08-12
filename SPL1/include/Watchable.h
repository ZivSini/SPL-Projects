#ifndef WATCHABLE_H_
#define WATCHABLE_H_

#include <string>
#include <vector>
using namespace std;
#include <string>
#include <iostream>
#include "../include/Session.h"
#include "../include/User.h"

class Session;

class Watchable{
public:
	Watchable(long id, int length, const std::vector<std::string>& tags);
	virtual ~Watchable();
	virtual std::string toString() const = 0;
	virtual Watchable* getNextWatchable(Session&) const = 0;
	int getLength() const;
    long getId() const;
    virtual string &getMovieOrEpisode()=0;
    vector<string> getTags() const;
    virtual string getName()=0;
    virtual string toStringShort()=0;
    virtual Watchable* duplicate()=0;

private:
    const long id;
    int length;
	std::vector<std::string> tags;


};



class Movie : public Watchable{
public:
	Movie(long id, std::string  name, int length, const std::vector<std::string>& tags);
	Movie (Movie* other);
	std::string toString() const;
	Watchable* getNextWatchable(Session&) const;
	string & getMovieOrEpisode();
	string getName();
    string toStringShort();
    virtual Watchable* duplicate();
private:
	std::string name;
	string MovieOrEpisode;
};



class Episode: public Watchable{
public:
	Episode(long id, const std::string& seriesName,int length, int season, int episode ,const std::vector<std::string>& tags);
    Episode (Episode* other);
	std::string toString() const;
	Watchable* getNextWatchable(Session&) const;
	string & getMovieOrEpisode();
    string getName();
    string toStringShort();
    virtual Watchable* duplicate();
    int getSeason() const;
    int getEpisode() const;

private:
	std::string seriesName;
	int season;
	int episode;
    long nextEpisodeId;
	string MovieOrEpisode;
};

#endif
