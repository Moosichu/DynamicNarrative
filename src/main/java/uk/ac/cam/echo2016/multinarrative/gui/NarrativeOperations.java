package uk.ac.cam.echo2016.multinarrative.gui;

import static uk.ac.cam.echo2016.multinarrative.gui.Strings.ADD_EMPTY_STRING;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.CANNOT_FORMAT;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.CYCLE_EXISTS;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.GUI_X;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.GUI_Y;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.INVALID_TYPE;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.ITEM_ALREADY_EXISTS;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.ITEM_DOES_NOT_EXIST;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.NODE_PREFIX;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.PROPERTY_VALUE_STRING;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.ROUTE_PREFIX;
import static uk.ac.cam.echo2016.multinarrative.gui.Strings.SYSTEM_PROPERTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.os.BaseBundle;
import javafx.scene.paint.Color;
import uk.ac.cam.echo2016.multinarrative.ChoiceNode;
import uk.ac.cam.echo2016.multinarrative.GUINarrative;
import uk.ac.cam.echo2016.multinarrative.GraphElementNotFoundException;
import uk.ac.cam.echo2016.multinarrative.Node;
import uk.ac.cam.echo2016.multinarrative.NonUniqueIdException;
import uk.ac.cam.echo2016.multinarrative.Route;
import uk.ac.cam.echo2016.multinarrative.SynchronizationNode;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;
import uk.ac.cam.echo2016.multinarrative.gui.operations.DFSCycleDetect;
import uk.ac.cam.echo2016.multinarrative.gui.operations.IllegalOperationException;

/**
 * The class that encapsulates all operations on the narrative structure
 * 
 * 
 * @author jr650
 * @author eyx20
 * @author rjm232
 */
public class NarrativeOperations {

	private GUINarrative multinarrative;

	/**
	 * Valid property types
	 */
	public static final HashSet<String> TYPES = new HashSet<String>(
			Arrays.asList(Strings.TYPE_BOOLEAN, Strings.TYPE_BYTE, Strings.TYPE_DOUBLE, Strings.TYPE_FLOAT,
					Strings.TYPE_INTEGER, Strings.TYPE_LONG, Strings.TYPE_SHORT, Strings.TYPE_STRING));

	private static int nodeCounter = 1;
	private static int narrativeCounter = 1;
	private static int valueCounter = 1;

	/**
	 * Constructor. Creates a new GUIOperations.
	 */
	public NarrativeOperations() {
		multinarrative = new GUINarrative();
	}

	/**
	 * Adds a value to a property
	 * 
	 * @param id
	 *            property
	 * @param value
	 *            value to add
	 * @throws IllegalOperationException
	 */
	public void addPropertyValue(String id, String value) throws IllegalOperationException {
		String type = multinarrative.getPropertyTypes().get(id);
		if (type == null || !isCorrectForType(type, value)) {
			throw new IllegalOperationException("Property value does not match type");
		}
		BaseBundle prop = multinarrative.getPropertyMapping().get(id);
		if (prop == null) {
			throw new IllegalOperationException("Property does not exist.");
		}
		if (prop.containsKey(value)) {
			throw new IllegalOperationException("Value already exists.");
		}
		addValue(id, type, value);

	}

	/**
	 * Adds the required property
	 * 
	 * @throws IllegalOperationException
	 *             if can't add property, message of exception is displayed to
	 *             the user, using the Strings class for formatting.
	 */
	public void addProperty(String s, BaseBundle b) throws IllegalOperationException {

		if (s.equals("") || s == null) {
			throw new IllegalOperationException(ADD_EMPTY_STRING);
		}
		if (multinarrative.getPropertyMapping().containsKey(s)) {
			throw new IllegalOperationException(ITEM_ALREADY_EXISTS, s);
		}
		multinarrative.getPropertyMapping().put(s, b);
		// sets default property type to String. Can change
		// with setPropertyType
		multinarrative.setPropertyType(s, "String");

	}

