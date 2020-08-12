package bgu.spl.mics;
//import javafx.util.Pair;

import java.util.List;

public class AgentsAvailableEvent<T> implements Event<T> {


    // Fields
    private Future<T> future=new Future<>();
    private List<String> agentsSerialNum;

    public int getmId() {
        return mId;
    }

    private int mId;


    // Constructor
    public AgentsAvailableEvent(List<String> agentsSerialNum,int mId){
        this.agentsSerialNum=agentsSerialNum;
        this.mId=mId;
    }

    public List<String> getAgentsSerialNum() {
        return agentsSerialNum;
    }

    public  Future<T> getFuture(){
        return future;}

        public void setFuture(Future<T> fut){
        this.future=fut;
        }
}
