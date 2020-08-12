package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;
import org.javatuples.Pair;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private int tickCounter;
	private int ticksLimit;
	private Integer id;
	private CountDownLatch latch;

	public M(int ticksLimit, Integer id, CountDownLatch latch) {
		super("M "+id.toString());
		this.ticksLimit =ticksLimit;
		this.id=id;
		this.latch=latch;

	}

	@Override
	protected void initialize() throws InterruptedException {
		Thread.currentThread().setName(getName());
		tickCounter = 0;
		MessageBrokerImpl.getInstance().register(this);
		Callback<TickBroadcast> tickBroadcastCallback = (TickBroadcast tickBroadcast) ->{
			this.tickCounter=tickBroadcast.getTick();
//			System.out.println("M "+id+" GOT TICK: "+tickBroadcast.getTick());
			if (tickCounter== ticksLimit)
			{
				super.terminate();
			}
		};
		this.subscribeBroadcast(TickBroadcast.class, tickBroadcastCallback);
		latch.countDown();
		Callback<MissionReceivedEvent> MREcallBack = missionEvent -> {

			Diary.getInstance().incrementTotal();
//			System.out.println("M"+id+"--GOT:"+missionEvent.getMission().getMissionName()+" INTEL"+missionEvent.getIntelId()+"--AT:"+tickCounter);
			Future<Integer> gadgFuture = null; // so it will be out of "if"'s scope and can be used in the Report constructor.
			List<String> serialAgentsList = missionEvent.getMission().getSerialAgentsNumbers();
			Event<Pair<List<String>, Integer>> agentsEvent = new AgentsAvailableEvent<>(serialAgentsList,id);
			Future<Pair<List<String>, Integer>> agentsFuture = this.getSimplePublisher().sendEvent(agentsEvent);
//			System.out.println("M"+id+"--AGENT AVAIL>>MP: "+missionEvent.getMission().getMissionName()+"--AT:"+tickCounter);
			Pair<List<String>, Integer> agentsFutureResult = agentsFuture.get();
			if (agentsFutureResult == null) { // not all agents exist in the squad
//				System.out.println("M-"+id+" RECEIVED ANS: NULL AFTER AAE");
				//Diary.getInstance().incrementTotal();
			} else { // all agents exist in the squad
//				System.out.println("M-"+id+" RECEIVED ANS: ALL AGENTS AVAIL");
				String gadget = missionEvent.getMission().getGadget();
				Event<Integer> gadgetEvent = new GadgetAvailableEvent(gadget);
//				System.out.println("M"+id+"--GADGET AVAIL>>Q: "+missionEvent.getMission().getMissionName()+"--AT:"+tickCounter);
				gadgFuture = this.getSimplePublisher().sendEvent(gadgetEvent);
//				Integer gadgFutureResault = gadgFuture.get();
				if (gadgFuture.get() == null) { // gadget doesn't exist in the inventory
//					System.out.println("M"+id+" --RELEASE AGENTS>>MP: "+missionEvent.getMission().getSerialAgentsNumbers()+"--AT:"+tickCounter);
					Event<Boolean> releaseAgents = new ReleaseAgentsEvent<>(serialAgentsList);
					Future<Boolean> releaseAgentsFut = this.getSimplePublisher().sendEvent(releaseAgents);
					//Diary.getInstance().incrementTotal();
				} else { // gadget exists in the inventory
					if (missionEvent.getMission().getTimeExpired() < tickCounter) { // mission's time expired
//						System.out.println("M"+id+" --RELEASE AGENTS>>MP: "+missionEvent.getMission().getSerialAgentsNumbers()+"--AT: "+tickCounter);
						Event<Boolean> releaseAgents = new ReleaseAgentsEvent<>(serialAgentsList);
						Future<Boolean> releaseAgentsFut = this.getSimplePublisher().sendEvent(releaseAgents);
						//Diary.getInstance().incrementTotal();
					} else { // all conditions to execute the mission are met and the agents are sent
//						System.out.println("M"+id+" --SEND AGENTS>>MP: "+missionEvent.getMission().getSerialAgentsNumbers()+"--AT: "+tickCounter);
						Event<Boolean> sendAgents = new SendAgentsEvent<>(serialAgentsList, missionEvent.getMission().getDuration());
						Future<Boolean> sendAgentsFut = this.getSimplePublisher().sendEvent(sendAgents);
						Report r = new Report(missionEvent.getMission(), agentsFutureResult.getValue0(), this.id, agentsFutureResult.getValue1(), gadgFuture.get(), this.tickCounter);
						Diary.getInstance().addReport(r);
					}
				}
			}
			this.complete(missionEvent,true);

		};
		this.subscribeEvent(MissionReceivedEvent.class, MREcallBack);
	}

	public Integer getId(){ return id; }
	public int getTickCounter(){return tickCounter;}


}
