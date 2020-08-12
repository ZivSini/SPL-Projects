package bgu.spl.mics;

import java.util.HashMap;
import java.util.Map;

/**
 * The Subscriber is an abstract class that any subscriber in the system
 * must extend. The abstract Subscriber class is responsible to get and
 * manipulate the singleton {@link MessageBroker} instance.
 * <p>
 * Derived classes of Subscriber should never directly touch the MessageBroker.
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the Subscriber
 * message-queue (see {@link MessageBroker#register(Subscriber)}
 * method). The abstract Subscriber stores this callback together with the
 * type of the message is related to.
 *
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class Subscriber extends RunnableSubPub {

    //Fields:
    private Map <Class,Callback> eventMap;
    private Map <Class,Callback> broadcastMap;
    private boolean terminated = false;
    private final String MR = "bgu.spl.mics.MissionReceivedEvent";
    private final String AA = "bgu.spl.mics.AgentsAvailableEvent";
    private final String GA = "bgu.spl.mics.GadgetAvailableEvent";
    private final String SA = "bgu.spl.mics.SendAgentsEvent";
    private final String RA = "bgu.spl.mics.ReleaseAgentsEvent";
    private final String TB = "bgu.spl.mics.TickBroadcast";



    /**
     * @param name the Subscriber name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public Subscriber(String name) {
        super(name);
        eventMap=new HashMap<>();
        broadcastMap=new HashMap<>();
    }

    /**
     * Subscribes to events of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to events in the singleton MessageBroker using the supplied
     * {@code type}
     * 2. Store the {@code callback} so that when events of type {@code type}
     * are received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <E>      The type of event to subscribe to.
     * @param <T>      The type of result expected for the subscribed event.
     * @param type     The {@link Class} representing the type of event to
     *                 subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this Subscriber message
     *                 queue.
     */
    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) throws InterruptedException {
        eventMap.put(type,callback);
        MessageBrokerImpl.getInstance().subscribeEvent(type,this);
    }

    /**
     * Subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to broadcast messages in the singleton MessageBroker using the
     * supplied {@code type}
     * 2. Store the {@code callback} so that when broadcast messages of type
     * {@code type} received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The {@link Class} representing the type of broadcast
     *                 message to subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this Subscriber message
     *                 queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) throws InterruptedException {
        this.broadcastMap.put(type,callback);
        MessageBrokerImpl.getInstance().subscribeBroadcast(type,this);
    }

    /**
     * Completes the received request {@code e} with the result {@code result}
     * using the MessageBroker.
     * <p>
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     */
    protected final <T> void complete(Event<T> e, T result) throws InterruptedException {
        MessageBrokerImpl.getInstance().complete(e,result);
    }

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     */
    protected final void terminate() {
        this.terminated = true;
    }

    /**
     * The entry point of the Subscriber. TODO: you must complete this code
     * otherwise you will end up in an infinite loop.
     */
    @Override
    public final void run() {
        try {
            initialize();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!terminated) {
            try {
                Message currMS = MessageBrokerImpl.getInstance().awaitMessage(this);
                String type = currMS.getClass().getName();
                switch (type) {
                    case TB: {
                        Callback<TickBroadcast> tickBroadcastCallBack = broadcastMap.get(TickBroadcast.class);
                        tickBroadcastCallBack.call((TickBroadcast) currMS);
                        break;
                    }
                    case MR: {
                        Callback<MissionReceivedEvent> MRCallBack = eventMap.get(MissionReceivedEvent.class);
                        MRCallBack.call((MissionReceivedEvent) currMS);
                        break;
                    }
                    case AA:
                        Callback<AgentsAvailableEvent> AACallBack =  eventMap.get(AgentsAvailableEvent.class);
                        AACallBack.call((AgentsAvailableEvent) currMS);
                        break;
                    case GA:
                        Callback<GadgetAvailableEvent> GACallBack = eventMap.get(GadgetAvailableEvent.class);
                        GACallBack.call((GadgetAvailableEvent)currMS);
                        break;
                    case SA:
                        Callback<SendAgentsEvent> SACallBack = eventMap.get(SendAgentsEvent.class);
                        SACallBack.call((SendAgentsEvent)currMS);
                        break;
                    case RA:
                        Callback<ReleaseAgentsEvent> RACallBack = eventMap.get(ReleaseAgentsEvent.class);
                        RACallBack.call((ReleaseAgentsEvent)currMS);
                        break;
                }

            }catch (NullPointerException e){
            }catch (InterruptedException e){
                this.terminate();
            };
        }
        try {
            MessageBrokerImpl.getInstance().unregister(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("terminated "+this.getName());
    }

}
