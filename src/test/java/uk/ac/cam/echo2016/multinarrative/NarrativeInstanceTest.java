package uk.ac.cam.echo2016.multinarrative;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

public class NarrativeInstanceTest {
    HashMap<String, Route> sampleRoutes = new HashMap<String, Route>();
    HashMap<String, Node> sampleNodes = new HashMap<String, Node>();

    HashMap<String, Route> loadRoutes = new HashMap<String, Route>();
    HashMap<String, Node> loadNodes = new HashMap<String, Node>();

    @Before
    public void setup() {

        // Standard Test - From the Visual Basic Sample Diagram

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
        sampleNodes.get("syncStart").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeMike2", sampleNodes.get("choiceMike1"), sampleNodes.get("syncEnd"));
        sampleNodes.get("choiceMike1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeMike3", sampleNodes.get("choiceMike1"), sampleNodes.get("sync3"));
        sampleNodes.get("choiceMike1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeMike4", sampleNodes.get("sync3"), sampleNodes.get("syncEnd"));
        sampleNodes.get("sync3").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);

        tempRoute = new Route("routeSam1", sampleNodes.get("syncStart"), sampleNodes.get("choiceSam1"));
        sampleNodes.get("syncStart").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSam2", sampleNodes.get("choiceSam1"), sampleNodes.get("sync3"));
        sampleNodes.get("choiceSam1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSam3", sampleNodes.get("sync3"), sampleNodes.get("syncEnd"));
        sampleNodes.get("sync3").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSam4", sampleNodes.get("choiceSam1"), sampleNodes.get("sync4"));
        sampleNodes.get("choiceSam1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSam5", sampleNodes.get("sync4"), sampleNodes.get("syncEnd"));
        sampleNodes.get("sync4").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);

        tempRoute = new Route("routeSarah1", sampleNodes.get("syncStart"), sampleNodes.get("choiceSarah1"));
        sampleNodes.get("syncStart").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSarah2", sampleNodes.get("choiceSarah1"), sampleNodes.get("sync3"));
        sampleNodes.get("choiceSarah1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSarah3", sampleNodes.get("sync3"), sampleNodes.get("syncEnd"));
        sampleNodes.get("sync3").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSarah4", sampleNodes.get("choiceSarah1"), sampleNodes.get("sync1"));
        sampleNodes.get("choiceSarah1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeSarah5", sampleNodes.get("sync1"), sampleNodes.get("sync3"));
        sampleNodes.get("sync1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);

        tempRoute = new Route("routeChris1", sampleNodes.get("syncStart"), sampleNodes.get("choiceChris1"));
        sampleNodes.get("syncStart").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeChris2", sampleNodes.get("choiceChris1"), sampleNodes.get("sync1"));
        sampleNodes.get("choiceChris1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeChris3", sampleNodes.get("sync1"), sampleNodes.get("syncEnd"));
        sampleNodes.get("sync1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeChris4", sampleNodes.get("choiceChris1"), sampleNodes.get("sync2"));
        sampleNodes.get("choiceChris1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeChris5", sampleNodes.get("sync2"), sampleNodes.get("syncEnd"));
        sampleNodes.get("sync2").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);

        tempRoute = new Route("routeJessica1", sampleNodes.get("syncStart"), sampleNodes.get("sync2"));
        sampleNodes.get("syncStart").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeJessica2", sampleNodes.get("sync2"), sampleNodes.get("choiceJessica1"));
        sampleNodes.get("sync2").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeJessica3", sampleNodes.get("choiceJessica1"), sampleNodes.get("sync4"));
        sampleNodes.get("choiceJessica1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeJessica4", sampleNodes.get("sync4"), sampleNodes.get("syncEnd"));
        sampleNodes.get("sync4").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);
        tempRoute = new Route("routeJessica5", sampleNodes.get("choiceJessica1"), sampleNodes.get("syncEnd"));
        sampleNodes.get("choiceJessica1").getOptions().add(tempRoute);
        sampleRoutes.put(tempRoute.getIdentifier(), tempRoute);

        // Load Test - binary tree with node "1abcd" having children "10abcd" and "11abcd"
        
        for (int i = 1; i < 1000000; ++i) {
            Node node = new ChoiceNode(Integer.toBinaryString(i));
            loadNodes.put(node.getIdentifier(), node);
        }
        for (Node node : loadNodes.values()) {
            String id = node.getIdentifier();
            int binary = Integer.parseInt(id,2);
            int binCopy = binary;

            int bitCount = 0;
            while (binCopy > 0) {
                binCopy >>= 1;
                ++bitCount;
            }
            int bin1 = binary | 2 << bitCount - 1; // Set highest bit+1 to 1
            int bin2 = bin1 & ~(1 << bitCount - 1); // Set 2nd highest bit to 0
            if (bin1 == 0 || bin2 == 0 ) {
                System.out.println("?");
            }
            Node child1 = loadNodes.get(Integer.toBinaryString(bin1));
            Node child2 = loadNodes.get(Integer.toBinaryString(bin2));
            if (child1 != null && child2 != null) {
                Route route1 = new Route("route" + Integer.toBinaryString(bin1), node, child1);
                Route route2 = new Route("route" + Integer.toBinaryString(bin2), node, child2);
                loadRoutes.put(route1.getIdentifier(), route1);
                loadRoutes.put(route2.getIdentifier(), route2);
                node.getOptions().add(route1);
                node.getOptions().add(route2);
            }
        }

    }

    @Test
    public void testNodeStructure() throws NullPointerException { // TODO Documentation!
        
        // Sample Tests
        
        NarrativeTemplate sampleTemplate = new NarrativeTemplate();
        sampleTemplate.routes.putAll(sampleRoutes);
        sampleTemplate.nodes.putAll(sampleNodes);
        sampleTemplate.start = sampleTemplate.getNode("syncStart");

        sampleTemplate.getNode("choiceMike1").createProperties(); // TODO replace with deep clone method test - use .equals()?
        sampleTemplate.getNode("choiceMike1").getProperties().putBoolean("ChoicePropertyCopiedCorrectly", true);
        sampleTemplate.getNode("sync1").createProperties();
        sampleTemplate.getNode("sync1").getProperties().putBoolean("SyncPropertyCopiedCorrectly", true);

        assertEquals(24, sampleTemplate.routes.size());
        assertEquals(11, sampleTemplate.nodes.size());
        assertEquals(sampleTemplate.getRoute("routeSarah5").getEnd().getIdentifier(), "sync3");
       
        NarrativeInstance sampleInst = sampleTemplate.generateInstance2();
        
        assertTrue("Check Choice properties copied correctly", sampleInst.getNodeProperties("choiceMike1").containsKey("ChoicePropertyCopiedCorrectly"));
        assertTrue("Check Sync properties copied correctly", sampleInst.getNodeProperties("sync1").containsKey("SyncPropertyCopiedCorrectly"));

        assertEquals("Checking correct number of routes: ", 24, sampleInst.routes.size());
        assertEquals("Checking correct number of nodes: ", 11, sampleInst.nodes.size());
        assertEquals("Checking \"routeSarah5\" connects to \"sync3\":",
                sampleInst.getRoute("routeSarah5").getEnd().getIdentifier(), "sync3");
        assertTrue("Checking \"choiceJessica1\" has route \"routeJessica3\":",
                sampleInst.getNode("choiceJessica1").getOptions().contains(sampleInst.getRoute("routeJessica3")));

        Route route = sampleInst.getRoute("routeBob1");
        assertNull(route);

        sampleInst.kill("routeMike1");
        assertEquals(21, sampleInst.routes.size());
        
        // Load Test
        
        NarrativeTemplate loadTemplate = new NarrativeTemplate();
        loadTemplate.routes.putAll(loadRoutes);
        loadTemplate.nodes.putAll(loadNodes);
        loadTemplate.start = loadTemplate.getNode("1");
        
        NarrativeInstance loadInst = loadTemplate.generateInstance2();
        System.out.println(loadInst.getRoute("route10101").getIdentifier());
    }

    /**
     * Here template.start is not set, so an error is thrown.
     * 
     * @throws NullPointerException
     */

    /*
     * @Test(expected = NullPointerException.class) public void testErrorThrown() throws NullPointerException {
     * NarrativeTemplate template = new NarrativeTemplate(); template.route.putAll(routeMap);
     * template.nodes.putAll(nodeMap);
     * 
     * @SuppressWarnings("unused") NarrativeInstance instance = new NarrativeInstance(template); }
     */
    
   /*
    @Test(expected = NullPointerException.class)
    public void testErrorThrown() throws NullPointerException {
        NarrativeTemplate template = new NarrativeTemplate();
        template.routes.putAll(routeMap);
        template.nodes.putAll(nodeMap);
        
        @SuppressWarnings("unused")
        NarrativeInstance instance = template.generateInstance();
    }
*/
    
}
