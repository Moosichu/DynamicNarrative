package uk.ac.cam.echo2016.multinarrative;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Before;

/**
 * Here there are commented out traverse statements. One can uncomment them to
 * see the structure of the graph.
 * 
 * @author rjm232
 * @author tj393
 *
 */
public class GUINarrativeTest { // TODO add actual GUINarrative tests
    public static void follow(Route route) {
        System.out.println("This route is " + route.getId());
        System.out.println("It connects " + route.getStart().getId() + " to " + route.getEnd().getId());
        System.out.println();
    }

    public static void traverse(Node node) { // TODO: move to multinarrative/dev
        System.out.println("This node is " + node.getId());
        System.out.println("Entering this node are the following routes:");
        for (Route n : node.getEntering()) {
            System.out.println("    " + n.getId());
        }
        System.out.println("Exiting this node are the following routes:");
        for (Route n : node.getExiting()) {
            System.out.println("    " + n.getId());
        }
        System.out.println();
        for (Route n : node.getExiting()) {
            follow(n);
        }
        for (Route n : node.getExiting()) {
            traverse(n.getEnd());
        }
    }

    GUINarrative gNarr;

    /**
     * Builds graph structure. Uncomment the traverse statement in
     * addRouteAndNodeTest to see the structure.
     * 
     * <pre>
     * ________________
     * _____start______
     * ______1|________
     * _______O-choice1
     * _____2/_\3______
     * _____/___\______
     * __left___right__
     * ___4\____5/_____
     * _____\___/______
     * ______end_______
     * ________________
     * </pre>
     * 
     * @see addRouteAndNodeTest
     */
    @Before
    public void setup() {
        gNarr = new GUINarrative();

        SynchronizationNode s1 = new SynchronizationNode("start");
        ChoiceNode c1 = new ChoiceNode("choice1");
        SynchronizationNode s2 = new SynchronizationNode("left");
        SynchronizationNode s3 = new SynchronizationNode("right");
        SynchronizationNode s4 = new SynchronizationNode("end");
        Route l1 = new Route("link1", s1, c1);
        l1.setup();
        Route l2 = new Route("link2", c1, s2);
        l2.setup();
        Route l3 = new Route("link3", c1, s3);
        l3.setup();
        Route l4 = new Route("link4", s2, s4);
        l4.setup();
        Route l5 = new Route("link5", s3, s4);
        l5.setup();
        gNarr.addNode(s1);
        gNarr.addNode(c1);
        gNarr.addNode(s2);
        gNarr.addNode(s3);
        gNarr.addNode(s4);
        gNarr.addRoute(l1);
        gNarr.addRoute(l2);
        gNarr.addRoute(l3);
        gNarr.addRoute(l4);
        gNarr.addRoute(l5);
        gNarr.start = s1;
    }

    @Test
    public void addRouteAndNodeTest() {
        // traverse(gNarr.start);
    }

    @Test
    public void renameRouteTest() {
        gNarr.renameRoute("link4", "fromLeftToEnd");
        assertNotNull("Check link4 renamed internally", gNarr.routes.get("fromLeftToEnd"));
        assertNull("Check link4 removed internally", gNarr.routes.get("link4"));
        assertTrue("Check link4 renamed in start Node",
                gNarr.nodes.get("left").getExiting().contains(gNarr.routes.get("fromLeftToEnd")));
        assertTrue("Check link4 renamed in end Node",
                gNarr.nodes.get("end").getEntering().contains(gNarr.routes.get("fromLeftToEnd")));
        // traverse(gNarr.start);
    }

    @Test
    public void renameNodeTest() {
        gNarr.renameNode("choice1", "FirstChoice");
        assertEquals("check choice1 renamed internally", "FirstChoice", gNarr.nodes.get("FirstChoice").getId());
        assertEquals("check choice1 renamed in entering narr", "FirstChoice",
                gNarr.routes.get("link1").getEnd().getId());
        assertEquals("check choice1 renamed in exiting left", "FirstChoice",
                gNarr.routes.get("link2").getStart().getId());
        assertEquals("check choice1 renamed in exiting right", "FirstChoice",
                gNarr.routes.get("link3").getStart().getId());
        assertEquals("check choice1 still has options", 2, gNarr.nodes.get("FirstChoice").getExiting().size());
        for (Route n : gNarr.nodes.get("FirstChoice").getExiting()) {
            assertEquals("check choice1 renamed in each exiting", "FirstChoice", n.getStart().getId());
        }
        for (Route n : gNarr.nodes.get("FirstChoice").getEntering()) {
            assertEquals("check choice1 renamed in each entering", "FirstChoice", n.getEnd().getId());
        }
        // traverse(gNarr.start);
    }

