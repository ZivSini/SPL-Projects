package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class MissionReceivedEvent<T> implements Event<T> {
    private MissionInfo mission;
    private Future<T> future;

    public int getIntelId() {
        return intelId;
    }

    private int intelId;

    public MissionReceivedEvent(MissionInfo mission,int intelId){
        this.mission=mission;
        this.future =  null;
        this.intelId=intelId;

    }


    public MissionInfo getMission(){
        return mission;
    }

    public Future<T> getFuture(){return future;}
    public void setFuture(Future<T> fut){
        this.future=fut;
    }

}
