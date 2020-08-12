package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InventoryTest {
    Inventory inv;

    @BeforeEach
    public void setUp(){
        String[] s = {"a","b","c"};
        inv = new Inventory();
        inv.load(s);

    }
    public void testLoad(){
        assertEquals(false,inv.getItem("d"));
        assertEquals(true,inv.getItem("a"));
        assertEquals(false,inv.getItem("a"));
    }

    // test ziv
    @Test
    public void test(){
        setUp();
        testLoad();
//        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }
}
