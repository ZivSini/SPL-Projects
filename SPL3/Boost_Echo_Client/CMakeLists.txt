cmake_minimum_required(VERSION 3.15)
project(SPL3)

set(CMAKE_CXX_STANDARD 11)

add_executable(SPL3 src/connectionHandler.cpp src/echoClient.cpp include/connectionHandler.h src/KeyBoardThread.cpp include/KeyBoardThread.h src/Client.cpp include/Client.h)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}-g -Wall -Weffc++ -std=c++11")

find_package(Boost 1.56 REQUIRED COMPONENTS
        date_time filesystem iostreams)
target_link_libraries(SPL3 Boost::date_time Boost::filesystem Boost::iostreams)