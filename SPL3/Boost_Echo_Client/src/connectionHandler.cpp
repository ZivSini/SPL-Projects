#include "../include/connectionHandler.h"
#include <boost/algorithm/string.hpp>



using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using namespace std;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
        else
            connected=true;
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\n');
}
 

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
	do{
		if(!getBytes(&ch, 1))
		{
			return false;
		}
		if(ch!='\0')  
			frame.append(1, ch);
	}while (delimiter != ch);
    } catch (std::exception& e) {
	std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
	return false;
    }
    return true;
}
 
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
        connected=false;
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }


}

void ConnectionHandler::run() {


    while (connected){
        string answer_from_server;
        if (!getLine(answer_from_server)) {
            cout << "Disconnected. Exiting...\n" << endl;
            break;
        }else{
            std::vector<std::string> answer_vector ;
            boost::split(answer_vector, answer_from_server, boost::is_any_of(" "));
            string stomp_command = answer_vector.at(0);
            switch (stomp_command){
                case "CONNECTED":
                case "MESSAGE":
                case "RECEIPT":
                case "ERROR":

            }
        }


    }

}

void ConnectionHandler::send(string msg) {

}

string ConnectionHandler::getBookPrevOwner(string book_name) {
    unordered_map<string,string>::const_iterator it = books_prevOwner_map.find(book_name);
    string prev_owner = it->second;
    books_prevOwner_map.erase(book_name);
    return prev_owner;

}

void ConnectionHandler::addBook(string topic, string book_name) {
    unordered_map<string,list<string>*>::const_iterator it = topic_books_map.find(topic);
    if (it==topic_books_map.end()) {
        list<string> *book_list = new list<string>;
        book_list->push_back(book_name);
        topic_books_map[topic] = book_list;
    }else{
        it->second->push_back(book_name);

    }


}

