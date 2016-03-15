package uk.ac.cam.echo2016.multinarrative.gui;

import java.io.IOException;
import java.util.ArrayList;

import android.os.BaseBundle;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import uk.ac.cam.echo2016.multinarrative.GUINarrative;
import uk.ac.cam.echo2016.multinarrative.NonUniqueStartException;
import uk.ac.cam.echo2016.multinarrative.Route;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;
import uk.ac.cam.echo2016.multinarrative.gui.graph.GraphEdge;
import uk.ac.cam.echo2016.multinarrative.gui.graph.GraphNode;
import uk.ac.cam.echo2016.multinarrative.gui.operations.CompositeOperation;
import uk.ac.cam.echo2016.multinarrative.gui.operations.IllegalOperationException;
import uk.ac.cam.echo2016.multinarrative.gui.operations.Operation;
import uk.ac.cam.echo2016.multinarrative.gui.operations.OperationGenerator;
import uk.ac.cam.echo2016.multinarrative.gui.operations.UndoableOperationSequence;
import uk.ac.cam.echo2016.multinarrative.io.SaveReader;
import uk.ac.cam.echo2016.multinarrative.io.SaveWriter;

/**
 * Manges gui and narrative operations, adding loading saving undoing and
 * redoing
 * 
 * @author jr650
 *
 */
public class OperationsManager {

	private FXMLController controller;
	private ControllerOperations controllerOperations;
	private NarrativeOperations narrativeOperations;
	private UndoableOperationSequence sequence;
	private Generator generator = new Generator();

	/**
	 * Whether changes have been saved
	 */
	public boolean dirtyFlag = false;

	/**
	 * Initialises from controller
	 * 
	 * @param controller
	 *            the controller to operate on
	 */
	public OperationsManager(FXMLController controller) {
		this.controller = controller;
		controllerOperations = new ControllerOperations(controller);
		narrativeOperations = new NarrativeOperations();
		sequence = new UndoableOperationSequence();
	}

	/**
	 * Does an operation
	 * 
	 * @param o
	 *            operation to perform
	 * @throws IllegalOperationException
	 */
	public void doOp(Operation o) throws IllegalOperationException {
		try {
			sequence.storeAndExecute(o);
			dirtyFlag = true;
		} catch (IllegalOperationException ioe) {
			Debug.logError(ioe, 5, Debug.SYSTEM_GUI);
			throw ioe;
		}
	}

	/**
	 * Undoes last operation
	 * 
	 * @throws IllegalOperationException
	 */
	public void undo() throws IllegalOperationException {
		try {
			sequence.undoLastOperation();
		} catch (IllegalOperationException ioe) {
			Debug.logError(ioe, 5, Debug.SYSTEM_GUI);
			throw ioe;
		}
	}

	/**
	 * Redoes last undo
	 * 
	 * @throws IllegalOperationException
	 */
	public void redo() throws IllegalOperationException {
		try {
			sequence.redoLastUndo();
		} catch (IllegalOperationException ioe) {
			Debug.logError(ioe, 5, Debug.SYSTEM_GUI);
			throw ioe;
		}
	}

	/**
	 * Loads from a file
	 * 
	 * @param filename
	 *            file to load from
	 * @throws IOException
	 *             if can't read from file
	 * @throws IllegalOperationException
	 */
	public void loadNarrative(String filename) throws IOException, IllegalOperationException {
		GUINarrative loaded = SaveReader.loadGUINarrative(filename);
		if (loaded == null) {
			throw new IOException();
		}
		controllerOperations.clearGraph();

		narrativeOperations.loadNarrative(loaded);
		controllerOperations.buildGraph(loaded);

		sequence.clearHistory();
		dirtyFlag = false;
	}

	/**
	 * Saves to a file
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void saveNarrative(String filename) throws IOException {
		GUINarrative narrative = narrativeOperations.getNarrative();
		SaveWriter.saveObject(filename, narrative);
		dirtyFlag = false;
	}

	/**
	 * Exports to a model file
	 * 
	 * @param filename
	 *            file to export to
	 * @throws IOException
	 *             if can't write
	 * @throws IllegalOperationException
	 */
	public void exportNarrative(String filename) throws IOException, IllegalOperationException {
		try {
			narrativeOperations.getNarrative().saveTemplate(filename);
		} catch (NonUniqueStartException e) {
			throw new IllegalOperationException(Strings.NON_UNIQUE_START);
		}
	}

