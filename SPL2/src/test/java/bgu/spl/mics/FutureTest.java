package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    private Future f;
    @BeforeEach
    public void setUp(){
        f=new Future();
    }

    public void testIsDone(){
        boolean b = f.isDone();
        assertEquals(false,b);
        boolean result=true;
        f.resolve(result);
        assertEquals(true,b);
    }

    public void testResolve() throws InterruptedException {
        boolean result= false;
        f.resolve(result);
        Object x=f.get();
        assertEquals(true,x.getClass());
    }

    @Test
    public void test() throws InterruptedException {
        testIsDone();
        testResolve();
        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }
}
