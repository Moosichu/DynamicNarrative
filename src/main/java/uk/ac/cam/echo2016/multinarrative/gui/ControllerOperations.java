package uk.ac.cam.echo2016.multinarrative.gui;

import static uk.ac.cam.echo2016.multinarrative.gui.Strings.PROPERTY_ADDED;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.PROPERTY_REMOVED;

import java.io.IOException;
import java.util.ArrayList;

import android.os.BaseBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import uk.ac.cam.echo2016.multinarrative.GUINarrative;
import uk.ac.cam.echo2016.multinarrative.GraphElementNotFoundException;
import uk.ac.cam.echo2016.multinarrative.Node;
import uk.ac.cam.echo2016.multinarrative.Route;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;
import uk.ac.cam.echo2016.multinarrative.gui.graph.Graph;
import uk.ac.cam.echo2016.multinarrative.gui.graph.GraphEdge;
import uk.ac.cam.echo2016.multinarrative.gui.graph.GraphNode;
import uk.ac.cam.echo2016.multinarrative.gui.operations.IllegalOperationException;

/**
 * Class to perform operations on the Visual graph
 * 
 * @author jr650
 *
 */
public class ControllerOperations {

	/**
	 * Controller to use
	 */
	private FXMLController controller;

	/**
	 * The current properties
	 */
	private ArrayList<FXMLPropertyController> props = new ArrayList<>();

	/**
	 * Initialises with source controller
	 * 
	 * @param controller
	 *            the controller to operate on
	 */
	public ControllerOperations(FXMLController controller) {
		this.controller = controller;
	}

	/**
	 * Loads a new property controller from file
	 * 
	 * @param s
	 * @return
	 */
	public FXMLPropertyController createProperty(String s) {
		Debug.logInfo("Create property: " + s, 4, Debug.SYSTEM_GUI);
		try {
			controller.getPropertyName().setText("");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml_property.fxml"));
			loader.load();
			FXMLPropertyController prop = (FXMLPropertyController) loader.getController();
			Menu menu = new Menu(s);
			prop.init(s, controller, menu);
			return prop;
		} catch (IOException ioe) {
			// Indicates that fxml files aren't set up properly...
			throw new RuntimeException("FXML files not configured correctly", ioe);
		}
	}

	/**
	 * Adds a property
	 * 
	 * @param s
	 *            name
	 */
	public void addProperty(FXMLPropertyController prop) {
		Debug.logInfo("Add property: " + prop.getName(), 4, Debug.SYSTEM_GUI);

		controller.getContextMenuForGraph().getItems().add(prop.getMenu());
		controller.getProperties().getPanes().add(prop.getRoot());
		controller.getProperties().setExpandedPane(prop.getRoot());
		controller.setInfo(Strings.populateString(PROPERTY_ADDED, prop.getName()));
		props.add(prop);
	}

	/**
	 * Removes a property
	 * 
	 * @param s
	 *            name
	 * @param pane
	 *            pane to remove
	 * @param menu
	 *            the menu item for it
	 * @return
	 */
	public void removeProperty(FXMLPropertyController prop) {
		Debug.logInfo("Remove property: " + prop.getName(), 4, Debug.SYSTEM_GUI);
		controller.getProperties().getPanes().remove(prop.getRoot());
		controller.getContextMenuForGraph().getItems().remove(prop.getMenu());
		controller.setInfo(Strings.populateString(PROPERTY_REMOVED, prop.getName()));
		props.remove(prop);
	}

	/**
	 * Creates a new synch node
	 * 
	 * @param name
	 *            Id of node must be unique
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param graph
	 *            the graph to add it to (doesn't add, this is just for name
	 *            purposes)
	 * @return The created node
	 * @throws IllegalOperationException
	 */
	public GraphNode createSynchNode(String name, Double x, Double y, Graph graph) throws IllegalOperationException {
		Debug.logInfo("Creating Synch " + name, 4, Debug.SYSTEM_GUI);
		try {
			Button b = FXMLLoader.load(getClass().getResource("synch_button.fxml"));
			b.setText(name);
			b.setOnContextMenuRequested(event -> {
				controller.setPropertiesSource(b);
				controller.getContextMenuForGraph().show(b, event.getScreenX(), event.getScreenY());
			});
			GraphNode newNode = new GraphNode(b, b.textProperty(), x, y, graph);
			return newNode;
		} catch (IOException ioe) {
			// Error with fxml files
			throw new RuntimeException("FXML files not configured correctly", ioe);
		} 
	}

