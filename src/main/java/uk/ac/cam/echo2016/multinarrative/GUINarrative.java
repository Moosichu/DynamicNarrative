package uk.ac.cam.echo2016.multinarrative;

import android.os.BaseBundle;
import uk.ac.cam.echo2016.multinarrative.io.SaveWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code EditableNarrative} used by the {@code FXMLGUI} editor to store the
 * graph structure. This graph is generated alongside the editor via
 * {@code GUIOperations} and is used to build the template when the design is
 * exported.
 * 
 * 
 * @author tr393
 * @author eyx20
 * @version 1.0
 * @throws NonUniqueIdException
 * @see MultiNarrative
 */
public class GUINarrative extends EditableNarrative { // TODO Finish
                                                      // Documentation
    private static final long serialVersionUID = 1;
    private Map<String, BaseBundle> propertyMapping = new HashMap<String, BaseBundle>();
    protected HashMap<String, String> typemap = new HashMap<String, String>();

    public boolean isUniqueId(String id) {
        return (!routes.containsKey(id) && !nodes.containsKey(id));
    }

    public boolean isChoiceNode(String nodeId) throws GraphElementNotFoundException {
        Node node = nodes.get(nodeId);
        return node instanceof ChoiceNode;
    }

    public void setPropertyType(String property, String type) {
        typemap.put(property, type);
    }

    /**
     * Adds a route with ID {@code id} to the graph, connecting the node with ID
     * {@code startId} to the route with ID {@code endId}.
     * 
     * @param id
     * @param startId
     * @param endId
     * @throws CommandException
     */
    public void newRoute(String id, String startId, String endId)
            throws NonUniqueIdException, GraphElementNotFoundException {
        if (isUniqueId(id)) {
            Node startNode = getNode(startId);
            if (startNode == null)
                throw new GraphElementNotFoundException(startId);
            Node endNode = getNode(endId);
            if (endNode == null)
                throw new GraphElementNotFoundException(endId);

            Route route = new Route(id, startNode, endNode);
            // Updates references of graph and nodes
            route.setup();
            addRoute(route);
        } else {
            throw new NonUniqueIdException("Invalid id: " + id + " is not unique.");
        }
    }

    public void newSynchronizationNode(String id) throws NonUniqueIdException {
        if (isUniqueId(id))
            nodes.put(id, new SynchronizationNode(id));
        else
            throw new NonUniqueIdException("Invalid id: " + id + " is not unique.");
    }

    public void newChoiceNode(String id) throws NonUniqueIdException {
        if (isUniqueId(id))
            nodes.put(id, new ChoiceNode(id));
        else
            throw new NonUniqueIdException("Invalid id: " + id + " is not unique.");
    }

    public void addRouteType(String type) { // TODO add to tests

        if (!getGlobalProperties().getStringArrayList("System.Types").contains(type)) {
            getGlobalProperties().getStringArrayList("System.Types").add(type);
        }

    }

    public boolean removeRouteType(String type) { // TODO add to tests
        return this.getGlobalProperties().getStringArrayList("System.Types").remove(type);
    }

    public BaseBundle getProperties(String id) throws GraphElementNotFoundException {
        Route route = getRoute(id);
        if (route != null) {
            return route.getProperties();
        } else {
            Node node = getNode(id);
            if (node != null) {
                return node.getProperties();
            }
        }
        throw new GraphElementNotFoundException(id);
    }