	/**
	 * Gets the narrative operations
	 * 
	 * @return the narrative operations
	 */
	public NarrativeOperations narrativeOperations() {
		return narrativeOperations;
	}

	/**
	 * Gets the controller operations
	 * 
	 * @return the controller operations
	 */
	public ControllerOperations controllerOperations() {
		return controllerOperations;
	}

	/**
	 * Creates a new file
	 */
	public void newFile() {
		narrativeOperations.newNarrative();
		controllerOperations.clearGraph();
		sequence.clearHistory();
		dirtyFlag = false;
	}

	/**
	 * Gets the operation generator for this manager
	 * 
	 * @return the generator
	 */
	public Generator generator() {
		return generator;
	}

	/**
	 * Humongous inner class to provide operations
	 * 
	 * @author James
	 *
	 */
	public class Generator {

		/**
		 * Returns operation to add a property
		 * 
		 * @param s
		 *            Property Name
		 * @return
		 * @throws IllegalOperationException
		 */
		public Operation addProperty(String s) {
			class AddPropertyOperation implements Operation {
				FXMLPropertyController prop;
				BaseBundle b;

				public AddPropertyOperation(String s) {
					prop = controllerOperations.createProperty(s);
					b = new BaseBundle();
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.addProperty(prop.getName(), b);
					controllerOperations.addProperty(prop);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.removeProperty(prop.getName());
					controllerOperations.removeProperty(prop);
				}

			}

			return new AddPropertyOperation(s);
		}

		/**
		 * Returns operation to remove a property
		 * 
		 * @param s
		 * @throws IllegalOperationException
		 */
		public Operation removeProperty(FXMLPropertyController prop) throws IllegalOperationException {
			class RemovePropertyOperation implements Operation {
				FXMLPropertyController prop;
				BaseBundle b;

				public RemovePropertyOperation(FXMLPropertyController prop) throws IllegalOperationException {
					this.prop = prop;
					b = narrativeOperations.getPropertyBundle(prop.getName());
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.removeProperty(prop.getName());
					controllerOperations.removeProperty(prop);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.addProperty(prop.getName(), b);
					controllerOperations.addProperty(prop);
				}

			}

			return new RemovePropertyOperation(prop);
		}

		/**
		 * Returns operation to set the property type
		 * 
		 * @param prop
		 *            Property
		 * @param oldType
		 *            previous type (for undo)
		 * @param type
		 *            new type
		 * @param propCont
		 *            the controller
		 * @return operation that changes type
		 * @throws IllegalOperationException
		 */
		public Operation setPropertyTypeSolo(String prop, String oldType, String type, FXMLPropertyController propCont)
				throws IllegalOperationException {
			class SetPropertyTypeOperation implements Operation {

				public SetPropertyTypeOperation() throws IllegalOperationException {
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.setPropertyType(prop, type);

					propCont.setType(type);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.setPropertyType(prop, oldType);

					propCont.setType(oldType);
				}

			}
			return new SetPropertyTypeOperation();
		}

		/**
		 * Returns operation that removes incompatible values, and then changes
		 * type
		 * 
		 * @param prop
		 *            property name
		 * @param type
		 *            type to change to
		 * @param propCont
		 *            controller
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation setPropertyType(String prop, String type, FXMLPropertyController propCont)
				throws IllegalOperationException {
			ArrayList<OperationGenerator> r = new ArrayList<>();
			BaseBundle b = narrativeOperations.getNarrative().getPropertyMapping().get(prop);
			String oldType = narrativeOperations.getPropertyType(prop);
			if (b == null) {
				throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, prop);
			}
			for (String s : b.keySet()) {
				if (!narrativeOperations.isCorrectForType(type, s)) {
					r.add(() -> removeValue(prop, oldType, s, propCont));
				}
			}
			r.add(() -> setPropertyTypeSolo(prop, oldType, type, propCont));
			if (type.equals(Strings.TYPE_BOOLEAN)) {
				if (!b.containsKey("true")) {
					r.add(() -> addValue(prop, type, "true", 0, propCont));
				}
				if (!b.containsKey("false")) {
					r.add(() -> addValue(prop, type, "false", 1, propCont));
				}
			}
			return new CompositeOperation(r);
		}

		/**
		 * Renames a property
		 * 
		 * @param prop
		 *            property
		 * @param newName
		 *            name to change to
		 * @param propCont
		 *            controller
		 * @return operation that does this
		 */
		public Operation renameProperty(String prop, String newName, FXMLPropertyController propCont) {
			class RenamePropertyAction implements Operation {
				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.renameProperty(prop, newName);
					propCont.setName(newName);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.renameProperty(newName, prop);
					propCont.setName(prop);
				}
			}
			return new RenamePropertyAction();
		}

