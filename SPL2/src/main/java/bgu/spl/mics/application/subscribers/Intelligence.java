package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence  extends Subscriber {

	private Integer id;
	private Integer tickCounter;
	private Map<Integer, LinkedList<MissionInfo>> missionMap;
	private Long ticksLimit;
	private CountDownLatch latch;

	public Intelligence(List<MissionInfo> missionList, Integer id, Long ticksLimit,CountDownLatch latch) {
		super("Intelligence "+id.toString());
		this.id=id;
		this.latch=latch;
		missionMap=new HashMap<>();
		for (MissionInfo mission:missionList){
			if(!missionMap.containsKey(mission.getTimeIssued()))
			{
				LinkedList<MissionInfo> missionSameTimeList = new LinkedList<>();
				missionSameTimeList.add(mission);
				missionMap.put(mission.getTimeIssued(),missionSameTimeList);
			}
			else
			{
				missionMap.get(mission.getTimeIssued()).add(mission);
			}
		}
		this.tickCounter= 0;
		this.ticksLimit=ticksLimit;
	}

	@Override
	protected void initialize() throws InterruptedException {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);
		Callback<TickBroadcast> tickBroadcastCallback = (TickBroadcast tickBroadcast) -> {
			this.tickCounter=tickBroadcast.getTick();
			if(tickCounter==ticksLimit.intValue())
			{
				super.terminate();
			}
			if (missionMap.containsKey(tickCounter)) {
				for (MissionInfo mission : missionMap.get(tickCounter)) {
					Event<Boolean> missionEvent = new MissionReceivedEvent<>(mission, this.id);
					Future<Boolean> dontCareFuture = this.getSimplePublisher().sendEvent(missionEvent);
//					System.out.println("Intelligence " + id + " sent mission: " + mission.getMissionName() + " at tick: " + tickCounter);
				}
			}
		};
		this.subscribeBroadcast(TickBroadcast.class,tickBroadcastCallback);
		latch.countDown();
	}

}
