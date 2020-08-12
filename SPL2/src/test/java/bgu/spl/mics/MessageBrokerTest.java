package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageBrokerTest {

    Subscriber simlpeSub;// = new SimpleSubscriber();
    String resault = "Not Resolved";
    Event<String> event = new MissionReceivedEvent(resault);
    MessageBrokerImpl ms ;
    Broadcast b;
    Future<String > f = ms.sendEvent(event);
    Subscriber anotherSimpleSub;

    @BeforeEach
    public void setUp(){
        simlpeSub = new SimpleSubscriber("SimpleSub");
        anotherSimpleSub = new SimpleSubscriber("AnotherSimpleSub");
        ms = new MessageBrokerImpl();
        b = new SimpleBroadcast();
        ms.register(simlpeSub);
    }


    public void testSubscribEvent() {
        ms.subscribeEvent(event.class, simlpeSub);
        ms.sendEvent(event);
        try {
            assertEquals(event, ms.awaitMessage(simlpeSub));
        }
        catch (Exception e){};
    }

    public void testSubscribeBroadcast() {
        ms.subscribeBroadcast(b.getClass(), simlpeSub);
        ms.sendBroadcast(b);
        try {
            assertEquals(b, ms.awaitMessage(simlpeSub));
        }
        catch (Exception e){};
    }

    public void testComplete(){
        ms.complete(event,"resolved");
        assertEquals("resolved",f.get());
    }

    public void testSendBroadcast(){
        ms.sendBroadcast(b);
        try{
        assertEquals(b,ms.awaitMessage(simlpeSub));
    }
        catch (Exception e){};
    }

    public void testSendEvent(){
        ms.sendEvent(event);
        try{
        assertEquals(event,ms.awaitMessage(simlpeSub));
    }
        catch (Exception e){};
    }

    public void testRegister(){
        ms.register(anotherSimpleSub);
        ms.subscribeEvent(event.class,anotherSimpleSub);
        ms.sendEvent(event);
        try{
        assertEquals(event,ms.awaitMessage(anotherSimpleSub));
    }
        catch (Exception e){};
    }

    public void testUnregister() {
        ms.unregister(anotherSimpleSub);
        try {
            assertEquals(null, ms.awaitMessage(anotherSimpleSub));
        }
        catch (Exception e){};
    }

    public void testAwaitMessage(){
        try {
            assertEquals(b,ms.awaitMessage(simlpeSub));
        }
        catch (Exception e){};
    }

    @Test
    public void test(){
        setUp();
        testAwaitMessage();
        testComplete();
        testRegister();
        testSendBroadcast();
        testSendEvent();
        testSubscribeBroadcast();
        testSubscribEvent();
        testUnregister();
        fail("Not a good test");
    }
}
