package bgu.spl.mics;

import java.util.List;

public class ReleaseAgentsEvent<T> implements Event<T> {
    private Future<T> future=null;
    private List<String> serials;

    public ReleaseAgentsEvent(List<String> serials){
        this.serials=serials;

    }

    public List<String> getSerials(){
        return serials;
    }
    public Future<T> getFuture(){
        return future;
    }
    public void setFuture(Future<T> fut){
        this.future=fut;
    }


}
