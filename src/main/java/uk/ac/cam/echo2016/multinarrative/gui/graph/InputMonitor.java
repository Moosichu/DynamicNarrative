package uk.ac.cam.echo2016.multinarrative.gui.graph;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;

/**
 * Class to deal with input to the graph
 * 
 * @author jr650
 *
 */
public class InputMonitor {

	private EventHandler<MouseEvent> dragStartHandler;
	private EventHandler<MouseDragEvent> dragReleasedHandler;

	private EventHandler<MouseEvent> mousePressedEventHandler;
	private EventHandler<MouseEvent> mouseDraggedEventHandler;
	private EventHandler<MouseEvent> mouseReleasedEventHandler;

	/**
	 * Creates a monitor for the given graph
	 * @param graph graph to monitor
	 */
	public InputMonitor(final Graph graph) {

		mousePressedEventHandler = event -> {
			Object source = event.getSource();

			if (source instanceof Node) {
				Node node = (Node) source;
				Object graphElement = node.getUserData();

				if (graphElement instanceof GraphNode) {
					Debug.logInfo("Mouse Pressed on Node", 5, Debug.SYSTEM_GUI);
					graph.getTool().mousePressedOnNode(event, (GraphNode) graphElement);
				} else if (graphElement instanceof GraphEdge) {
					Debug.logInfo("Mouse Pressed on Edge", 5, Debug.SYSTEM_GUI);
					graph.getTool().mousePressedOnEdge(event, (GraphEdge) graphElement);
				} else {
					Debug.logInfo("Mouse Pressed", 5, Debug.SYSTEM_GUI);
					graph.getTool().mousePressed(event);
				}
			} else {
				Debug.logInfo("Mouse Pressed", 5, Debug.SYSTEM_GUI);
				graph.getTool().mousePressed(event);
			}

			event.consume();
		};

		mouseDraggedEventHandler = event -> {
			Debug.logInfo("Mouse Dragged", 5, Debug.SYSTEM_GUI);
			graph.getTool().mouseDragged(event);
			event.consume();
		};

		mouseReleasedEventHandler = event -> {
			Debug.logInfo("Mouse Released", 5, Debug.SYSTEM_GUI);
			graph.getTool().mouseReleased(event);
			event.consume();
		};

		dragStartHandler = event -> {
			Debug.logInfo("Mouse Drag Start", 5, Debug.SYSTEM_GUI);
			graph.getTool().dragStart(event);
			event.consume();
		};

		dragReleasedHandler = event -> {
			Object source = event.getSource();
			Debug.logInfo("Mouse Drag End", 5, Debug.SYSTEM_GUI);
			if (source instanceof Node) {
				Node node = (Node) source;
				Object graphElement = node.getUserData();

				if (graphElement instanceof GraphNode) {
					Debug.logInfo("Mouse Released on Node", 5, Debug.SYSTEM_GUI);
					graph.getTool().mouseReleasedOnNode(event, (GraphNode) graphElement);
				} else if (graphElement instanceof GraphEdge) {
					Debug.logInfo("Mouse Released on Edge", 5, Debug.SYSTEM_GUI);
					graph.getTool().mouseReleasedOnEdge(event, (GraphEdge) graphElement);
				}
			}

			event.consume();
		};
	}

	/**
	 * Add handler to graph components
	 * @param node node to add handlers for
	 */
	public void registerHandlerFor(Node node) {
		node.setOnDragDetected(dragStartHandler);
		node.setOnMouseDragReleased(dragReleasedHandler);

		node.setOnMouseDragged(mouseDraggedEventHandler);
		node.setOnMousePressed(mousePressedEventHandler);
		node.setOnMouseReleased(mouseReleasedEventHandler);

	}
}