    /**
     * Takes the route with ID {@code routeId} and splits it in two, where the
     * divisor is a new {@code ChoiceNode} with ID {@code newChoiceId}. Here,
     * the original route is preserved between its start and the new node.
     * 
     * <pre>
     * Before:
     *           start
     *             |
     *             | routeId
     *             |
     *            end  
     *
     * After:
     *           start
     *             |
     *             | newRouteId1 (=routeId)
     *             |
     *        newChoiceId
     *             |
     *             | newRouteId2
     *             |
     *            end
     * </pre>
     * 
     * @see insertChoiceOnRoute
     * @param routeId
     * @param newChoiceId
     * @param newRouteId
     * @throws NonUniqueIdException
     * @throws GraphElementNotFoundException
     */
    public void insertChoiceOnRoute(String routeId, String newChoiceId, String newRouteId)
            throws NonUniqueIdException, GraphElementNotFoundException {

        if (!isUniqueId(newChoiceId) || !isUniqueId(newRouteId)) {
            throw new NonUniqueIdException(
                    "Error: Invalid id: " + (isUniqueId(newChoiceId) ? newChoiceId : newRouteId) + " is not unique.");
        }
        if (newChoiceId.equals(newRouteId)) {
            throw new NonUniqueIdException("Error: Arguments not unique");
        }
        Route route1 = getRoute(routeId);
        if (route1 == null)
            throw new GraphElementNotFoundException(routeId);

        ChoiceNode choice = new ChoiceNode(newChoiceId);
        // Connect route2 start and end
        Route route2 = new Route(newRouteId, choice, route1.getEnd());
        route2.setup();
        route2.getEnd().getEntering().remove(route1);

        // route1.start and start.exitRoutes already correct
        route1.setEnd(choice);
        choice.getEntering().add(route1);

        routes.put(route2.getId(), route2);
        nodes.put(choice.getId(), choice);
    }

    /**
     * Takes the route with ID {@code routeId} and splits it in two, where the
     * divisor is a new {@code ChoiceNode} with ID {@code newChoiceId}. Here,
     * the original route is discarded.
     * 
     * <pre>
     *
     * Before:
     *           start
     *             |
     *             | routeId
     *             |
     *            end  
     *         
     * After:
     *           start
     *             |
     *             | newRouteId1
     *             |    
     *        newChoiceId  
     *             |
     *             | newRouteId2
     *             |
     *            end
     * </pre>
     *
     * 
     * @see insertChoiceOnRoute
     * @param routeId
     * @param newChoiceId
     * @param newRouteId1
     * @param newRouteId2
     * @throws NonUniqueIdException
     * @throws GraphElementNotFoundException
     */
    public void insertChoiceOnRoute(String routeId, String newChoiceId, String newRouteId1, String newRouteId2)
            throws NonUniqueIdException, GraphElementNotFoundException {

        if (!isUniqueId(newChoiceId) || !isUniqueId(newRouteId1) || !isUniqueId(newRouteId2)) {
            throw new NonUniqueIdException("Error: Invalid id: "
                    + (isUniqueId(newChoiceId) ? (isUniqueId(newRouteId1) ? newRouteId2 : newRouteId1) : newChoiceId)
                    + " is not unique.");
        }
        if (newChoiceId.equals(newRouteId1) || newChoiceId.equals(newRouteId2) || newRouteId1.equals(newRouteId2)) {
            throw new NonUniqueIdException("Error: Arguments are not unique");
        }

        Route route = getRoute(routeId);
        if (route == null)
            throw new GraphElementNotFoundException(routeId);

        ChoiceNode choice = new ChoiceNode(newChoiceId);

        Node start = route.getStart();
        Node end = route.getEnd();

        // Connect route1
        Route route1 = new Route(newRouteId1, start, choice);
        route1.setup();
        start.getExiting().remove(route);

        // Connect route2
        Route route2 = new Route(newRouteId2, choice, end);
        route2.setup();
        end.getEntering().remove(route);

        // Update GuiNarrative references
        routes.remove(route.getId());
        routes.put(route1.getId(), route1);
        routes.put(route2.getId(), route2);
        nodes.put(choice.getId(), choice);
    }

