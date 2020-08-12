package bgu.spl.mics;

/**
 * The SimplePublisher is a class that any publisher in the system
 * stores. The SimplePublisher class is responsible to send
 * messages to the singleton {@link MessageBroker} instance.
 * <p>
 *
 * Only private fields and methods may be added to this class.
 * <p>
 */
public final class SimplePublisher {
    private final String MR = "bgu.spl.mics.MissionReceivedEvent";
    private final String AA = "bgu.spl.mics.AgentsAvailableEvent";
    private final String GA = "bgu.spl.mics.GadgetAvailableEvent";
    private final String SA = "bgu.spl.mics.SendAgentsEvent";
    private final String RA = "bgu.spl.mics.ReleaseAgentsEvent";
    /**
     * Sends the event {@code e} using the MessageBroker and receive a {@link Future<T>}
     * object that may be resolved to hold a result. This method must be Non-Blocking since
     * there may be events which do not require any response and resolving.
     * <p>
     * @param <T>       The type of the expected result of the request
     *                  {@code e}
     * @param e         The event to send
     * @return  		{@link Future<T>} object that may be resolved later by a different
     *         			subscriber processing this event.
     * 	       			null in case no Subscriber has subscribed to {@code e.getClass()}.
     */
    //TODO: idan
    public final <T> Future<T> sendEvent(Event<T> e) throws InterruptedException {
        Future<T>  future = MessageBrokerImpl.getInstance().sendEvent(e);

        String eventClass = e.getClass().getName();
        switch (eventClass){
            case MR:
                return ((MissionReceivedEvent) e).getFuture();
            case AA:
                return ((AgentsAvailableEvent) e).getFuture();
            case GA:
                return ((GadgetAvailableEvent) e).getFuture();
            case SA:
                return ((SendAgentsEvent) e).getFuture();
            case RA:
                return ((ReleaseAgentsEvent) e).getFuture();
        }
        return future;
    }

    /**
     * A Publisher calls this method in order to send the broadcast message {@code b} using the MessageBroker
     * to all the subscribers subscribed to it.
     * <p>
     * @param b The broadcast message to send
     */
    public final void sendBroadcast(Broadcast b) {
//        /**   test */
//        System.out.println("tick sent" );
        MessageBrokerImpl.getInstance().sendBroadcast(b);


    }
}