    @Test
    public void removeRouteTest() {
        gNarr.removeRoute("link1");
        assertNull("Check link1 removed from routes", gNarr.routes.get("link1"));
        assertEquals("Check link1 removed from start exiting", 0, gNarr.start.getExiting().size());
        assertEquals("Check link1 removed from choice1 entering", 0, gNarr.getNode("choice1").getEntering().size());
        // traverse(gNarr.start);

        gNarr.removeRoute("link3");
        assertEquals("Check link3 removed from choice1 exiting", 1, gNarr.nodes.get("choice1").getExiting().size());
        assertEquals("Check link3 removed from right entering", 0, gNarr.nodes.get("right").getEntering().size());
        // traverse(gNarr.nodes.get("choice1"));
    }

    @Test
    public void removeNodeTest() {
        gNarr.removeNode("left");
        assertNull("Check left removed from nodes", gNarr.nodes.get("left"));
        assertNull("Check entering route removed from routes", gNarr.routes.get("link2"));
        assertNull("Check exiting route removed from routes", gNarr.routes.get("link4"));
        // traverse(gNarr.start);

        setup();
        gNarr.removeNode("choice1");
        assertNull("Check choice1 removed from nodes", gNarr.nodes.get("choice1"));
        assertNull("Check entering route removed from routes", gNarr.routes.get("link1"));
        assertNull("Check left exiting route removed", gNarr.routes.get("link2"));
        assertNull("Check right exiting route removed", gNarr.routes.get("link3"));
        // traverse(gNarr.start);
        // traverse(gNarr.nodes.get("left"));
        // traverse(gNarr.nodes.get("right"));
    }

    /**
     * This test builds the graph, removes the route numbered 2 in the diagram
     * and then adds it again.
     * 
     * @throws NonUniqueIdException
     * @throws GraphElementNotFoundException
     */
    @Test
    public void newRouteTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.removeRoute("link4");
        gNarr.newRoute("newLink4", "left", "end");

