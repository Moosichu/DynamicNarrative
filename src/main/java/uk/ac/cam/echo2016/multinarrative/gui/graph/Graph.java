package uk.ac.cam.echo2016.multinarrative.gui.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;
import uk.ac.cam.echo2016.multinarrative.gui.FXMLController;
import uk.ac.cam.echo2016.multinarrative.gui.OperationsManager;

/**
 * Graph structure for gui
 * 
 * @author jr650
 *
 */
public class Graph {

	private Pane pane;
	private OperationsManager operations;

	private InputMonitor input;
	private GraphTool tool;

	private Map<String, GraphNode> nodes = new HashMap<>();
	private Map<String, GraphEdge> edges = new HashMap<>();

	private FXMLController controller;

	/**
	 * Creates a new graph
	 * 
	 * @param pane
	 *            containing scroll pane
	 * @param p
	 *            pane to add graph to
	 * @param operations
	 *            manager to add remove stuff to
	 * @param c
	 *            controller
	 */
	public Graph(ScrollPane pane, Pane p, OperationsManager operations, FXMLController c) {
		this.pane = p;
		this.operations = operations;
		controller = c;
		input = new InputMonitor(this);
		input.registerHandlerFor(p);
		input.registerHandlerFor(pane);
	}

	/**
	 * Sets graph tool
	 * 
	 * @param tool
	 *            tool to set
	 */
	public void setTool(GraphTool tool) {
		this.tool = tool;
	}

	/**
	 * Gets the current tool
	 * 
	 * @return
	 */
	public GraphTool getTool() {
		return tool;
	}

	/**
	 * Gets the nodes
	 * 
	 * @return the nodes
	 */
	public Map<String, GraphNode> getNodes() {
		return nodes;
	}

	/**
	 * Gets the edges
	 * 
	 * @return the edges
	 */
	public Map<String, GraphEdge> getEdges() {
		return edges;
	}

	/**
	 * Updates everything
	 */
	public void update() {
		for (GraphEdge edge : edges.values()) {
			updateEdge(edge);
		}
		for (GraphNode node : nodes.values()) {
			updateNode(node);
		}
	}

	/**
	 * Updates an edge
	 * 
	 * @param edge
	 *            edge to update
	 */
	public void updateEdge(GraphEdge edge) {
		edge.update(this);
	}

	/**
	 * Updates a node
	 * 
	 * @param node
	 *            node to update
	 */
	public void updateNode(GraphNode node) {
		node.update(this);
	}

	/**
	 * Adds a new node
	 * 
	 * @param node
	 *            node to add
	 */
	public void addNode(GraphNode node) {
		Node contents = node.getContents();
		pane.getChildren().add(contents);
		nodes.put(node.getName(), node);
		input.registerHandlerFor(contents);
	}

	/**
	 * Removes a node
	 * 
	 * @param node
	 *            node to remove
	 */
	public void removeNode(GraphNode node) {
		Node contents = node.getContents();
		pane.getChildren().remove(contents);
		nodes.remove(node);
		for (GraphEdge edge : new ArrayList<>(edges.values())) {
			if (edge.getFrom() == node || edge.getTo() == node) {
				removeEdge(edge);
			}
		}
	}

	/**
	 * Adds an edge
	 * 
	 * @param edge
	 *            edge to add
	 */
	public void addEdge(GraphEdge edge) {
		CubicCurve n = edge.getNode();
		Shape c = edge.getControl();
		pane.getChildren().add(0, n);
		int i = 1;
		while (pane.getChildren().get(i) instanceof CubicCurve) {
			i++;
		}
		pane.getChildren().add(i, c);
		edge.update(this);
		edges.put(edge.getName(), edge);
		input.registerHandlerFor(n);
		input.registerHandlerFor(c);
	}

	/**
	 * Removes an edge
	 * 
	 * @param edge
	 *            edge to remove
	 */
	public void removeEdge(GraphEdge edge) {
		pane.getChildren().remove(edge.getNode());
		pane.getChildren().remove(edge.getControl());
		edges.remove(edge);
	}

	/**
	 * Gets the input handler
	 * 
	 * @return the input handler
	 */
	public InputMonitor getInput() {
		return input;
	}

	/**
	 * Gets the controller
	 * 
	 * @return the controller
	 */
	public FXMLController getController() {
		return controller;
	}

	/**
	 * Gets the operations
	 * 
	 * @return the operations
	 */
	public OperationsManager getOperations() {
		return operations;
	}

	/**
	 * Gets the pane
	 * 
	 * @return the pane
	 */
	public Pane getPane() {
		return pane;
	}

	/**
	 * Renames a node
	 * 
	 * @param from
	 *            name of node
	 * @param to
	 *            name to rename to
	 */
	public void renameNode(String from, String to) {
		GraphNode node = nodes.remove(from);
		if (node != null) {
			node.getNameProperty().set(to);
			nodes.put(to, node);
			updateNode(node);
		}
	}

	/**
	 * Renames a route
	 * 
	 * @param from
	 *            name of route
	 * @param to
	 *            name to rename to
	 */
	public void renameRoute(String from, String to) {
		GraphEdge edge = edges.remove(from);
		if (edge != null) {
			edge.getNameProperty().set(to);
			edges.put(to, edge);
		}
	}

	/**
	 * Deletes a node
	 * 
	 * @param name
	 *            node to delete
	 */
	public void deleteNode(String name) {
		GraphNode node = nodes.get(name);
		if (node != null)
			removeNode(node);
	}

	/**
	 * Deletes a route
	 * 
	 * @param name
	 *            route to delete
	 */
	public void deleteRoute(String name) {
		GraphEdge edge = edges.get(name);
		if (edge != null)
			removeEdge(edge);
	}

	/**
	 * Clears everything
	 */
	public void clear() {
		nodes.clear();
		edges.clear();
	}

}
