package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SquadTest {
    private Squad sq;
    @BeforeEach
    public void setUp(){
        sq=new Squad();
        Agent a = new Agent();
        Agent b = new Agent();
        Agent c = new Agent();
        a.setName("a");
        b.setName("b");
        c.setName("c");
        a.setSerialNumber("007");
        b.setSerialNumber("009");
        c.setSerialNumber("0012");
        Agent[] s = {a,b,c};
        sq.load(s);
    }

    public void testgetAgentsNames(){
        List<String> s1= new ArrayList<String>();
        s1.add("a");
        s1.add("b");
        s1.add("c");
        List<String> s2= new ArrayList<String>();
        s2.add("007");
        s2.add("009");
        s2.add("0012");
        assertEquals(s1,sq.getAgentsNames(s2));
    }

    public void testreleaseAgents(){
        List<String> s=new ArrayList<String>();
        s.add("007");
        s.add("009");
        sq.releaseAgents(s);
        assertEquals(false,"007");
        assertEquals(true,"0012");
    }
    @Test
    public void test(){
        setUp();
        testgetAgentsNames();
        testreleaseAgents();
        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }
}