        assertNotNull("Check route added to routes", gNarr.getRoute("newLink4"));
        assertNull("Check old route removed", gNarr.getRoute("link4"));
        assertTrue("Check node being exited has correct name reference",
                gNarr.getNode("left").getExiting().contains(gNarr.routes.get("newLink4")));
        assertTrue("Check node being entered has correct name reference",
                gNarr.getNode("end").getEntering().contains(gNarr.getRoute("newLink4")));
        assertEquals("Check route has been added only once", 1,
                gNarr.getRoute("newLink4").getStart().getExiting().size());
        // traverse(gNarr.start);
    }

    @Test(expected = GraphElementNotFoundException.class)
    public void newRouteWrongStartTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.newRoute("newLink", "badStart", "end");
    }

    @Test(expected = GraphElementNotFoundException.class)
    public void newRouteWrongEndTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.newRoute("newLink", "start", "badEnd");
    }

    @Test(expected = NonUniqueIdException.class) // TODO test
                                                 // GraphElementNotFoundExceptions
    public void newRouteExceptionTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.newRoute("link2", "start", "end");
    }

    @Test
    public void newSyncAndChoiceNodeTest() throws NonUniqueIdException {
        gNarr.newSynchronizationNode("syncTest");
        gNarr.newChoiceNode("choiceTest");

        assertNotNull("Check Sync node added to nodes", gNarr.getNode("syncTest"));
        assertEquals("Check Sync node is of correct type", SynchronizationNode.class,
                gNarr.getNode("syncTest").getClass());
        assertNotNull("Check Choice node added to nodes", gNarr.getNode("choiceTest"));
        assertEquals("Check Choice node is of correct type", ChoiceNode.class, gNarr.getNode("choiceTest").getClass());
    }

    @Test(expected = NonUniqueIdException.class)
    public void newSyncExceptionTest() throws NonUniqueIdException {
        gNarr.newSynchronizationNode("right");
    }

    @Test(expected = NonUniqueIdException.class)
    public void newChoiceExceptionTest() throws NonUniqueIdException {
        gNarr.newChoiceNode("left");
    }

    @Test
    public void insertChoiceOnRoute1Test() throws NonUniqueIdException, GraphElementNotFoundException {
        // Tests insertChoiceOnRoute(String routeId, String charId, String
        // newChoiceId, String newRouteId)
        gNarr.insertChoiceOnRoute("link2", "newChoice", "newLink");

        assertNotNull("Check new node in nodes", gNarr.getNode("newChoice"));
        assertNotNull("Check new route in routes", gNarr.getRoute("newLink"));
        assertEquals("Check choice1 has link2 exiting", "link2", gNarr.getNode("choice1").getExiting().get(0).getId());
        assertEquals("Check link2 has choice1 as start", "choice1", gNarr.getRoute("link2").getStart().getId());
        assertEquals("Check link2 has newChoice as end", "newChoice", gNarr.getRoute("link2").getEnd().getId());
        assertEquals("Check newChoice has link2 entering", "link2",
                gNarr.getNode("newChoice").getEntering().get(0).getId());
        assertEquals("Check newChoice has newLink exiting", "newLink",
                gNarr.getNode("newChoice").getExiting().get(0).getId());
        assertEquals("Check newLink has newChoice as start", "newChoice", gNarr.getRoute("newLink").getStart().getId());
        assertEquals("Check newLink has left as end", "left", gNarr.getRoute("newLink").getEnd().getId());
        assertTrue("Check end has newLink entering",
                gNarr.nodes.get("left").getEntering().contains(gNarr.routes.get("newLink")));

        // traverse(gNarr.start);
    }

    @Test(expected = NonUniqueIdException.class)
    public void insertChoiceOnRoute1StartExceptionTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.insertChoiceOnRoute("link2", "start", "newlink");
    }

    @Test(expected = NonUniqueIdException.class)
    public void insertChoiceOnRoute1RouteExceptionTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.insertChoiceOnRoute("link2", "newChoice", "link3");
    }

    @Test(expected = GraphElementNotFoundException.class)
    public void insertChoiceOnRoute1WrongLinkTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.insertChoiceOnRoute("wrongLink", "newChoice", "newLink");
    }

    @Test
    public void insertChoiceOnRoute2Test() throws NonUniqueIdException, GraphElementNotFoundException {
        // Tests insertChoiceOnRoute(String routeId, String newChoiceId, String
        // newRouteId1, String newRouteId2)
        gNarr.insertChoiceOnRoute("link5", "newChoice", "link5.1", "link5.2");

        assertNotNull("Check new node in nodes", gNarr.getNode("newChoice"));
        assertNotNull("Check first route in routes", gNarr.getRoute("link5.1"));
        assertNotNull("Check second route in routes", gNarr.getRoute("link5.2"));
        assertNull("Check link5 removed", gNarr.getRoute("link5"));
        assertEquals("Check right has link5.1 exiting", "link5.1", gNarr.getNode("right").getExiting().get(0).getId());
        assertEquals("Check link5.1 has right as start", "right", gNarr.getRoute("link5.1").getStart().getId());
        assertEquals("Check link5.1 has newChoice as end", "newChoice", gNarr.getRoute("link5.1").getEnd().getId());
        assertEquals("Check newChoice has link5.1 entering", "link5.1",
                gNarr.getNode("newChoice").getEntering().get(0).getId());
        assertEquals("Check newChoice has link5.2 exiting", "link5.2",
                gNarr.getNode("newChoice").getExiting().get(0).getId());
        assertEquals("Check link5.2 has newChoice as start", "newChoice", gNarr.getRoute("link5.2").getStart().getId());
        assertEquals("Check link5.2 has end as end", "end", gNarr.getRoute("link5.2").getEnd().getId());
        assertTrue("Check end has link5.2 entering",
                gNarr.getNode("end").getEntering().contains(gNarr.routes.get("link5.2")));
        // traverse(gNarr.start);
    }

    @Test(expected = NonUniqueIdException.class)
    public void insertChoiceOnRoute2StartExceptionTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.insertChoiceOnRoute("link2", "start", "newLink1", "newlink2");
    }

    @Test(expected = NonUniqueIdException.class)
    public void insertChoiceOnRoute2StartRouteExceptionTest()
            throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.insertChoiceOnRoute("link2", "newChoice", "link3", "newLink");
    }

    @Test(expected = NonUniqueIdException.class)
    public void insertChoiceOnRoute2EndRouteExceptionTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.insertChoiceOnRoute("link2", "newChoice", "newLink", "link3");
    }

    @Test(expected = GraphElementNotFoundException.class)
    public void insertChoiceOnRoute2WrongLinkTest() throws NonUniqueIdException, GraphElementNotFoundException {
        gNarr.insertChoiceOnRoute("wrongLink", "newChoice", "newLink1", "newLink2");
    }

    @Test
    public void generateTemplateTest() throws Exception {
        NarrativeTemplate template = gNarr.generateTemplate();
        assertEquals("Check template has correct number of nodes", 5, template.getNodes().size());
        assertEquals("Check template has correct number of edges", 5, template.getRoutes().size());
        assertEquals("Check template has correct start", "start",
                ((Node) MultiNarrative.class.getDeclaredField("start").get(template)).getId());
    }
}
