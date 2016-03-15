package uk.ac.cam.echo2016.multinarrative.gui.graph;

import javafx.scene.input.MouseEvent;

/**
 * Tools for interracting with a graph
 * 
 * @author jr650
 *
 */
public interface GraphTool {

	/**
	 * Mouse pressed on the graph (not a node or edge)
	 */
	void mousePressed(MouseEvent event);

	/**
	 * Mouse released on the graph (called just after released on node/edge)
	 */
	void mouseReleased(MouseEvent event);

	/**
	 * Mouse dragged on the graph
	 */
	void mouseDragged(MouseEvent event);

	/**
	 * Mouse pressed on a node
	 */
	void mousePressedOnNode(MouseEvent event, GraphNode node);

	/**
	 * Mouse pressed on an edge
	 */
	void mousePressedOnEdge(MouseEvent event, GraphEdge edge);

	/**
	 * Mouse released on a node
	 */
	void mouseReleasedOnNode(MouseEvent event, GraphNode node);

	/**
	 * Mouse released on an edge
	 */
	void mouseReleasedOnEdge(MouseEvent event, GraphEdge edge);

	/**
	 * Dragging has started
	 */
	void dragStart(MouseEvent event);
}
