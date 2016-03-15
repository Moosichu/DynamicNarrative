package uk.ac.cam.echo2016.multinarrative.gui.graph;

import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import uk.ac.cam.echo2016.multinarrative.GraphElementNotFoundException;
import uk.ac.cam.echo2016.multinarrative.gui.FXUtil;

/**
 * Class for nodes in the graph
 * 
 * @author jr650
 *
 */
public class GraphNode {

	private Region contents;

	private StringProperty name;

	private DoubleProperty x = new SimpleDoubleProperty();
	private DoubleProperty y = new SimpleDoubleProperty();

	/**
	 * Creates new node
	 * @param node Region to use as visuals
	 * @param name name of node
	 * @param x x position
	 * @param y y position
	 * @param g graph context
	 */
	public GraphNode(Region node, StringProperty name, double x, double y, Graph g){
		node.setUserData(this);
		contents = node;
		this.name = name;
		node.layoutXProperty().bind(this.x);
		node.layoutYProperty().bind(this.y);
		this.x.set(x);
		this.y.set(y);
		update(g);
	}

	/**
	 * Sets position
	 * @param x x position
	 * @param y y position
	 */
	public void setPosition(double x, double y) {
		this.x.set(x);
		this.y.set(y);
	}

	/**
	 * Translates node
	 * @param x x distance
	 * @param y y distance
	 */
	public void translate(double x, double y) {
		this.x.set(this.x.get() + x);
		this.y.set(this.y.get() + y);
	}

	/**
	 * Gets x
	 * @return the x
	 */
	public double getX() {
		return x.get();
	}

	/**
	 * Gets x property
	 * @return the x property
	 */
	public DoubleProperty xProperty() {
		return x;
	}

	/**
	 * Gets y
	 * @return the y
	 */
	public double getY() {
		return y.get();
	}

	/**
	 * Gets y property
	 * @return the y property
	 */
	public DoubleProperty yProperty() {
		return y;
	}

	/**
	 * Gets the contents
	 * @return the contents
	 */
	public Region getContents() {
		return contents;
	}

	/**
	 * Gets name property
	 * @return the name property
	 */
	public StringProperty getNameProperty() {
		return name;
	}

	/**
	 * Gets name 
	 * @return the name 
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * Updates colour
	 * @param g
	 */
	public void update(Graph g) {

		ArrayList<Color> c;
		try {
			c = g.getOperations().narrativeOperations().getNodeColor(name.get());
		} catch (GraphElementNotFoundException e) {
			c = new ArrayList<>();
		}
		if (c.isEmpty()) {
			contents.setStyle("-fx-background-color: #333333;");
		} else if (c.size() == 1) {
			contents.setStyle("-fx-background-color: " + FXUtil.colorToHex(c.get(0)) + ";");
		} else {
			String s = "-fx-background-color: linear-gradient(to right";
			for (Color col : c) {
				s += ", " + FXUtil.colorToHex(col);
			}
			s += ");";
			contents.setStyle(s);
		}
	}

}
