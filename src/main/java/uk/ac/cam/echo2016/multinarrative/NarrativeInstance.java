package uk.ac.cam.echo2016.multinarrative;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.BaseBundle;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;

/**
 * 
 * Represents an actual play through of the story. Instantiated from the
 * template.
 * 
 * @author tr39
 * @author rjm232
 * @author jr650
 * @version 1.0
 *
 */
public class NarrativeInstance extends MultiNarrative { // TODO Documentation
	public static final String SYSTEM_ISCOMPLETED = "System.isCompleted";
	private static final long serialVersionUID = 1;
	protected ArrayList<Node> activeNodes = new ArrayList<Node>();

	public NarrativeInstance(HashMap<String, Route> routes, HashMap<String, Node> nodes, SynchronizationNode start,
			BaseBundle properties) {
		this.routes = routes;
		this.nodes = nodes;
		this.start = start;
		this.properties = properties;
	}

	public NarrativeInstance() {
	}

	public BaseBundle startRoute(String id) throws GraphElementNotFoundException {
		Debug.logInfo("Starting " + id, 4, Debug.SYSTEM_GRAPH);
		Route route = getRoute(id);
		if (route == null)
			throw new GraphElementNotFoundException(id);
		Node startNode = route.getStart();
		return startNode.startRoute(route, this);
	}

	public GameChoice endRoute(String id) throws GraphElementNotFoundException {
		Debug.logInfo("Ending " + id, 4, Debug.SYSTEM_GRAPH);
		Route route = getRoute(id);
		if (route == null)
			throw new GraphElementNotFoundException(id);
		route.createProperties();
		route.getProperties().putBoolean(SYSTEM_ISCOMPLETED, true);
		Node endNode = route.getEnd();
		if (endNode.isCompleted()) {
			setActive(endNode);
		}
		return endNode.onEntry(route, this);
	}

	/**
	 * Recursively deletes an item from the graph according to the instance this
	 * method is called from. Only nodes and routes further down the tree are
	 * deleted, so nodes must have no entering routes and routes must start from
	 * a node with other exiting routes available.
	 * 
	 * @param id
	 *            string identifier for the item to be deleted
	 * @return
	 */
	public boolean kill(String id) { // TODO More Documentation, including
										// overloaded methods
		Route route = getRoute(id);
		if (route != null) {
			kill(route);
			return true; // TODO change to throw GraphElementNotFoundException?
		} else {
			Node node = getNode(id);
			if (node != null) {
				kill(node);
				return true;
			}
			return false; // TODO change to throw GraphElementNotFoundException?
		}
	}

	/**
	 * {@link NarrativeInstance#kill(String)}
	 * 
	 * @see NarrativeInstance#kill(String)
	 */
	public boolean kill(Route route) {
		if (route == null)
			return false;
		Debug.logInfo("Killing Route " + route.getId(), 3, Debug.SYSTEM_GRAPH);
		Node nEnd = route.getEnd();

		nEnd.getEntering().remove(route);
		// If there are now no routes entering the node, kill it
		if (nEnd.getEntering().size() == 0) {
			kill(nEnd);
		} else {
			// If all the remaining entering routes are completed, set the end
			// node to be active.
			if (nEnd.isCompleted()) {
				setActive(nEnd);
			}
			if (route.getProperties() != null) {
				// Kills all methods leaving the end node if they have the same
				// type and no entering routes also
				// have that property TODO specify in documentation
				Debug.logInfo("Number of properties: " + route.getProperties().keySet().size(), 4, Debug.SYSTEM_GRAPH); // TODO
																														// remove
				for (String key : route.getProperties().keySet()) {
					if (key.startsWith("GUI"))
						continue;

					if (this.getGlobalProperties().getStringArrayList("System.Types").contains(key)) {
						Debug.logInfo("A property of " + route.getId() + " is: " + key, 4, Debug.SYSTEM_GRAPH); // TODO
																												// remove
						// TODO error if no global "Types" property
						Object type = route.getProperties().get(key);

						boolean similarRouteExists = false;
						for (Route entry : nEnd.getEntering()) {
							if (entry.getProperties().containsKey(key) && entry.getProperties().get(key).equals(type)) {
								similarRouteExists = true;
								Debug.logInfo("Similar route found: " + entry.getId() + " for: " + key + "=" + type, 3,
										Debug.SYSTEM_GRAPH);
								break;
							}
						}
						if (!similarRouteExists) {
							for (Route option : new ArrayList<Route>(nEnd.getExiting())) {
								if (option.getProperties() != null && option.getProperties().containsKey(key)
										&& option.getProperties().get(key).getClass() != type.getClass()) {
									Debug.logError("Type mismatch on "+ option +"." + key + " " + type + ":"
											+ type.getClass().getSimpleName() + "!=" + option.getProperties().get(key)
											+ ":" + option.getProperties().get(key).getClass().getSimpleName(), 2,
											Debug.SYSTEM_GRAPH);
								}
								if (option.getProperties().containsKey(key)
										&& option.getProperties().get(key).equals(type)) {
									kill(option);
								}
							}
						}
					}
				}
			}
		}

		// Remove the route from the exiting routes of the node it comes from
		Node nStart = route.getStart();
		nStart.getExiting().remove(route); // Should return true, otherwise
											// something's broken

		routes.remove(route.getId());
		return true;
	}

	/**
	 * {@link NarrativeInstance#kill(String)}
	 * 
	 * @see NarrativeInstance#kill(String)
	 */
	public boolean kill(Node node) {
		if (node == null)
			return false;
		Debug.logInfo("Killing Node " + node.getId(), 3, Debug.SYSTEM_GRAPH);
		for (Route route : new ArrayList<Route>(node.getExiting())) {
			kill(route); // Copy of ArrayList used to allow deletion of nodes
							// within the function
		}

		// As specified in the javadoc
		assert node.getEntering().size() == 0;

		nodes.remove(node.getId());
		return true;
	}

	public ArrayList<Route> getPlayableRoutes() {
		ArrayList<Route> r_routes = new ArrayList<Route>();
		for (Node node : activeNodes) {
			for (Route route : node.getExiting()) {
				if (!route.getProperties().getBoolean(SYSTEM_ISCOMPLETED)) {
					r_routes.add(route);
				}
			}
		}
		return r_routes;
	}

	public ArrayList<Node> getEventfulNodes() {
		ArrayList<Node> r_nodes = new ArrayList<Node>();
		for (Node n : activeNodes) {
			if (n.isCompleted() && (n.getProperties() == null || !n.getProperties().getBoolean(SYSTEM_ISCOMPLETED))) {
				r_nodes.add(n);
			}
		}
		return r_nodes;
	}

	public void setActive(Node node) {
		if (!activeNodes.contains(node)) {
			activeNodes.add(node);
			for (Route r : node.getExiting()) {
				if (r.getProperties() != null && r.getProperties().getBoolean("System.Auto", false)) {
					Debug.logInfo("Auto Playing Route: " + r.getId(), 3, Debug.SYSTEM_GRAPH);
					try {
						startRoute(r.getId());
						endRoute(r.getId());
					} catch (GraphElementNotFoundException e) {
						// SHOULD BE IMPOSIBLE!!
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	public void complete(Node node) {
		if (activeNodes.contains(node)) {
			activeNodes.remove(node);
		}
	}
}
