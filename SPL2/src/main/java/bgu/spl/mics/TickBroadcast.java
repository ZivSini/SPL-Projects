package bgu.spl.mics;

public class TickBroadcast implements Broadcast {

    private int tick;
    private long limit;

    public TickBroadcast(int tick, long limit){
        this.tick=tick;
        this.limit=limit;
    }

    public int getTick() {
        return tick;
    }
    public long getLimit() {
        return limit;
    }
}