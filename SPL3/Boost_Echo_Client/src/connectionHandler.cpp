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
            switch (stomp_command) {
                case "CONNECTED": cout<< "Login successful";
                case "MESSAGE":
                    string msg_body=answer_vector.at(5);
                    size_t pos = msg_body.find("borrow");
                    string book_name = msg_body.substr(pos+7,msg_body.size()-1);
                    string topic = answer_vector.at(3);
                    int posOfColon = topic.find(":");
                    topic=topic.substr(posOfColon);
                    if(msg_body.find("borrow")!=-1)
                    {
                        unordered_map<string,list<string>*>::const_iterator it = topic_books_map.find(topic);
                        if (it!=topic_books_map.end()) {
                            list<basic_string<char>> *p = std::find (it->second, it->second + it->second->size(), book_name);
                            if (p != it->second+it->second->size())
                            {
                                string sendMsg = "SEND\n"
                                                 "destination:"+ topic+"\n\n"+
                                                 userName+" has "+book_name+"\n"+
                                                 "\0";
                                sendLine(sendMsg);
                                break;
                            }
                case "RECEIPT":{
                    string full_receipt_id = answer_vector.at(1);
                    int indexColon = full_receipt_id.find(":");
                    int receipt_id = stoi(full_receipt_id.substr(indexColon));
                    unordered_map<int ,string>::const_iterator it = receiptId_command_map.find(receipt_id);
                    string command = it->second;
                    switch (command){
                        case "discon":;
                        case "sub":{
                            unordered_map<int,string>::const_iterator it = receiptId_topic_map.find(receipt_id);
                            string topic = it->second;
                            cout<<"Joined club "+topic<<endl;}
                        case "unsub":{
                            unordered_map<int,string>::const_iterator it = receiptId_topic_map.find(receipt_id);
                            string topic = it->second;
                            cout<<"Exited club "+topic<<endl;}
                    }

                        }
                    }
                    if(msg_body.find("has")!=-1)
                    {
                        size_t pos = msg_body.find("has");
                        string userHasBook = msg_body.substr(pos+4,msg_body.size()-1);
                        for(string s:booksToBorrow)
                        {
                            if(s==book_name)
                            {
                                booksToBorrow.remove(book_name);
                                string sendMsg = "SEND\n"
                                                 "destination:"+ topic+"\n\n"+
                                                 "Taking "+book_name+"from "+userHasBook+"\n"+
                                                 "\0";
                                sendLine(sendMsg);
                                topic_books_map.at(topic)->push_back(book_name);
                                books_prevOwner_map[book_name]= userHasBook;
                                break;
                            }
                        }

                    }
                    if(msg_body.find("Taking")!=-1)
                    {
                        size_t pos = msg_body.find("from");
                        string user = msg_body.substr(pos+5,msg_body.size()-1);
                        if(user==this->userName)
                        {
                            topic_books_map.at(topic)->remove(book_name);
                        }
                        break;
                    }
                    if(msg_body.find("Returning")!=-1)
                    {
                        size_t pos = msg_body.find("to");
                        string user = msg_body.substr(pos+3,msg_body.size()-1);
                        if(user==this->userName)
                        {
                            topic_books_map.at(topic)->push_back(book_name);
                        }
                        break;
                    }
                    if(msg_body.find("book status")!=-1)
                    {
                        string booksList;
                        list<string>* booksInTheTopic = topic_books_map.at(topic);
                        for (string s: *booksInTheTopic)
                        {
                            booksList+=s+",";
                        }
                        booksList=booksList.substr(0,booksList.size()-2);
                        string sendMsg = "SEND\n"
                                         "destination:"+ topic+"\n\n"+
                                         booksList+"\n"+
                                         "\0";
                        sendLine(sendMsg);
                        break;
                    }

                }
                case "ERROR": {
                    int indexColon = answer_vector.at(1).find(":");
                    string error_msg = answer_vector.at(1).substr(indexColon);
                    cout << error_msg << endl;
                }
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
    if (it == topic_books_map.end()) {
        list<string> *book_list = new list<string>;
        book_list->push_back(book_name);
        topic_books_map[topic] = book_list;
    }else{
        it->second->push_back(book_name);

    }


}

void ConnectionHandler::add_to_topic_rcpt_map(string topic, int receipt_id) {
    receiptId_topic_map[receipt_id]=topic;

}

void ConnectionHandler::add_to_rcptId_cmmnd_map(int id, string command) {
    receiptId_command_map[id]=command;

}

void ConnectionHandler::remove_from_rcptId_cmmnd_map(int id) {
    this->receiptId_command_map.erase(id);

}






void ConnectionHandler::setUserName(string name) {
    this->userName=name;

}

void ConnectionHandler::addBookToBorrow(string bookName) {
    booksToBorrow.push_back(bookName);
}

void ConnectionHandler::removeBookToBorrow(string bookName) {
    booksToBorrow.remove(bookName);
}

