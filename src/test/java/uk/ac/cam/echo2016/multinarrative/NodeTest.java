package uk.ac.cam.echo2016.multinarrative;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class NodeTest {
    SynchronizationNode syncTest;
    ChoiceNode choiceTest;

    @Before
    public void setup() {
        syncTest = new SynchronizationNode("syncTestID");
        choiceTest = new ChoiceNode("choiceTestID");
    }

    @Test
    public void testConstructor() {
        assertEquals("syncTestID", syncTest.getId());
        assertEquals("choiceTestID", choiceTest.getId());
        assertEquals(new ArrayList<Route>(), syncTest.getExiting());
        assertEquals(new ArrayList<Route>(), choiceTest.getExiting());
        assertEquals(new ArrayList<Route>(), syncTest.getEntering());
        assertEquals(new ArrayList<Route>(), choiceTest.getEntering());
        assertNull(syncTest.getProperties());
        assertNull(choiceTest.getProperties());
    }

    // @Test
    // public void testCallConstructor() {
    // Node newSync = syncTest.callConstructor("newSyncNode");
    // Node newChoice = choiceTest.callConstructor("newChoiceNode");
    //
    // assertEquals("Test type", SynchronizationNode.class, newSync.getClass());
    // assertEquals("Test type", ChoiceNode.class, newChoice.getClass());
    // assertEquals("Test new ID", "newSyncNode", newSync.getIdentifier());
    // assertEquals("Test new ID", "newChoiceNode", newChoice.getIdentifier());
    // }
}
