CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system

all: EchoClient
	g++ -o  bin/connectionHandler.o bin/KeyBoardThread.o $(LDFLAGS)

EchoClient: bin/connectionHandler.o bin/KeyBoardThread.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/KeyBoardThread.o: src/KeyBoardThread.cpp
	g++ $(CFLAGS) -o bin/KeyBoardThread.o src/KeyBoardThread.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