	/**
	 * Creates a new choice node
	 * 
	 * @param name
	 *            Id of node must be unique
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param graph
	 *            the graph to add it to (doesn't add, this is just for name
	 *            purposes)
	 * @return The created node
	 * @throws IllegalOperationException
	 */
	public GraphNode createChoiceNode(String name, Double x, Double y, Graph graph) throws IllegalOperationException {
		Debug.logInfo("Creating Choice " + name, 4, Debug.SYSTEM_GUI);
		try {
			Button b = FXMLLoader.load(getClass().getResource("choice_button.fxml"));
			b.setText(name);
			b.setShape(new Circle(10));
			b.setOnContextMenuRequested(event -> {
				controller.setPropertiesSource(b);
				controller.getContextMenuForGraph().show(b, event.getScreenX(), event.getScreenY());
			});
			GraphNode newNode = new GraphNode(b, b.textProperty(), x, y, graph);
			return newNode;
		} catch (IOException ioe) {
			// Error with fxml files
			throw new RuntimeException("FXML files not configured correctly", ioe);
		} 
	}

	/**
	 * Adds a new node
	 * 
	 * @param node
	 *            the node to add
	 */
	public void addNode(GraphNode node) {
		Debug.logInfo("Adding Node " + node.getName(), 4, Debug.SYSTEM_GUI);
		controller.getGraph().addNode(node);
		controller.insertNodeNameIntoList(node.getName());
	}

	public GraphEdge createRoute(String name, GraphNode from, GraphNode to) {
		CubicCurve c = new CubicCurve();
		c.setStroke(Color.WHITE);
		c.setStrokeWidth(2);
		c.setStrokeLineCap(StrokeLineCap.ROUND);
		c.setFill(Color.TRANSPARENT);
		Polygon p = new Polygon();
		p.getPoints().addAll(25.0, 0.0, 0.0, 8.0, 0.0, -8.0);
		p.setStroke(Color.rgb(127, 127, 127));
		p.setFill(Color.TRANSPARENT);
		p.setOnContextMenuRequested(event -> {
			controller.setPropertiesSource(p);
			controller.getContextMenuForGraph().show(controller.getGraphArea(), event.getScreenX(), event.getScreenY());
		});
		return new GraphEdge(new SimpleStringProperty(name), from, to, c, p, 25, 0);
	}

	/**
	 * Adds a new route
	 * 
	 * @param name
	 *            route id
	 * @param from
	 *            start node
	 * @param to
	 *            end node
	 */
	public void addRoute(GraphEdge edge) {
		Debug.logInfo("Adding Route " + edge.getName(), 4, Debug.SYSTEM_GUI);
		controller.getGraph().addEdge(edge);
		controller.getGraph().updateEdge(edge);
		controller.insertRouteNameIntoList(edge.getName());
	}

	/**
	 * Removes a node
	 * 
	 * @param node
	 *            the node to remove
	 */
	public void removeNode(GraphNode node) {
		controller.getGraph().removeNode(node);
		controller.removeNodeNameFromList(node.getName());
	}

	/**
	 * Removes a route
	 * 
	 * @param name
	 *            route id
	 */
	public void removeRoute(GraphEdge edge) {
		controller.getGraph().removeEdge(edge);
		controller.removeRouteNameFromList(edge.getName());
	}

