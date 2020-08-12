 
# All Targets
all: splflix

# Tool invocations
# Executable "splflix" depends on the files splflix.o and run.o.
splflix: bin/Session.o bin/Watchable.o bin/User.o bin/Action.o bin/Main.o
	 
	g++ -o bin/splflix bin/Session.o  bin/User.o bin/Action.o bin/Main.o bin/Watchable.o
	

# Depends on the source and header files
bin/Session.o: src/Session.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Session.o src/Session.cpp
	
# Depends on the source and header files
bin/User.o: src/User.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/User.o src/User.cpp
# Depends on the source a nd header files 
bin/Action.o: src/Action.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Action.o src/Action.cpp
# Depends on the source and header files
bin/Main.o: src/Main.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Main.o src/Main.cpp
	# Depends on the source and header files
bin/Watchable.o: src/Watchable.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Watchable.o src/Watchable.cpp
#Clean the build directory
clean: 
	rm -f bin/*