		/**
		 * Removes a values
		 * 
		 * @param prop
		 *            property
		 * @param type
		 *            type of property
		 * @param value
		 *            value to remove
		 * @param propCont
		 *            controller
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation removeValue(String prop, String type, String value, FXMLPropertyController propCont)
				throws IllegalOperationException {
			class RemovePropertyAction implements Operation {
				int i = propCont.getIndexOf(value);
				Color c;

				public RemovePropertyAction() throws IllegalOperationException {
					c = narrativeOperations.getColor(prop, value);
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.removeValue(prop, type, value);
					propCont.removeValue(value);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.addValue(prop, type, value);
					narrativeOperations.setColor(prop, value, c);
					propCont.addValue(value, i);
				}
			}
			return new RemovePropertyAction();
		}

		/**
		 * Adds a value
		 * 
		 * @param prop
		 *            property
		 * @param type
		 *            type of property
		 * @param value
		 *            value to add
		 * @param index
		 *            position to add it
		 * @param propCont
		 *            controller
		 * @return
		 */
		public Operation addValue(String prop, String type, String value, int index, FXMLPropertyController propCont) {
			class AddPropertyAction implements Operation {
				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.addValue(prop, type, value);
					propCont.addValue(value, index);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.removeValue(prop, type, value);
					propCont.removeValue(value);
				}
			}
			return new AddPropertyAction();
		}

		/**
		 * Renames a value (removes and adds a different one)
		 * 
		 * @param prop
		 *            property
		 * @param type
		 *            type
		 * @param value
		 *            value to remove
		 * @param newValue
		 *            value to add
		 * @param propCont
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation renameValue(String prop, String type, String value, String newValue,
				FXMLPropertyController propCont) throws IllegalOperationException {
			int i = propCont.getIndexOf(value);
			ArrayList<OperationGenerator> r = new ArrayList<>();
			Color c = narrativeOperations.getColor(prop, value);
			if (!narrativeOperations.isCorrectForType(type, value)) {
				throw new IllegalOperationException(Strings.CANNOT_FORMAT, value);
			}
			BaseBundle b = narrativeOperations.getPropertyBundle(prop);
			if (b == null) {
				throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, prop);
			}
			if (b.containsKey(newValue)) {
				throw new IllegalOperationException(Strings.ITEM_ALREADY_EXISTS, newValue);
			}
			r.add(() -> removeValue(prop, type, value, propCont));
			r.add(() -> addValue(prop, type, newValue, i, propCont));
			r.add(() -> setColor(prop, newValue, c, propCont));
			return new CompositeOperation(r);
		}

		/**
		 * Sets the color of a property
		 * 
		 * @param prop
		 *            property
		 * @param value
		 *            value to colour
		 * @param col
		 *            colour to use
		 * @param propCont
		 *            controller
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation setColor(String prop, String value, Color col, FXMLPropertyController propCont)
				throws IllegalOperationException {
			class SetColorOperation implements Operation {
				Color old;

				public SetColorOperation(String prop, String value, Color col) throws IllegalOperationException {
					old = narrativeOperations.getColor(prop, value);
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.setColor(prop, value, col);
					propCont.recolour(value, col);
					controller.getGraph().update();
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.setColor(prop, value, old);
					propCont.recolour(value, old);
					controller.getGraph().update();
				}
			}
			return new SetColorOperation(prop, value, col);
		}

		/**
		 * Sets as a route type
		 * 
		 * @param prop
		 *            property
		 * @param value
		 *            true to set as route type, false to remove
		 * @param check
		 *            linked check box
		 * @return
		 */
		public Operation setRouteType(String prop, boolean value, CheckBox check) {
			class SetRouteTypeOperation implements Operation {

				@Override
				public void execute() throws IllegalOperationException {
					if (value) {
						narrativeOperations.setAsRouteType(prop);
					} else {
						narrativeOperations.clearAsRouteType(prop);
					}
					check.setSelected(value);
				}

				@Override
				public void undo() throws IllegalOperationException {
					if (!value) {
						narrativeOperations.setAsRouteType(prop);
					} else {
						narrativeOperations.clearAsRouteType(prop);
					}
					check.setSelected(!value);
				}

			}
			return new SetRouteTypeOperation();
		}

		/**
		 * Adds a synch node
		 * 
		 * @param s
		 *            name
		 * @param x
		 *            x position
		 * @param y
		 *            y position
		 * @return operation that deos this
		 * @throws IllegalOperationException
		 */
		public Operation addSynchNode(String s, Double x, Double y) throws IllegalOperationException {
			class AddSynchNodeOperation implements Operation {

				GraphNode node;

				public AddSynchNodeOperation(String name, Double x, Double y) throws IllegalOperationException {
					node = controllerOperations.createSynchNode(name, x, y, controller.getGraph());
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.addSynchNode(node.getName(), node.getX(), node.getY());
					controllerOperations.addNode(node);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.deleteNode(node.getName());
					controllerOperations.removeNode(node);
				}
			}
			return new AddSynchNodeOperation(s, x, y);
		}

		/**
		 * Adds a choice node
		 * 
		 * @param s
		 *            name
		 * @param x
		 *            x position
		 * @param y
		 *            y position
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation addChoiceNodeSolo(String s, Double x, Double y) throws IllegalOperationException {
			class AddChoiceNodeOperation implements Operation {

				GraphNode node;

				public AddChoiceNodeOperation(String name, Double x, Double y) throws IllegalOperationException {
					node = controllerOperations.createChoiceNode(name, x, y, controller.getGraph());
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.addChoiceNode(node.getName(), node.getX(), node.getY());
					controllerOperations.addNode(node);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.deleteNode(node.getName());
					controllerOperations.removeNode(node);
				}
			}
			return new AddChoiceNodeOperation(s, x, y);
		}

		/**
		 * Adds a choice node bisecting a route
		 * 
		 * @param s
		 *            name
		 * @param split
		 *            edge to bisect
		 * @return operation that does this (composite)
		 * @throws IllegalOperationException
		 */
		public Operation addChoiceNode(String s, GraphEdge split) throws IllegalOperationException {
			String route2 = narrativeOperations.getUniqueRouteName();
			ArrayList<OperationGenerator> r = new ArrayList<>();
			double x = split.getXOff();
			double y = split.getYOff();
			r.add(() -> addChoiceNodeSolo(s, split.getControl().getLayoutX(), split.getControl().getLayoutY()));
			r.add(() -> addRoute(route2, s, split.getTo().getName()));
			r.add(() -> setEnd(split.getName(), s));
			r.add(() -> translateRoute(split.getName(), -x, -y));
			return new CompositeOperation(r);
		}

		/**
		 * Removes a node
		 * 
		 * @param name
		 *            node to remove
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation removeNodeSolo(String name) throws IllegalOperationException {
			class RemoveNodeOperation implements Operation {

				GraphNode node;
				boolean isChoice;
				BaseBundle props;

				public RemoveNodeOperation(String name) throws IllegalOperationException {
					this.node = controller.getGraph().getNodes().get(name);
					if (node == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, name);
					}
					try {
						props = narrativeOperations.getNarrative().getNodeProperties(name);
					} catch (NullPointerException npe) {
						throw new IllegalOperationException(npe, Strings.ITEM_DOES_NOT_EXIST, name);
					}
					isChoice = narrativeOperations.isChoiceNode(node.getName());
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.deleteNode(node.getName());
					controllerOperations.removeNode(node);
				}

				@Override
				public void undo() throws IllegalOperationException {
					if (isChoice)
						narrativeOperations.addChoiceNode(node.getName(), node.getX(), node.getY());
					else
						narrativeOperations.addSynchNode(node.getName(), node.getX(), node.getY());
					if (props != null) {
						narrativeOperations.getNarrative().getNode(node.getName()).assignProperties(props);
					}
					controllerOperations.addNode(node);
				}

			}
			return new RemoveNodeOperation(name);
		}

		/**
		 * Removes connecting arcs, then removes node
		 * 
		 * @param name
		 *            node to remove
		 * @return Operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation removeNode(String name) throws IllegalOperationException {
			ArrayList<OperationGenerator> items = new ArrayList<>();
			ArrayList<Route> routes = new ArrayList<>();
			try {
				routes.addAll(narrativeOperations.getNarrative().getNode(name).getEntering());
				routes.addAll(narrativeOperations.getNarrative().getNode(name).getExiting());
			} catch (NullPointerException npe) {
				throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, name);
			}
			for (Route r : routes) {
				items.add(() -> removeRoute(r.getId()));
			}
			items.add(() -> removeNodeSolo(name));
			return new CompositeOperation(items);
		}

		/**
		 * Adds a route
		 * 
		 * @param s
		 *            name
		 * @param from
		 *            source node
		 * @param to
		 *            destination node
		 * @return operation that deos this
		 * @throws IllegalOperationException
		 */
		public Operation addRoute(String s, String from, String to) throws IllegalOperationException {
			class AddRouteOperation implements Operation {

				GraphEdge edge;

				public AddRouteOperation(String name, String start, String end) throws IllegalOperationException {
					GraphNode from = controller.getGraph().getNodes().get(start);
					if (from == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, start);
					}
					GraphNode to = controller.getGraph().getNodes().get(end);
					if (to == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, end);
					}
					edge = controllerOperations.createRoute(name, from, to);
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.addRoute(edge.getName(), edge.getFrom().getName(), edge.getTo().getName(),
							edge.getXOff(), edge.getYOff());
					controllerOperations.addRoute(edge);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.deleteRoute(edge.getName());
					controllerOperations.removeRoute(edge);
				}

			}
			return new AddRouteOperation(s, from, to);
		}

		/**
		 * Removes a route
		 * 
		 * @param s
		 *            name of route
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation removeRoute(String s) throws IllegalOperationException {
			class RemoveRouteOperation implements Operation {

				GraphEdge edge;
				BaseBundle props;

				public RemoveRouteOperation(String s) throws IllegalOperationException {
					edge = controller.getGraph().getEdges().get(s);
					if (edge == null)
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, s);
					try {
						props = narrativeOperations.getNarrative().getRouteProperties(s);
					} catch (NullPointerException npe) {
						throw new IllegalOperationException(npe, Strings.ITEM_DOES_NOT_EXIST, s);
					}
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.deleteRoute(edge.getName());
					controllerOperations.removeRoute(edge);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.addRoute(edge.getName(), edge.getFrom().getName(), edge.getTo().getName(),
							edge.getXOff(), edge.getYOff());
					if (props != null) {
						narrativeOperations.getNarrative().getRoute(edge.getName()).assignProperties(props);
					}
					controllerOperations.addRoute(edge);
					controller.getGraph().updateEdge(edge);
				}
			}
			return new RemoveRouteOperation(s);
		}

		/**
		 * Changes type of node
		 * 
		 * @param s
		 *            name of node
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation swapChoiceOrSynch(String s) throws IllegalOperationException {
			class SwapChoiceOrSynchOperation implements Operation {
				GraphNode node;

				public SwapChoiceOrSynchOperation(String name) throws IllegalOperationException {
					node = controller.getGraph().getNodes().get(name);
					if (node == null)
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, name);
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.switchChoiceOrSynch(node.getName());
					controllerOperations.swapChoiceOrSynch(node);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.switchChoiceOrSynch(node.getName());
					controllerOperations.swapChoiceOrSynch(node);
				}
			}
			return new SwapChoiceOrSynchOperation(s);
		}

		/**
		 * Moves a node
		 * 
		 * @param s
		 *            name of node
		 * @param x
		 *            x distance
		 * @param y
		 *            y distance
		 * @return
		 * @throws IllegalOperationException
		 */
		public Operation translateNode(String s, double x, double y) throws IllegalOperationException {
			class TranslateNodeOperation implements Operation {
				GraphNode node;
				double x, y;

				public TranslateNodeOperation(String s, double x, double y) throws IllegalOperationException {
					node = controller.getGraph().getNodes().get(s);
					if (node == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, s);
					}
					this.x = x;
					this.y = y;
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.translateNode(node.getName(), x, y);
					node.translate(x, y);
					controller.getGraph().updateNode(node);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.translateNode(node.getName(), -x, -y);
					node.translate(-x, -y);
					controller.getGraph().updateNode(node);
				}
			}
			return new TranslateNodeOperation(s, x, y);
		}

		/**
		 * Translates a route
		 * 
		 * @param s
		 *            name of route
		 * @param x
		 *            x distance
		 * @param y
		 *            y distance
		 * @return
		 * @throws IllegalOperationException
		 */
		public Operation translateRoute(String s, double x, double y) throws IllegalOperationException {
			class TranslateRouteOperation implements Operation {
				GraphEdge route;
				double x, y;

				public TranslateRouteOperation(String s, double x, double y) throws IllegalOperationException {
					route = controller.getGraph().getEdges().get(s);
					if (route == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, s);
					}
					this.x = x;
					this.y = y;
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.translateRoute(route.getName(), x, y);
					route.translate(x, y);
					controller.getGraph().updateEdge(route);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.translateRoute(route.getName(), -x, -y);
					route.translate(-x, -y);
					controller.getGraph().updateEdge(route);
				}
			}
			return new TranslateRouteOperation(s, x, y);
		}

		/**
		 * Sets the start of a route
		 * 
		 * @param name
		 *            route to change
		 * @param start
		 *            start to set
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation setStart(String name, String start) throws IllegalOperationException {
			class SetStartOperation implements Operation {
				GraphEdge edge;
				GraphNode oldStart;
				GraphNode newStart;

				public SetStartOperation(String name, String start) throws IllegalOperationException {
					if (name == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, "null");
					}
					if (start == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, "null");
					}
					edge = controller.getGraph().getEdges().get(name);
					if (edge == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, name);
					}
					oldStart = edge.getFrom();
					newStart = controller.getGraph().getNodes().get(start);
					if (newStart == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, start);
					}
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.setStart(edge.getName(), newStart.getName());
					controllerOperations.setStart(edge, newStart);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.setStart(edge.getName(), oldStart.getName());
					controllerOperations.setStart(edge, oldStart);
				}
			}
			return new SetStartOperation(name, start);
		}

		/**
		 * Sets the end of a route
		 * 
		 * @param name
		 *            name of route
		 * @param end
		 *            node to set end to
		 * @return operation that does this
		 * @throws IllegalOperationException
		 */
		public Operation setEnd(String name, String end) throws IllegalOperationException {
			class SetEndOperation implements Operation {
				GraphEdge edge;
				GraphNode oldEnd;
				GraphNode newEnd;

				public SetEndOperation(String name, String end) throws IllegalOperationException {
					if (name == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, "null");
					}
					if (end == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, "null");
					}
					edge = controller.getGraph().getEdges().get(name);
					if (edge == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, name);
					}
					oldEnd = edge.getTo();
					newEnd = controller.getGraph().getNodes().get(end);
					if (newEnd == null) {
						throw new IllegalOperationException(Strings.ITEM_DOES_NOT_EXIST, end);
					}
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.setEnd(edge.getName(), newEnd.getName());
					controllerOperations.setEnd(edge, newEnd);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.setEnd(edge.getName(), oldEnd.getName());
					controllerOperations.setEnd(edge, oldEnd);
				}
			}
			return new SetEndOperation(name, end);
		}

		/**
		 * Renames a node
		 * 
		 * @param node
		 *            name of node
		 * @param name
		 *            new name
		 * @return operation that does this
		 */
		public Operation renameNode(String node, String name) {
			class RenameNodeOperation implements Operation {
				String to;
				String from;

				public RenameNodeOperation(String node, String name) {
					to = name;
					from = node;
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.renameNode(from, to);
					controllerOperations.renameNode(from, to);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.renameNode(to, from);
					controllerOperations.renameNode(to, from);
				}
			}
			return new RenameNodeOperation(node, name);
		}

		/**
		 * Renames a route
		 * 
		 * @param route
		 *            name of route
		 * @param name
		 *            new name to change to
		 * @return operation that does this
		 */
		public Operation renameRoute(String route, String name) {
			class RenameRouteOperation implements Operation {
				String to;
				String from;

				public RenameRouteOperation(String route, String name) {
					to = name;
					from = route;
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.renameRoute(from, to);
					controllerOperations.renameRoute(from, to);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.renameRoute(to, from);
					controllerOperations.renameRoute(to, from);
				}
			}
			return new RenameRouteOperation(route, name);
		}

		/**
		 * Assigns a property to a node
		 * 
		 * @param node
		 *            name of node
		 * @param prop
		 *            name of property
		 * @param type
		 *            type of property
		 * @param value
		 *            value to assign
		 * @return operation that does this
		 */
		public Operation assignPropertyToNode(String node, String prop, String type, String value) {
			class AssignPropertyToNodeOperation implements Operation {
				String old = null;

				public AssignPropertyToNodeOperation() {
					if (narrativeOperations.getNarrative().getNodeProperties(node).get(prop) != null) {
						old = narrativeOperations.getNarrative().getNodeProperties(node).get(prop).toString();
					}
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.assignPropertyToNode(node, prop, type, value);
					controllerOperations.updateItem(node);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.assignPropertyToNode(node, prop, type, old);
					controllerOperations.updateItem(node);
				}

			}
			return new AssignPropertyToNodeOperation();
		}

		/**
		 * Assigns a property to a route
		 * 
		 * @param node
		 *            name of route
		 * @param prop
		 *            name of property
		 * @param type
		 *            type of property
		 * @param value
		 *            value to assign
		 * @return operation that does this
		 */
		public Operation assignPropertyToRoute(String route, String prop, String type, String value) {
			class AssignPropertyToRouteOperation implements Operation {
				String old = null;

				public AssignPropertyToRouteOperation() {
					if (narrativeOperations.getNarrative().getRouteProperties(route).get(prop) != null) {
						old = narrativeOperations.getNarrative().getRouteProperties(route).get(prop).toString();
					}
				}

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.assignPropertyToRoute(route, prop, type, value);
					controllerOperations.updateItem(route);
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.assignPropertyToRoute(route, prop, type, old);
					controllerOperations.updateItem(route);
				}

			}
			return new AssignPropertyToRouteOperation();
		}

		/**
		 * Deletes a property from a node
		 * 
		 * @param node
		 *            name of node
		 * @param prop
		 *            name of property
		 * @param type
		 *            type of property
		 * @return operation that does this
		 */
		public Operation deletePropertyFromNode(String node, String prop, String type) {
			class AssignPropertyToNodeOperation implements Operation {
				String old = narrativeOperations.getNarrative().getNodeProperties(node).get(prop).toString();

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.deleteProperty(node, prop);
					controllerOperations.updateItem(node);
					controller.initSelect();
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.assignPropertyToNode(node, prop, type, old);
					controllerOperations.updateItem(node);
				}

			}
			return new AssignPropertyToNodeOperation();
		}

		/**
		 * Deletes a property from a route
		 * 
		 * @param node
		 *            name of route
		 * @param prop
		 *            name of property
		 * @param type
		 *            type of property
		 * @return operation that does this
		 */
		public Operation deletePropertyFromRoute(String route, String prop, String type) {
			class AssignPropertyToRouteOperation implements Operation {
				String old = narrativeOperations.getNarrative().getRouteProperties(route).get(prop).toString();

				@Override
				public void execute() throws IllegalOperationException {
					narrativeOperations.deleteProperty(route, prop);
					controllerOperations.updateItem(route);
					controller.initSelect();
				}

				@Override
				public void undo() throws IllegalOperationException {
					narrativeOperations.assignPropertyToRoute(route, prop, type, old);
					controllerOperations.updateItem(route);
				}

			}
			return new AssignPropertyToRouteOperation();
		}
	}
}