    public boolean setStartPoint(String id) throws GraphElementNotFoundException {
        Node node = getNode(id);
        if (node == null)
            throw new GraphElementNotFoundException(id);
        if (node instanceof SynchronizationNode) {
            start = (SynchronizationNode) node;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Swaps a Node's type from {@code SynchronizationNode} to
     * {@code ChoiceNode} and vica versa. Note that this may create a choice
     * node with multiple entering routes.
     * 
     * @param nodeId
     * @return
     * @throws GraphElementNotFoundException
     */
    public void swapSyncAndChoice(String nodeId) throws GraphElementNotFoundException {
        Node node = nodes.get(nodeId);
        Node newNode;
        if (isChoiceNode(nodeId)) {
            newNode = new SynchronizationNode(nodeId);
        } else {
            newNode = new ChoiceNode(nodeId);
        }
        nodes.remove(nodeId);
        newNode.setEntering(node.getEntering());
        newNode.setExiting(node.getExiting());
        newNode.setProperties(node.getProperties());
        nodes.put(nodeId, newNode);
    }

    public void setEnd(String routeId, String nodeId) throws GraphElementNotFoundException {
        Route route = routes.get(routeId);
        if (route == null) {
            throw new GraphElementNotFoundException(routeId);
        }
        Node newNode = nodes.get(nodeId);
        if (newNode == null) {
            throw new GraphElementNotFoundException(nodeId);
        }
        Node oldNode = route.getEnd();
        oldNode.getEntering().remove(route);
        route.setEnd(newNode);
        newNode.getEntering().add(route);
    }

    public void setStart(String routeId, String nodeId) throws GraphElementNotFoundException {
        Route route = routes.get(routeId);
        if (route == null) {
            throw new GraphElementNotFoundException(routeId);
        }
        Node newNode = nodes.get(nodeId);
        if (newNode == null) {
            throw new GraphElementNotFoundException(nodeId);
        }
        Node oldNode = route.getEnd();
        oldNode.getExiting().remove(route);
        route.setStart(newNode);
        newNode.getExiting().add(route);
    }

    public NarrativeTemplate generateTemplate() throws NonUniqueStartException {
        HashMap<String, Node> r_nodes = new HashMap<>();
        HashMap<String, Route> r_routes = new HashMap<>();

        for (Node node : nodes.values()) {
            Node r_node = node.clone();
            r_node.createProperties();
            r_node.getProperties().remove("GUI.X");
            r_node.getProperties().remove("GUI>Y");
            r_node.setExiting(new ArrayList<Route>());
            r_node.setEntering(new ArrayList<Route>());
            r_nodes.put(node.getId(), r_node);
        }

        for (Route route : routes.values()) {
            Route r_route = route.clone();
            // Find Start and end in r_nodes

            r_route.setStart(r_nodes.get(route.getStart().getId()));
            r_route.setEnd(r_nodes.get(route.getEnd().getId()));
            r_route.getStart().getExiting().add(r_route);
            r_route.getEnd().getEntering().add(r_route);

            r_routes.put(route.getId(), r_route);
        }

        SynchronizationNode start = null;
        for (Node node : r_nodes.values()) {
            if (node.getEntering().size() == 0) {
                if (start == null) {
                    start = (SynchronizationNode) node;
                } else {
                    throw new NonUniqueStartException();
                }
            }
        }
        NarrativeTemplate template = new NarrativeTemplate(r_routes, r_nodes, start,
                BaseBundle.deepcopy(this.properties));
        return template;
    }

    public void saveTemplate(String filename) throws NonUniqueStartException, IOException {
        SaveWriter.saveObject(filename, generateTemplate());
    }

    public Map<String, BaseBundle> getPropertyMapping() {
        return propertyMapping;
    }

    public void createMapping() {
        if (propertyMapping == null) {
            propertyMapping = new HashMap<String, BaseBundle>();
        }
        if (typemap == null) {
            typemap = new HashMap<String, String>();
        }
    }

    public Map<String, String> getPropertyTypes() {
        return typemap;
    }
}
