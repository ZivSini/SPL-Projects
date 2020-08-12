package bgu.spl.mics;

import bgu.spl.mics.application.subscribers.Moneypenny;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {
	private Map<Subscriber, LinkedBlockingQueue<Message>> subsMap;
	private Map<String, LinkedBlockingQueue<Subscriber>> eventsMap;
	private Map<String, LinkedList<Subscriber>> broadcastMap;
	private final String MR = "bgu.spl.mics.MissionReceivedEvent";
	private final String AA = "bgu.spl.mics.AgentsAvailableEvent";
	private final String GA = "bgu.spl.mics.GadgetAvailableEvent";
	private final String SA = "bgu.spl.mics.SendAgentsEvent";
	private final String RA = "bgu.spl.mics.ReleaseAgentsEvent";
	private final String TB = "bgu.spl.mics.TickBroadcast";



	private static class MessageBrokerImplHolder{
		private static MessageBrokerImpl instance = new MessageBrokerImpl();
	}

	private MessageBrokerImpl(){
		// innitiate fields
		subsMap = new ConcurrentHashMap<>();
		eventsMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		//TODO: Implement this
		return MessageBrokerImplHolder.instance;
	}

	@Override
	public synchronized  <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) throws InterruptedException {
		if(!eventsMap.containsKey(type.getName()))
		{
			eventsMap.put(type.getName(),new LinkedBlockingQueue<Subscriber>());
		}
		eventsMap.get(type.getName()).add(m);
		//System.out.println(m.getName()+" subscribed to "+type.toString().substring(19)+" events");
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		if(!broadcastMap.containsKey(type.getName()))
		{
			broadcastMap.put(type.getName(),new LinkedList<Subscriber>());
		}
		broadcastMap.get(type.getName()).add(m);
		//System.out.println(m.getName()+" subscribed to "+type.toString().substring(19)+" broadcast");
	}

	@Override
	public  <T> void complete(Event<T> e, T result) throws InterruptedException {
		String type = e.getClass().getName();
		switch (type){
			case MR:
				((MissionReceivedEvent)e).getFuture().resolve(result);
				break;
			case AA:
				((AgentsAvailableEvent)e).getFuture().resolve(result);
				break;
			case GA:
				((GadgetAvailableEvent)e).getFuture().resolve(result);
				break;
			case SA:
				((SendAgentsEvent)e).getFuture().resolve(result);
				break;
			case RA:
				((ReleaseAgentsEvent)e).getFuture().resolve(result);
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + type);
		}

	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if(!broadcastMap.isEmpty()) {
			if(!broadcastMap.get(b.getClass().getName()).isEmpty()) {
				List<Subscriber> list = broadcastMap.get(b.getClass().getName());
				long tick = ((TickBroadcast)b).getTick();
				if ((tick==(((TickBroadcast)b).getLimit()))){
					Object[] subArr = list.toArray();
					for (int i = 0;i<subArr.length;i++){
						subsMap.get(subArr[i]).add(b);
					}
				}
				else {
					for (Subscriber s : list) {
						subsMap.get(s).add(b);
					}
				}
			}
		}
	}

	@Override
	public synchronized   <T> Future<T> sendEvent(Event<T> e) throws InterruptedException {
		Subscriber currSub;
		Future<T> currFuture = new Future<>();
		String eventClass = e.getClass().getName();

		switch (eventClass){
			case MR:
				if (eventsMap.get(eventClass).isEmpty()){
					currFuture.resolve(null);
				}
				else {
					currSub = eventsMap.get(eventClass).remove();
					subsMap.get(currSub).add(e);
					eventsMap.get(eventClass).add(currSub);
				}
				((MissionReceivedEvent) e).setFuture(currFuture);
				return currFuture;
			case AA:
				if (eventsMap.get(eventClass).isEmpty()){
					currFuture.resolve(null);
				}
				else {
					currSub = eventsMap.get(eventClass).remove();
					subsMap.get(currSub).add(e);
					eventsMap.get(eventClass).add(currSub);
				}
				((AgentsAvailableEvent)e).setFuture(currFuture);
				return currFuture;
			case GA:
				if (eventsMap.get(eventClass).isEmpty()){
					currFuture.resolve(null);
					((GadgetAvailableEvent)e).setFuture(currFuture);
				}
				else {
					((GadgetAvailableEvent)e).setFuture(currFuture);
					currSub = eventsMap.get(eventClass).remove();
					subsMap.get(currSub).add(e);
					eventsMap.get(eventClass).add(currSub);
				}
				return currFuture;
			case SA:
				if (eventsMap.get(eventClass).isEmpty()){
					currFuture.resolve(null);
				}
				else {
					currSub = eventsMap.get(eventClass).remove();
					subsMap.get(currSub).add(e);
					eventsMap.get(eventClass).add(currSub);
				}
				((SendAgentsEvent)e).setFuture(currFuture);
				return currFuture;
			case RA:
				if (eventsMap.get(eventClass).isEmpty()){
					currFuture.resolve(null);
				}
				else {
					currSub = eventsMap.get(eventClass).remove();
					subsMap.get(currSub).add(e);
					eventsMap.get(eventClass).add(currSub);
				}
				((ReleaseAgentsEvent)e).setFuture(currFuture);
				return currFuture;
		}
		return currFuture;
	}

	@Override
	public void register(Subscriber m) {
		subsMap.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public synchronized void unregister(Subscriber m)  {
		System.out.println(m.getName()+ " UNREGISTERED");
		try {
			for (Message message : subsMap.get(m)){
				if (!message.getClass().getName().equals(TB)){
				this.complete((Event) message, null);
			}
			}
		} catch (Exception e){
			System.out.println("MB: "+e);}
		subsMap.remove(m);
		Object[] eventsArr = eventsMap.keySet().toArray();
		for (int i=0;i<eventsArr.length;i++){
			eventsMap.get(eventsArr[i]).remove(m);
		}
//		for(String s: eventsMap.keySet()){
//			eventsMap.get(s).remove(m);
//		}
		Object[] broadcastsArr = broadcastMap.keySet().toArray();
		for (int i=0;i<broadcastsArr.length;i++){
			broadcastMap.get(broadcastsArr[i]).remove(m);
		}

//		for(String s: broadcastMap.keySet()){
//			broadcastMap.get(s).remove(m);
//		}
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		return this.subsMap.get(m).take();
	}


}
