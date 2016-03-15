package uk.ac.cam.echo2016.multinarrative;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import android.os.BaseBundle;

public class NarrativeInstanceTest {
    NarrativeTemplate sampleTemplate;
    NarrativeTemplate loadTemplate;

    HashMap<String, Route> sampleRoutes = new HashMap<String, Route>();
    HashMap<String, Node> sampleNodes = new HashMap<String, Node>();

    static final int LOAD_SIZE = 100000;
    HashMap<String, Route> loadRoutes = new HashMap<String, Route>();
    HashMap<String, Node> loadNodes = new HashMap<String, Node>();

    /**
     * Generic Test - From the Visual Basic Sample Diagram
     */
    @Before
    public void generateSampleGraph() {

        sampleNodes.put("syncStart", new SynchronizationNode("syncStart")); // ___0
        sampleNodes.put("syncEnd", new SynchronizationNode("syncEnd")); // _______1
        sampleNodes.put("sync1", new SynchronizationNode("sync1")); // ___________2
        sampleNodes.put("sync2", new SynchronizationNode("sync2")); // ___________3
        sampleNodes.put("sync3", new SynchronizationNode("sync3")); // ___________4
        sampleNodes.put("sync4", new SynchronizationNode("sync4")); // ___________5
        sampleNodes.put("choiceMike1", new ChoiceNode("choiceMike1")); // ________6
        sampleNodes.put("choiceSam1", new ChoiceNode("choiceSam1")); // __________7
        sampleNodes.put("choiceSarah1", new ChoiceNode("choiceSarah1")); // ______8
        sampleNodes.put("choiceChris1", new ChoiceNode("choiceChris1")); // ______9
        sampleNodes.put("choiceJessica1", new ChoiceNode("choiceJessica1")); // _10

        Route tempRoute;

        tempRoute = new Route("routeMike1", sampleNodes.get("syncStart"), sampleNodes.get("choiceMike1"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Mike");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeMike2", sampleNodes.get("choiceMike1"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Mike");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeMike3", sampleNodes.get("choiceMike1"), sampleNodes.get("sync3"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Mike");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeMike4", sampleNodes.get("sync3"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Mike");
        sampleRoutes.put(tempRoute.getId(), tempRoute);

        tempRoute = new Route("routeSam1", sampleNodes.get("syncStart"), sampleNodes.get("choiceSam1"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sam");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSam2", sampleNodes.get("choiceSam1"), sampleNodes.get("sync3"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sam");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSam3", sampleNodes.get("sync3"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sam");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSam4", sampleNodes.get("choiceSam1"), sampleNodes.get("sync4"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sam");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSam5", sampleNodes.get("sync4"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sam");
        sampleRoutes.put(tempRoute.getId(), tempRoute);

        tempRoute = new Route("routeSarah1", sampleNodes.get("syncStart"), sampleNodes.get("choiceSarah1"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sarah");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSarah2", sampleNodes.get("choiceSarah1"), sampleNodes.get("sync3"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sarah");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSarah3", sampleNodes.get("sync3"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sarah");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSarah4", sampleNodes.get("choiceSarah1"), sampleNodes.get("sync1"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sarah");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeSarah5", sampleNodes.get("sync1"), sampleNodes.get("sync3"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Sarah");
        sampleRoutes.put(tempRoute.getId(), tempRoute);

        tempRoute = new Route("routeChris1", sampleNodes.get("syncStart"), sampleNodes.get("choiceChris1"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Chris");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeChris2", sampleNodes.get("choiceChris1"), sampleNodes.get("sync1"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Chris");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeChris3", sampleNodes.get("sync1"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Chris");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeChris4", sampleNodes.get("choiceChris1"), sampleNodes.get("sync2"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Chris");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeChris5", sampleNodes.get("sync2"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Chris");
        sampleRoutes.put(tempRoute.getId(), tempRoute);

        tempRoute = new Route("routeJessica1", sampleNodes.get("syncStart"), sampleNodes.get("sync2"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Jessica");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeJessica2", sampleNodes.get("sync2"), sampleNodes.get("choiceJessica1"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Jessica");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeJessica3", sampleNodes.get("choiceJessica1"), sampleNodes.get("sync4"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Jessica");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeJessica4", sampleNodes.get("sync4"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Jessica");
        sampleRoutes.put(tempRoute.getId(), tempRoute);
        tempRoute = new Route("routeJessica5", sampleNodes.get("choiceJessica1"), sampleNodes.get("syncEnd"));
        tempRoute.setup();
        tempRoute.createProperties();
        tempRoute.getProperties().putString("Character", "Jessica");
        sampleRoutes.put(tempRoute.getId(), tempRoute);

        // Creates template using the maps created above
        sampleTemplate = new NarrativeTemplate();
        sampleTemplate.routes.putAll(sampleRoutes);
        sampleTemplate.nodes.putAll(sampleNodes);
        sampleTemplate.start = (SynchronizationNode) sampleTemplate.getNode("syncStart");

        sampleTemplate.getGlobalProperties().getStringArrayList("System.Types").add("Character");
    }

    /**
     * Load Test - binary tree with node "1X" having children "10X" and "11X"
     */
    @Before
    public void generateLoadGraph() {
        for (int i = 1; i < LOAD_SIZE; ++i) {
            Node node = new ChoiceNode(Integer.toBinaryString(i));
            loadNodes.put(node.getId(), node);
        }
        for (Node node : loadNodes.values()) {
            String id = node.getId();
            int binary = Integer.parseInt(id, 2);
            int binCopy = binary;

            int bitCount = 0;
            while (binCopy > 0) {
                binCopy >>= 1;
                ++bitCount;
            }
            int bin1 = binary | 2 << bitCount - 1; // Set highest bit+1 to 1
            int bin2 = bin1 & ~(1 << bitCount - 1); // Set 2nd highest bit to 0
            if (bin1 == 0 || bin2 == 0) {
                System.out.println("?");
            }
            Node child1 = loadNodes.get(Integer.toBinaryString(bin1));
            Node child2 = loadNodes.get(Integer.toBinaryString(bin2));
            if (child1 != null && child2 != null) {
                Route route1 = new Route("route" + Integer.toBinaryString(bin1), node, child1);
                route1.setup();
                Route route2 = new Route("route" + Integer.toBinaryString(bin2), node, child2);
                route2.setup();
                loadRoutes.put(route1.getId(), route1);
                loadRoutes.put(route2.getId(), route2);
            }
        }
        // Add a start node (and route)
        SynchronizationNode start = new SynchronizationNode("start");
        Route startRoute = new Route("startRoute", start, loadNodes.get("1"));
        startRoute.setup();
        loadNodes.put("start", start);
        loadRoutes.put("startRoute", startRoute);

        // Creates template using the maps created above
        loadTemplate = new NarrativeTemplate();
        loadTemplate.routes.putAll(loadRoutes);
        loadTemplate.nodes.putAll(loadNodes);
    }

    @Test
    public void sampleGenerateTest() throws InvalidGraphException {

        // Tests the template constructor - these are for the full sample graph
        assertEquals(24, sampleTemplate.routes.size());
        assertEquals(11, sampleTemplate.nodes.size());
        assertEquals(sampleTemplate.getRoute("routeSarah5").getEnd().getId(), "sync3");
        assertEquals(sampleTemplate.getNode("choiceMike1").getEntering().get(0).getId(), "routeMike1");
        assertEquals(sampleTemplate.getNode("choiceMike1").getExiting().size(), 2);

        // Adds a test property for reference test later
        sampleTemplate.getNode("syncStart").createProperties();
        sampleTemplate.getNode("syncStart").getProperties().putIntArray("TestProperty", new int[] { 1, 2, 3 });

        // Copy the template into an instance
        NarrativeInstance sampleInst = sampleTemplate.generateInstance();

        // Tests whether the copy has the correct structure
        assertEquals("Testing correct number of routes: ", 24, sampleInst.routes.size());
        assertEquals("Testing correct number of nodes: ", 11, sampleInst.nodes.size());
        assertEquals("Testing \"routeSarah5\" connects to \"sync3\":",
                sampleInst.getRoute("routeSarah5").getEnd().getId(), "sync3");
        assertTrue("Testing \"choiceJessica1\" has route \"routeJessica3\" exiting:",
                sampleInst.getNode("choiceJessica1").getExiting().contains(sampleInst.getRoute("routeJessica3")));
        assertTrue("Testing \"sync4\" has route \"routeSam4\" entering:",
                sampleInst.getNode("sync4").getEntering().contains(sampleInst.getRoute("routeSam4")));
        assertArrayEquals("Testing Test Property was copied: ",
                sampleInst.getNode("syncStart").getProperties().getIntArray("TestProperty"), new int[] { 1, 2, 3 });

        // Tests whether the copy has different references
        assertNotSame("Testing \"routes\" reference is different: ", sampleInst.routes, sampleTemplate.routes);
        assertNotSame("Testing \"nodes\" reference is different: ", sampleInst.nodes, sampleTemplate.nodes);
        assertNotSame("Testing \"start\" reference is different: ", sampleInst.start, sampleTemplate.start);
        assertNotSame("Testing Test Property reference is different: ",
                sampleInst.getNode("syncStart").getProperties().get("TestProperty"),
                sampleTemplate.getNode("syncStart").getProperties().get("TestProperty"));
    }

    @Test
    public void SampleKillAndGetPlayableTest() throws InvalidGraphException, GraphElementNotFoundException {
        NarrativeInstance sampleInst = sampleTemplate.generateInstance();

        assertEquals("Testing playable routes: ", 5, sampleInst.getPlayableRoutes().size());
        assertEquals("", 1, sampleInst.activeNodes.size());

        sampleInst.kill("routeMike1");
        assertEquals("Testing kill method: ", 20, sampleInst.routes.size());
        assertEquals("Testing kill method: ", 10, sampleInst.nodes.size());

        assertEquals("Testing playable routes: ", 4, sampleInst.getPlayableRoutes().size());

        sampleInst.startRoute("routeSarah1");
        sampleInst.endRoute("routeSarah1");

        assertEquals("", 2, sampleInst.activeNodes.size());
        assertEquals("Testing playable routes: ", 5, sampleInst.getPlayableRoutes().size());

        sampleInst.kill("routeSarah4");
        assertEquals("Testing kill method: ", 18, sampleInst.routes.size());
        assertEquals("Testing kill method: ", 10, sampleInst.nodes.size());
    }

    /**
     * This test creates an NarrativeInstance and simulates a playthrough.
     * 
     * @throws InvalidGraphException
     * @throws GraphElementNotFoundException
     */
    @Test
    public void makeChoicesTest() throws InvalidGraphException, GraphElementNotFoundException {
        NarrativeInstance choiceInst = sampleTemplate.generateInstance();

        assertEquals("Check only one active node", 1, choiceInst.activeNodes.size());

        String firstChoice = choiceInst.getPlayableRoutes().get(0).getId();
        assertTrue("Check this route is one of the first routes",
                choiceInst.start.getExiting().contains(choiceInst.routes.get(firstChoice)));
        assertEquals("routeMike1", firstChoice);

        choiceInst.routes.get(firstChoice).createProperties();
        choiceInst.routes.get(firstChoice).getProperties().putBoolean("Correct", true);
        BaseBundle firstChoiceStarted = choiceInst.startRoute(firstChoice);
        assertTrue("Check properties returned correctly", firstChoiceStarted.getBoolean("Correct"));

        GameChoice firstChoiceEnded = choiceInst.endRoute(firstChoice);
        assertEquals("Check only two active nodes", 2, choiceInst.activeNodes.size());
        assertEquals("Check playable routes increased", 6, choiceInst.getPlayableRoutes().size());
        assertFalse("Check firstChoice no longer playable",
                choiceInst.getPlayableRoutes().contains(choiceInst.routes.get(firstChoice)));
        assertEquals("Check firstChoiceEnded has correct action", GameChoice.ACTION_MAJOR_DECISION,
                firstChoiceEnded.getAction());
        assertEquals("Check firstChoiceEnded has correct number of exiting routes", 2,
                firstChoiceEnded.getOptions().size());

        String secondChoice = firstChoiceEnded.getOptions().get(1).getId();
        choiceInst.startRoute(secondChoice);
        GameChoice secondChoiceEnded = choiceInst.endRoute(secondChoice);
        assertEquals("Check only one active node left", 1, choiceInst.activeNodes.size());
        assertEquals("Check playable routes decreased", 4, choiceInst.getPlayableRoutes().size());
        assertEquals("Check secondChoiceEnded has correct action", GameChoice.ACTION_CHOOSE_ROUTE,
                secondChoiceEnded.getAction());
        assertEquals("Check secondChoiceEnded has correct number of exiting routes", 4,
                secondChoiceEnded.getOptions().size());
        assertFalse("Check other route no longer playable",
                choiceInst.getPlayableRoutes().contains(firstChoiceEnded.getOptions().get(0)));
    }

    @Test
    public void loadTest() throws InvalidGraphException {

        NarrativeTemplate loadTemplate = new NarrativeTemplate();
        loadTemplate.routes.putAll(loadRoutes);
        loadTemplate.nodes.putAll(loadNodes);
        loadTemplate.start = (SynchronizationNode) loadTemplate.getNode("start");

        NarrativeInstance loadInst = loadTemplate.generateInstance();
        assertTrue("Testing load test constructor: ", loadInst.routes.containsKey("route10101"));
    }

    @Test(expected = InvalidGraphException.class)
    public void testErrorThrownIn1() throws InvalidGraphException {
        sampleTemplate.start = null;

        @SuppressWarnings("unused")
        NarrativeInstance sampleInst = sampleTemplate.generateInstance();
    }

    @Test(expected = InvalidGraphException.class)
    public void testErrorThrownIn2() throws InvalidGraphException {
        sampleTemplate.start = null;

        @SuppressWarnings("unused")
        NarrativeInstance sampleInst = sampleTemplate.generateInstance();
    }
}