	/**
	 * Gives whether an input is correct for the given type
	 * 
	 * @param type
	 *            the type to check
	 * @param value
	 *            the value to validate
	 * @return true if can add safely and false otherwise
	 */
	public boolean isCorrectForType(String type, String value) {
		try {
			switch (type) {
			case "String":
				return true;
			case "Integer":
				Integer.parseInt(value);
				return true;
			case "Boolean":
				return value.equals("true") || value.equals("false");
			case "Byte":
				Byte.parseByte(value);
				return true;
			case "Short":
				Short.parseShort(value);
				return true;
			case "Long":
				Long.parseLong(value);
				return true;
			case "Float":
				Float.parseFloat(value);
				return true;
			case "Double":
				Double.parseDouble(value);
				return true;
			default:
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Sets property type, which this class should then use to validate further
	 * input.
	 * 
	 * @param property
	 * @param type
	 * @throws IllegalOperationException
	 */
	public void setPropertyType(String property, String type) throws IllegalOperationException {
		if (!NarrativeOperations.TYPES.contains(type)) {
			throw new IllegalOperationException(INVALID_TYPE);
		}
		multinarrative.setPropertyType(property, type);
		BaseBundle prop = multinarrative.getPropertyMapping().get(property);
		BaseBundle propcopy = new BaseBundle(multinarrative.getPropertyMapping().get(property));
		for (String val : propcopy.keySet()) {
			if (!isCorrectForType(type, val)) {
				prop.remove(val);
			}
		}
	}

	/**
	 * sets property bundle (used by undo())
	 * 
	 * @param propname
	 * @param prop
	 */
	public void setPropertyBundle(String propname, BaseBundle prop) {
		multinarrative.getPropertyMapping().put(propname, prop);
	}

	/**
	 * Gets property type.
	 * 
	 * @param property
	 * @param type
	 * @throws IllegalOperationException
	 */
	public String getPropertyType(String property) throws IllegalOperationException {
		if (property.equals(GUI_X) || property.equals(GUI_Y)) {
			return "Double";
		}
		if (!multinarrative.getPropertyTypes().containsKey(property)) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, property);
		}
		return multinarrative.getPropertyTypes().get(property);
	}

	/**
	 * Adds a value to a given property
	 * 
	 * @param property
	 *            - property name to add value to
	 * @param type
	 *            - data type of the value, selected from {"String", "Integer",
	 *            "Boolean", "Byte", "Short", "Long", "Float", "Double"}
	 * @param value
	 *            - value to be added
	 * @throws IllegalOperationException
	 *             when value cannot be added to the property. Informative
	 *             message is sent to the user.
	 */
	public void addValue(String property, String type, String value) throws IllegalOperationException {
		String proptype = multinarrative.getPropertyTypes().get(property);
		if (!multinarrative.getPropertyMapping().containsKey(property)) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, property);
		}
		if (!type.equals(proptype)) {
			Debug.logError(type + "=\\=" + proptype, 5, Debug.SYSTEM_GUI);
			throw new IllegalOperationException(Strings.INVALID_TYPE);
		}
		try {
			switch (type) {
			case "String":
				multinarrative.getPropertyMapping().get(property).putString(value, value);
				break;
			case "Integer":
				multinarrative.getPropertyMapping().get(property).putInt(value, Integer.parseInt(value));
				break;
			case "Boolean":
				multinarrative.getPropertyMapping().get(property).putBoolean(value, Boolean.parseBoolean(value));
				break;
			case "Byte":
				multinarrative.getPropertyMapping().get(property).putByte(value, Byte.parseByte(value));
				break;
			case "Short":
				multinarrative.getPropertyMapping().get(property).putShort(value, Short.parseShort(value));
				break;
			case "Long":
				multinarrative.getPropertyMapping().get(property).putLong(value, Long.parseLong(value));
				break;
			case "Float":
				multinarrative.getPropertyMapping().get(property).putFloat(value, Float.parseFloat(value));
				break;
			case "Double":
				multinarrative.getPropertyMapping().get(property).putDouble(value, Double.parseDouble(value));
				break;
			default:
				throw new IllegalOperationException(INVALID_TYPE);

			}
		} catch (NumberFormatException e) {
			throw new IllegalOperationException(e, CANNOT_FORMAT, value, type);
		}

	}

	/**
	 * Removes value from property
	 * 
	 * @param id
	 * @param value
	 * @throws IllegalOperationException
	 */
	public void removeValue(String id, String type, String value) throws IllegalOperationException {
		if (!multinarrative.getPropertyMapping().containsKey(id)) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, id);
		}
		multinarrative.getGlobalProperties().remove(id + "=" + value);
		multinarrative.getPropertyMapping().get(id).remove(value);
	}

	/**
	 * get default value for correct type
	 * 
	 * @param id
	 * @param type
	 * @return
	 * @throws IllegalOperationException
	 */
	public String getDefaultValue(String id, String type) throws IllegalOperationException {
		if (!NarrativeOperations.TYPES.contains(type)) {
			throw new IllegalOperationException(INVALID_TYPE);
		}
		if (type.equals("String")) {
			return Strings.populateString(PROPERTY_VALUE_STRING, "" + valueCounter++);
		} else {
			return "" + valueCounter++;
		}
	}

	/**
	 * Gets a name that's not already in the graph.
	 * 
	 * @return new node name
	 */
	public String getUniqueNodeName() {
		String newName = Strings.populateString(NODE_PREFIX, nodeCounter);
		if (multinarrative.getNode(newName) == null) {
			nodeCounter += 1;
			return newName;
		} else {
			while (multinarrative.getNode(newName) != null) {
				nodeCounter += 1;
				newName = Strings.populateString(NODE_PREFIX, nodeCounter);
			}
			nodeCounter += 1;
			return newName;
		}
	}

	/**
	 * Adds a node, throwing exception if it fails.
	 * 
	 * @throws NonUniqueIdException
	 */
	public void addSynchNode(String name, double x, double y) throws IllegalOperationException {

		if (multinarrative.getNode(name) != null) {
			throw new IllegalOperationException(ITEM_ALREADY_EXISTS, name);
		}
		SynchronizationNode newNode = new SynchronizationNode(name);
		newNode.createProperties();
		newNode.getProperties().putDouble(GUI_X, x);
		newNode.getProperties().putDouble(GUI_Y, y);
		multinarrative.getNodes().put(name, newNode);

	}

	/**
	 * Adds a choiceNode at the given position, with the given name
	 */
	public void addChoiceNode(String name, double x, double y) throws IllegalOperationException {

		if (multinarrative.getNode(name) != null) {
			throw new IllegalOperationException(ITEM_ALREADY_EXISTS, name);
		}
		ChoiceNode newNode = new ChoiceNode(name);
		newNode.createProperties();
		newNode.getProperties().putDouble(GUI_X, x);
		newNode.getProperties().putDouble(GUI_Y, y);
		multinarrative.setPropertyType(GUI_X, "Double");
		multinarrative.setPropertyType(GUI_Y, "Double");
		multinarrative.getNodes().put(name, newNode);

	}

	/**
	 * Repositions a node by the given offset
	 */
	public void translateNode(String name, double x, double y) throws IllegalOperationException {
		Node theNode = multinarrative.getNode(name);
		if (theNode == null) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, name);
		}
		if (theNode.getProperties() == null) {
			theNode.createProperties();
		}
		double transx = x + theNode.getProperties().getDouble(GUI_X, 0);
		double transy = y + theNode.getProperties().getDouble(GUI_Y, 0);
		theNode.getProperties().putDouble(GUI_X, transx);
		theNode.getProperties().putDouble(GUI_Y, transy);
	}

	/**
	 * Repositions a node by the given offset
	 */
	public void translateRoute(String name, double x, double y) throws IllegalOperationException {
		Route theRoute = multinarrative.getRoute(name);
		if (theRoute == null) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, name);
		}
		if (theRoute.getProperties() == null) {
			theRoute.createProperties();
		}
		double transx = x + theRoute.getProperties().getDouble(GUI_X, 0);
		double transy = y + theRoute.getProperties().getDouble(GUI_Y, 0);
		theRoute.getProperties().putDouble(GUI_X, transx);
		theRoute.getProperties().putDouble(GUI_Y, transy);
	}

	/**
	 * Gets a name that's not already in the graph.
	 * 
	 * @return unique narrative name
	 */
	public String getUniqueRouteName() {
		String newName = Strings.populateString(ROUTE_PREFIX, narrativeCounter);
		if (!multinarrative.getRoutes().containsKey(newName)) {
			narrativeCounter += 1;
			return newName;
		} else {
			while (multinarrative.getRoutes().containsKey(newName)) {
				narrativeCounter += 1;
				newName = Strings.populateString(ROUTE_PREFIX, narrativeCounter);
			}
			narrativeCounter += 1;
			return newName;
		}
	}

	/**
	 * 
	 * @param node
	 *            Route id
	 * @return List of properties in the form "name:type=value"
	 */
	public List<String> getRouteProperties(String route) {
		BaseBundle props = multinarrative.getRoute(route).getProperties();
		ArrayList<String> r = new ArrayList<String>();
		if (props != null) {
			for (String name : props.keySet()) {
				r.add(name +":"+props.get(name).getClass().getSimpleName()+ "=" + props.get(name).toString());
			}
		}
		return r;
	}

	/**
	 * Adds a route, throwing exception if it fails, due to names existing or a
	 * cycle being created.
	 * 
	 * @param name
	 *            - unique id of the route
	 * @param start
	 *            - starting node. If node does not exist creates a new node.
	 * @param end
	 *            - ending node. If node does not exist creates a new node.
	 */
	public void addRoute(String name, String start, String end, double xOff, double yOff)
			throws IllegalOperationException {
		try {
			multinarrative.newRoute(name, start, end);
			multinarrative.getRoute(name).createProperties();
			BaseBundle b = multinarrative.getProperties(name);
			b.putDouble(GUI_X, xOff);
			b.putDouble(GUI_Y, yOff);
		} catch (NonUniqueIdException e) {
			throw new IllegalOperationException(ITEM_ALREADY_EXISTS, name);
		} catch (GraphElementNotFoundException e) {
			if (e.getItem().equals(start))
				throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, start);
			else
				throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, end);
		}
		DFSCycleDetect cycleDetect = new DFSCycleDetect(multinarrative.getNode(start));
		if (cycleDetect.hasCycle()) {
			multinarrative.removeRoute(name);
			throw new IllegalOperationException(CYCLE_EXISTS);
		}
	}

	/**
	 * 
	 * @param node
	 *            Node id
	 * @return List of properties in the form "name=value"
	 */
	public List<String> getNodeProperties(String node) {
		BaseBundle props = multinarrative.getNode(node).getProperties();
		ArrayList<String> r = new ArrayList<String>();
		if (props != null) {
			for (String name : props.keySet()) {
				r.add(name + "=" + props.get(name).toString());
			}
		}
		return r;
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @throws IllegalOperationException
	 */
	public void renameNode(String from, String to) throws IllegalOperationException {
		if (!from.equals(to) && multinarrative.isUniqueId(to)) {
			multinarrative.renameNode(from, to);
		}

	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @throws IllegalOperationException
	 */
	public void renameRoute(String from, String to) throws IllegalOperationException {

		if (!from.equals(to) && multinarrative.isUniqueId(to))
			multinarrative.renameRoute(from, to);

	}

	/**
	 * 
	 * @param route
	 * @param node
	 */
	public void setEnd(String route, String node) throws IllegalOperationException {

		Route mRoute = multinarrative.getRoute(route);
		Node mOldEnd = mRoute.getEnd();

		try {
			multinarrative.setEnd(route, node);
		} catch (GraphElementNotFoundException e) {
			throw new IllegalOperationException(e, ITEM_DOES_NOT_EXIST, e.getItem());
		}
		DFSCycleDetect detect = new DFSCycleDetect(multinarrative.getRoute(route).getStart());
		if (detect.hasCycle()) {
			try {
				multinarrative.setEnd(route, mOldEnd.getId());
			} catch (GraphElementNotFoundException e) {
				throw new IllegalOperationException(e, ITEM_DOES_NOT_EXIST, e.getItem());
			}
			throw new IllegalOperationException(CYCLE_EXISTS);
		}

	}

	/**
	 * 
	 * @param route
	 * @param node
	 */
	public void setStart(String route, String node) throws IllegalOperationException {

		Route mRoute = multinarrative.getRoute(route);
		Node mOldStart = mRoute.getStart();

		try {
			multinarrative.setStart(route, node);
		} catch (GraphElementNotFoundException e) {
			throw new IllegalOperationException(e, ITEM_DOES_NOT_EXIST, e.getItem());
		}
		DFSCycleDetect detect = new DFSCycleDetect(multinarrative.getRoute(route).getStart());
		if (detect.hasCycle()) {
			try {
				multinarrative.setStart(route, mOldStart.getId());
			} catch (GraphElementNotFoundException e) {
				throw new IllegalOperationException(e, ITEM_DOES_NOT_EXIST, e.getItem());
			}
			throw new IllegalOperationException(CYCLE_EXISTS);
		}

	}

	/**
	 * deletes a node
	 * 
	 * @param id
	 */
	public void deleteNode(String id) {
		multinarrative.removeNode(id);
	}

	/**
	 * deletes a route
	 * 
	 * @param id
	 */
	public void deleteRoute(String id) {
		multinarrative.removeRoute(id);
	}

	/**
	 * Assigns the given property to the correct node
	 * 
	 * @param id
	 * @param property
	 * @param type
	 * @param value
	 * @throws IllegalOperationException
	 */
	public void assignPropertyToNode(String id, String property, String type, String value)
			throws IllegalOperationException {

		Node node = multinarrative.getNode(id);
		if (node == null) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, id);
		}
		if (value == null) {
			if (node.getProperties() != null) {
				node.getProperties().remove(property);
			}
		} else {
			node.createProperties();
			BaseBundle b = node.getProperties();
			try {
				switch (type) {
				case "String":
					b.putString(property, value);
					break;
				case "Integer":
					b.putInt(property, Integer.parseInt(value));
					break;
				case "Boolean":
					b.putBoolean(property, Boolean.parseBoolean(value));
					break;
				case "Byte":
					b.putByte(property, Byte.parseByte(value));
					break;
				case "Short":
					b.putShort(property, Short.parseShort(value));
					break;
				case "Long":
					b.putLong(property, Long.parseLong(value));
					break;
				case "Float":
					b.putFloat(property, Float.parseFloat(value));
					break;
				case "Double":
					b.putDouble(property, Double.parseDouble(value));
					break;
				default:
					throw new IllegalOperationException(INVALID_TYPE);
				}
			} catch (NumberFormatException nfe) {
				throw new IllegalOperationException(nfe, CANNOT_FORMAT, value, type);
			}
		}

	}

	/**
	 * Assigns the given property to the given route
	 * 
	 * @param route
	 * @param property
	 * @param type
	 * @param value
	 * @throws IllegalOperationException
	 */
	public void assignPropertyToRoute(String id, String property, String type, String value)
			throws IllegalOperationException {

		Route route = multinarrative.getRoute(id);
		if (route == null) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, id);
		}
		if (value == null) {
			if (route.getProperties() != null) {
				route.getProperties().remove(property);
			}
		} else {
			route.createProperties();
			BaseBundle b = route.getProperties();
			try {
				switch (type) {
				case "String":
					b.putString(property, value);
					break;
				case "Integer":
					b.putInt(property, Integer.parseInt(value));
					break;
				case "Boolean":
					b.putBoolean(property, Boolean.parseBoolean(value));
					break;
				case "Byte":
					b.putByte(property, Byte.parseByte(value));
					break;
				case "Short":
					b.putShort(property, Short.parseShort(value));
					break;
				case "Long":
					b.putLong(property, Long.parseLong(value));
					break;
				case "Float":
					b.putFloat(property, Float.parseFloat(value));
					break;
				case "Double":
					b.putDouble(property, Double.parseDouble(value));
					break;
				default:
					throw new IllegalOperationException(INVALID_TYPE);
				}
			} catch (NumberFormatException nfe) {
				throw new IllegalOperationException(nfe, CANNOT_FORMAT, value, type);
			}
		}

	}

	/**
	 * Add the property to the String ArrayList global property in GUINarrative
	 * "System.Types"
	 * 
	 * @param property
	 */
	public void setAsRouteType(String property) {
		multinarrative.getGlobalProperties().getStringArrayList("System.Types").add(property);
	}

	/**
	 * Remove the property from the String ArrayList global property in
	 * GUINarrative "System.Types"
	 * 
	 * @param property
	 */
	public void clearAsRouteType(String property) {
		multinarrative.getGlobalProperties().getStringArrayList("System.Types").remove(property);

	}

	/**
	 * Sets the correct colour for the given value of the given property
	 * 
	 * @param property
	 * @param value
	 * @param c
	 */
	public void setColor(String property, String value, Color c) throws IllegalOperationException {
		multinarrative.getGlobalProperties().putDoubleArray(property + "=" + value,
				new double[] { c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity() });
	}

	/**
	 * Gets the colour associated with this value of this property
	 * 
	 * @param property
	 * @param value
	 * @return
	 * @throws IllegalOperationException
	 */
	public Color getColor(String property, String value) throws IllegalOperationException {
		if (multinarrative.getPropertyMapping().get(property) == null) {
			return Color.TRANSPARENT;
		}
		Color c = null;
		try {
			double[] rgba = multinarrative.getGlobalProperties().getDoubleArray(property + "=" + value);
			if (rgba != null) {
				c = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
			}
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new IllegalOperationException("Value " + value + " is not a color");
		}
		return c == null ? Color.TRANSPARENT : c;
	}

	/**
	 * gets a list of all the non transparent colors of properties applying to a
	 * node
	 * 
	 * @return
	 * @throws GraphElementNotFoundException
	 */
	public ArrayList<Color> getNodeColor(String node) throws GraphElementNotFoundException {
		ArrayList<Color> r = new ArrayList<Color>();
		BaseBundle props = multinarrative.getProperties(node);
		for (String prop : props.keySet()) {
			try {
				Color c = getColor(prop, props.get(prop).toString());
				if (c.getOpacity() > 0) {
					r.add(c);
				}
			} catch (IllegalOperationException e) {
				Debug.logError(e, 5, Debug.SYSTEM_GUI);
				// Skip Round
			}
		}
		return r;
	}

	/**
	 * gets a list of all the non transparent colors of properties applying to a
	 * route
	 * 
	 * @return
	 * @throws IllegalOperationException
	 */
	public ArrayList<Color> getRouteColor(String route) throws IllegalOperationException {
		ArrayList<Color> r = new ArrayList<Color>();
		try {
			BaseBundle props = multinarrative.getProperties(route);
			// returns empty list if null, should we createProperties here?
			if (props != null) {
				for (String prop : props.keySet()) {
					try {
						Color c = getColor(prop, props.get(prop).toString());
						if (c.getOpacity() > 0) {
							r.add(c);
						}
					} catch (IllegalOperationException e) {
						Debug.logError(e, 5, Debug.SYSTEM_GUI);
						// Skip Round
					}
				}
			}
			return r;
		} catch (GraphElementNotFoundException e1) {
			throw new IllegalOperationException("Route not found");
		}
	}

	/**
	 * Gives whether the give id is a choice node
	 * 
	 * @param node
	 *            node id
	 * @return true if it's a choice node and false if it's a synch
	 * @throws IllegalOperationException
	 *             if node doesn't exist
	 */
	public boolean isChoiceNode(String node) throws IllegalOperationException {
		try {
			return multinarrative.isChoiceNode(node);
		} catch (GraphElementNotFoundException e) {
			throw new IllegalOperationException(e, ITEM_DOES_NOT_EXIST, e.getItem());
		}
	}

	/**
	 * Swaps the given node from a choice to a sync or vice versa
	 * 
	 * @param node
	 *            node id
	 * @return true if it's a choice node and false if it's a synch
	 * @throws IllegalOperationException
	 *             if node doesn't exist
	 */
	public void switchChoiceOrSynch(String node) throws IllegalOperationException {

		try {
			multinarrative.swapSyncAndChoice(node);
		} catch (GraphElementNotFoundException e) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, e.getItem());
		}
	}

	/**
	 * Removes the required property
	 * 
	 * @throws IllegalOperationException
	 *             if can't remove property, message of exception is displayed
	 *             to the user, using the Strings class for formatting.
	 */
	public void removeProperty(String s) throws IllegalOperationException {
		if (!multinarrative.getPropertyMapping().containsKey(s)) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, s);
		}
		multinarrative.getPropertyMapping().remove(s);

	}

	/**
	 * Gets the bundle of values for a property (used by undo)
	 * 
	 * @param s
	 *            name
	 * @return list of values
	 * @throws IllegalOperationException
	 *             if property doesn't exist
	 */
	public BaseBundle getPropertyBundle(String s) throws IllegalOperationException {
		if (!multinarrative.getPropertyMapping().containsKey(s)) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, s);
		}
		return multinarrative.getPropertyMapping().get(s);
	}

	/**
	 * Changes the name of the required property
	 * 
	 * @throws IllegalOperationException
	 *             if can't rename property, message of exception is displayed
	 *             to the user, using the Strings class for formatting.
	 */
	public void renameProperty(String from, String to) throws IllegalOperationException {

		if (!multinarrative.getPropertyMapping().containsKey(from)) {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, from);
		}
		if (multinarrative.getPropertyMapping().containsKey(to)) {
			throw new IllegalOperationException(ITEM_ALREADY_EXISTS, to);
		}

		String type = multinarrative.getPropertyTypes().get(from);
		BaseBundle oldprop = multinarrative.getPropertyMapping().get(from);
		multinarrative.getPropertyTypes().put(to, type);
		multinarrative.getPropertyMapping().put(to, oldprop);

		multinarrative.getPropertyTypes().remove(from);
		multinarrative.getPropertyMapping().remove(from);
	}

	/**
	 * Deletes a property from a node or route.
	 * 
	 * @param id
	 * @param propertyAndValue
	 * @throws IllegalOperationException
	 */
	public void deleteProperty(String id, String property) throws IllegalOperationException {
		Node node = multinarrative.getNode(id);
		Route route = multinarrative.getRoute(id);
		if (property.equals(GUI_X) || property.equals(GUI_Y)) {
			throw new IllegalOperationException(SYSTEM_PROPERTY, property);
		}
		if (node != null) {
			BaseBundle b = node.getProperties();
			if (b != null) {
				b.remove(property);
			} else {
				throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, property);
			}
		} else if (route != null) {
			BaseBundle b = route.getProperties();
			if (b != null) {
				b.remove(property);
			} else {
				throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, property);
			}
		} else {
			throw new IllegalOperationException(ITEM_DOES_NOT_EXIST, id);
		}

	}

	/**
	 * Loads a narrative
	 * 
	 * @param narrative
	 *            the narrative to load
	 */
	public void loadNarrative(GUINarrative narrative) {
		nodeCounter = 1;
		narrativeCounter = 1;
		valueCounter = 1;
		multinarrative = narrative;
		narrative.createMapping();
	}

	/**
	 * Gets the underlying narrative
	 * 
	 * @return
	 */
	public GUINarrative getNarrative() {
		return multinarrative;
	}

	/**
	 * Creates a new narrative (clears everything)
	 */
	public void newNarrative() {
		multinarrative = new GUINarrative();
	}

	/**
	 * Gives whether a given property is a route type
	 * 
	 * @param propName
	 *            the name of the property
	 * @return true if it's marked as a route type, and false otherwise
	 */
	public boolean isRouteType(String propName) {
		BaseBundle b = multinarrative.getGlobalProperties();
		if (b == null)
			return false;
		ArrayList<String> types = b.getStringArrayList("System.Types");
		return types.contains(propName);
	}

}
