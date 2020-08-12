/*
 * Wacthable.cpp
 *
 *  Created on: 21 Nov 2019
 *      Author: ziv
 */

using namespace std;
#include <vector>
#include <string>
#include "../include/User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"
#include "../include/Action.h"



Watchable::Watchable(long id, int length, const std::vector<std::string> &tags):
        id(id),length(length),tags(tags) {
}
/// Destructor
Watchable::~Watchable() {
    tags.clear();
    length=0;

}

long Watchable::getId() const
{
    return this->id;
}

int Watchable::getLength() const
{
    return this->length;
}
vector<string> Watchable::getTags() const {
    return tags;
}

//! MOVIE
Movie::Movie(long id, std::string  name, int length, const std::vector<std::string>& tags): Watchable(id,length,tags), name(std::move(name)),MovieOrEpisode("Movie")
{}

string Movie::getName() {
    return name;
}
string &Movie::getMovieOrEpisode() {
    return MovieOrEpisode;
}

string Movie::toString() const
{
    string PrintTags = "[";
    const vector<string> tags = getTags();
    for(auto iter =tags.begin();iter!=tags.end();iter++)
    {
        PrintTags =PrintTags+ iter.operator*();
        if(iter!=tags.end()-1)
        {
            PrintTags+= ", ";
        }
    }
    PrintTags= PrintTags + "]";
    string length = to_string(this->getLength());
    return (name + " " + length + " minutes " + PrintTags);
}

Watchable* Movie:: getNextWatchable(Session& s) const
{
    return s.getActiveUser()->getRecommendation(s);
}

string Movie::toStringShort() {
    return name;
}

Watchable *Movie::duplicate() {
    return new Movie(*this);
}

Movie::Movie(Movie *other) :
        Watchable (other->getId(),other->getLength(),other->getTags()),name (other->getName()), MovieOrEpisode("Movie")
{}


//! EPISODE
Episode::Episode(long id, const std::string& seriesName,int length, int season, int episode ,const std::vector<std::string>& tags):Watchable(id,length,tags),seriesName(seriesName),season(season),episode(episode),nextEpisodeId(0),MovieOrEpisode("Episode")
{}

string Episode::getName(){
    return seriesName;
}

string Episode::toString() const
{
    string PrintTags = "[";
    const vector<string> tags = getTags();
    for(auto iter =tags.begin();iter!=tags.end();iter++)
    {
        PrintTags = PrintTags + iter.operator*();
        if(iter!=tags.end()-1)
        {
            PrintTags+= ", ";
        }
    }
    PrintTags= PrintTags + "]";
    string SeasonNumber ;
    if (season >= 10) {
        SeasonNumber = to_string(season);
    } else {
        SeasonNumber = "0" + to_string(season);
    }
    string EpisodeNumber;
    if (episode >= 10) {
        EpisodeNumber = to_string(episode);
    } else {
        EpisodeNumber = "0" + to_string(episode);
    }
    return seriesName + " " + "S" + SeasonNumber + "E" + EpisodeNumber + " " + to_string(this->getLength()) + " minutes " + PrintTags;
}
Watchable* Episode::getNextWatchable(Session& s) const
{
    vector <Watchable*> content =s.getContent();
    vector <Watchable*> history= s.getActiveUser()->get_history();
    if((history.at(history.size()-1)->getMovieOrEpisode()=="Episode")&&
       ((history.at(history.size()-1)->getName()==(content.at(this->getId()+1)->getName()))))
    {
        return content.at(this->getId()+1);
    }
    else
    {
        return s.getActiveUser()->getRecommendation(s);
    }
}
string &Episode::getMovieOrEpisode()
{
    return MovieOrEpisode;
}

string Episode::toStringShort() {
    string SeasonNumber ;
    if (season >= 10) {
        SeasonNumber = to_string(season);
    } else {
        SeasonNumber = "0" + to_string(season);
    }
    string EpisodeNumber;
    if (episode >= 10) {
        EpisodeNumber = to_string(episode);
    } else {
        EpisodeNumber = "0" + to_string(episode);
    }
    return seriesName + " " + "S" + SeasonNumber + "E" + EpisodeNumber ;
}

Watchable *Episode::duplicate() {
    return new Episode(*this);
}

Episode::Episode(Episode *other) : Watchable (other->getId(),other->getLength(),other->getTags()),seriesName (other->getName()),season(other->season),episode(other->getEpisode()), nextEpisodeId(),MovieOrEpisode("Episode")
{}

int Episode::getSeason() const {
    return season;
}

int Episode::getEpisode() const {
    return episode;
}