	/**
	 * Deletes a property value from the selected item
	 * 
	 * @param propertyAndValue
	 * @throws IllegalOperationException
	 */
	public void updateItem(String id) throws IllegalOperationException {
		Debug.logInfo("Updating " + id, 4, Debug.SYSTEM_GUI);
		GraphNode node = controller.getGraph().getNodes().get(id);
		GraphEdge edge = controller.getGraph().getEdges().get(id);
		if (node != null) {
			controller.getGraph().updateNode(node);
		} else if (edge != null) {
			controller.getGraph().updateEdge(edge);
		} else {
			throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, id);
		}
	}

	/**
	 * Renames a node
	 * 
	 * @param oldName
	 *            id of node to rename
	 * @param newName
	 *            new id, must be unique
	 */
	public void renameNode(String oldName, String newName) {
		controller.getGraph().renameNode(oldName, newName);
		controller.removeNodeNameFromList(oldName);
		controller.insertNodeNameIntoList(newName);
		controller.selectNode(newName);
	}

	/**
	 * Renames a route
	 * 
	 * @param oldName
	 *            id of route to rename
	 * @param newName
	 *            new name of route, must be unique
	 */
	public void renameRoute(String oldName, String newName) {
		controller.getGraph().renameRoute(oldName, newName);
		controller.removeRouteNameFromList(oldName);
		controller.insertRouteNameIntoList(newName);
		controller.selectRoute(newName);
	}

	/**
	 * Clears everything (new file)
	 */
	public void clearGraph() {
		controller.clear();
		FXMLPropertyController[] copy = props.toArray(new FXMLPropertyController[0]);
		for (FXMLPropertyController prop : copy) {
			removeProperty(prop);
		}
	}

	/**
	 * Changes type of a node
	 * 
	 * @param node
	 *            id of node to modify
	 */
	public void swapChoiceOrSynch(GraphNode node) {
		if (node.getContents().getShape() == null) {
			node.getContents().setShape(new Circle(10));
		} else {
			node.getContents().setShape(null);
		}
	}

	/**
	 * Sets the start of a route
	 * 
	 * @param edge
	 *            id of route
	 * @param node
	 *            id of node to set as start
	 */
	public void setStart(GraphEdge edge, GraphNode node) {
		edge.setFrom(node);
	}

	/**
	 * Sets the end of a route
	 * 
	 * @param edge
	 *            id of route
	 * @param node
	 *            id of node to set as end
	 */
	public void setEnd(GraphEdge edge, GraphNode node) {
		edge.setTo(node);
	}

	/**
	 * Uses the narrative provided to rebuild the display graph.
	 * 
	 * @throws IllegalOperationException
	 *             if it's an invalid graph
	 */
	public void buildGraph(GUINarrative narrative) throws IllegalOperationException {
		for (String nodeName : narrative.getNodes().keySet()) {
			Node node = narrative.getNode(nodeName);

			double x = node.getProperties() == null ? 0.0 : node.getProperties().getDouble("GUI.X", 0.0);
			double y = node.getProperties() == null ? 0.0 : node.getProperties().getDouble("GUI.Y", 0.0);
			try {
				GraphNode graphNode = narrative.isChoiceNode(nodeName)
						? createChoiceNode(nodeName, x, y, controller.getGraph())
						: createSynchNode(nodeName, x, y, controller.getGraph());
				addNode(graphNode);
			} catch (GraphElementNotFoundException genfe) {
				Debug.logError(genfe.getMessage(), 2, Debug.SYSTEM_IO);
				// Continue loop
			}
		}

		for (String routeName : narrative.getRoutes().keySet()) {
			Route route = narrative.getRoute(routeName);
			GraphNode start = controller.getGraph().getNodes().get(route.getStart().getId());
			GraphNode end = controller.getGraph().getNodes().get(route.getEnd().getId());
			if (start == null) {
				Debug.logError("Did not find node: " + route.getStart().getId(), 2, Debug.SYSTEM_IO);
			}
			if (end == null) {
				Debug.logError("Did not find node: " + route.getEnd().getId(), 2, Debug.SYSTEM_IO);
			}
			if (start != null && end != null) {
				GraphEdge graphRoute = createRoute(routeName, start, end);
				double xOff = route.getProperties() == null ? 0.0 : route.getProperties().getDouble("GUI.X", 0.0);
				double yOff = route.getProperties() == null ? 0.0 : route.getProperties().getDouble("GUI.Y", 0.0);
				graphRoute.translate(xOff, yOff);
				addRoute(graphRoute);
			}

		}

		for (String s : narrative.getPropertyMapping().keySet()) {
			FXMLPropertyController propCont = createProperty(s);
			String type = narrative.getPropertyTypes().get(s);
			propCont.setType(type == null ? "String" : type);
			BaseBundle b = narrative.getPropertyMapping().get(s);
			ArrayList<String> types = narrative.getGlobalProperties().getStringArrayList("System.Types");
			if (types != null && types.contains(s)) {
				propCont.setAsRouteType(true);
			}
			int i = 0;
			for (String item : b.keySet()) {
				propCont.addValue(item, i++);
			}
			addProperty(propCont);
		}
	}
}
