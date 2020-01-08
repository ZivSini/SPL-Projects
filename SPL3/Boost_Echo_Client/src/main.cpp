//
// Created by zivsini@wincs.cs.bgu.ac.il on 08/01/2020.
//

#include "../include/KeyBoardThread.h"

int main (int argc, char *argv[]) {
    /// tryout
    cout<<"Client started"<<endl;
//    string input;
//    getline(cin, input);
//    vector<string> v;
//    boost::split(v, input, boost::is_any_of(" "));
//    cout<<v.at(0)<<endl;
//    cout<<v.at(1)<<endl;
// ip:
//    login 132.72.44.32:7777 idan a





    /// real program
    KeyBoardThread* th = new KeyBoardThread();
    thread real_thread(&KeyBoardThread::runKeyBoard,th);
    real_thread.join();

    return 0;
}