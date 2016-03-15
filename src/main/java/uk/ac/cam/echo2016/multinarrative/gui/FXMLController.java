package uk.ac.cam.echo2016.multinarrative.gui;

import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import uk.ac.cam.echo2016.multinarrative.GUINarrative;
import uk.ac.cam.echo2016.multinarrative.GraphElementNotFoundException;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;
import uk.ac.cam.echo2016.multinarrative.gui.graph.Graph;
import uk.ac.cam.echo2016.multinarrative.gui.graph.GraphEdge;
import uk.ac.cam.echo2016.multinarrative.gui.graph.GraphNode;
import uk.ac.cam.echo2016.multinarrative.gui.operations.IllegalOperationException;

/**
 * The class used by SceneBuilder as the source of all objects and methods.
 * 
 * @author jr650
 * @author rjm232
 */
public class FXMLController {

	@FXML
	private BorderPane root;
	@FXML
	private ScrollPane scroll;
	@FXML
	private Pane graphArea;
	@FXML
	private Text infoBar;
	@FXML
	private TextField propertyName;
	@FXML
	private Button addProperty;
	@FXML
	private Accordion properties;
	@FXML
	private ListView<String> nodes;
	@FXML
	private ListView<String> routes;

	@FXML
	private TitledPane itemEditor;
	@FXML
	private TextField itemName;
	@FXML
	private ListView<String> itemProperties;
	@FXML
	private Button itemPropertyDelete;
	@FXML
	private TitledPane nodeEditor;
	@FXML
	private RadioButton nodeSynch;
	@FXML
	private RadioButton nodeChoice;
	@FXML
	private TitledPane routeEditor;
	@FXML
	private ComboBox<String> routeStart;
	@FXML
	private ComboBox<String> routeEnd;

	private Boolean itemNode = null;

	private OperationsManager operations = new OperationsManager(this);

	private Graph graph;

	private EditingTool tool;

	private String currentFile;

	private FXMLGUI mainApp;

	private Node propertiesSource = null;
	private ContextMenu propertiesMenu = new ContextMenu();

	/**
	 * Initialise the controller. Called from the GUI bootstrap
	 */
	public void init(FXMLGUI main) {
		Debug.logInfo("Init Controller", 4, Debug.SYSTEM_GUI);
		mainApp = main;

		propertyName.addEventFilter(KeyEvent.KEY_TYPED, event -> {
			if (event.getCharacter().equals("=")) {
				event.consume();
			}
		});
		addProperty.disableProperty().bind(propertyName.textProperty().isEmpty());
		graphArea.minHeightProperty().bind(scroll.heightProperty());
		graphArea.minWidthProperty().bind(scroll.widthProperty());
		nodes.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
					if (newValue != null) {
						routes.getSelectionModel().clearSelection();
						itemNode = true;
						tool.setSelection(newValue);
						initSelect();
					}
				});
		routes.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
					if (newValue != null) {
						nodes.getSelectionModel().clearSelection();
						itemNode = false;
						initSelect();
					}
				});
		itemPropertyDelete.disableProperty()
				.bind(itemProperties.getSelectionModel().selectedIndexProperty().lessThan(0));
		routeStart.itemsProperty().bind(nodes.itemsProperty());
		routeEnd.itemsProperty().bind(nodes.itemsProperty());

		reInit(null, null);
	}

	/**
	 * Used to reinitialise stuff when a new file is loaded
	 * 
	 * @param narrative
	 *            Narrative to load
	 * @param filename
	 *            The file we've just loaded from. Null if not from a file.
	 */
	private void reInit(GUINarrative narrative, String filename) {
		currentFile = filename;

		graph = new Graph(scroll, graphArea, getOperations(), this);
		tool = new EditingTool(graph);
		graph.setTool(tool);
		itemEditor.setDisable(true);
		nodeEditor.setDisable(true);
		routeEditor.setDisable(true);

		nodes.itemsProperty().set(FXCollections.observableArrayList());
		routes.itemsProperty().set(FXCollections.observableArrayList());
	}

	/**
	 * FXML hook. Code run when the "New" menu item is clicked in the File menu.
	 */
	@FXML
	protected void registerNewClicked() {
		try {
			if (operations.dirtyFlag)
				if (mainApp.checkIfShouldSave())
					registerSaveClicked();
		} catch (UserErrorException uee) {
			return;
		}

		reInit(null, null);
		operations.newFile();
	}

	/**
	 * FXML hook. Code run when the "Close" menu item is clicked in the File
	 * menu.
	 */
	@FXML
	protected void close() {
		try {
			if (operations.dirtyFlag)
				if (mainApp.checkIfShouldSave())
					registerSaveClicked();
		} catch (UserErrorException uee) {
			return;
		}
		System.exit(0);
	}

	/**
	 * FXML hook. Code run when "About" is clicked in Help menu.
	 */
	@FXML
	protected void registerAboutClicked() {
		mainApp.showAboutDialog();
	}

	/**
	 * FXML hook. Code run when "Save" clicked in File menu
	 */
	@FXML
	protected void registerSaveClicked() {
		if (currentFile == null) {
			registerSaveAsClicked();
		} else {
			try {
				operations.saveNarrative(currentFile);
			} catch (IOException ioe) {
				showErrorDialog("Error when trying to save file.");
			}
		}
	}

	/**
	 * FXML hook. Code run when "Save as" clicked in the File menu
	 */
	@FXML
	protected void registerSaveAsClicked() {
		String returnedFile = mainApp.showSaveAs();
		if (returnedFile == null) {
			return;
		}

		currentFile = returnedFile;
		try {
			operations.saveNarrative(currentFile);
		} catch (IOException ioe) {
			showErrorDialog("Error when trying to save file.");
		}
	}

	/**
	 * FXML hook. Code run when "Save as" clicked in the File menu
	 */
	@FXML
	protected void registerExportClicked() {
		String returnedFile = mainApp.showExport();
		if (returnedFile == null) {
			return;
		}
		try {
			operations.exportNarrative(returnedFile);
		} catch (IOException ioe) {
			showErrorDialog("Error when trying to save file.");
		} catch (IllegalOperationException e) {
			showErrorDialog(e.getMessage());
		}
	}

	/**
	 * FXML hook. Code run when saving fails
	 */
	@FXML
	protected void showErrorDialog(String message) {
		mainApp.showError(message);
	}

	/**
	 * FXML hook. Code run when "Open" clicked in the File menu
	 */
	@FXML
	protected void registerOpenClicked() {
		try {
			if (operations.dirtyFlag)
				if (mainApp.checkIfShouldSave())
					registerSaveClicked();
		} catch (UserErrorException uee) {
			return;
		}
		String returnedFile = mainApp.showOpen();
		if (returnedFile == null) {
			return;
		}

		currentFile = returnedFile;

		try {
			operations.loadNarrative(returnedFile);
		} catch (IOException | IllegalOperationException ioe) {
			showErrorDialog("Error when trying to open file");
		}
	}

	/**
	 * FXML hook. Code run when "Delete" clicked
	 */
	@FXML
	protected void registerDeleteClicked() {
		removeSelect();
	}

	/**
	 * FXML hook. Code run when "Undo" clicked
	 */
	@FXML
	protected void registerUndoClicked() {
		deselect();
		try {
			operations.undo();
		} catch (IllegalOperationException e) {
			setInfo(e.getMessage());
		}

	}

	/**
	 * FXML hook. Code run when "Undo" clicked
	 */
	@FXML
	protected void registerRedoClicked() {
		Debug.logInfo("Doing redo", 3, Debug.SYSTEM_GUI);
		deselect();
		try {
			operations.redo();
		} catch (IllegalOperationException e) {
			setInfo(e.getMessage());
		}

	}

	/**
	 * FXML hook. Adds a property.
	 * 
	 * @param event
	 *            Triggering event
	 */
	@FXML
	protected void addPropertyButtonAction(ActionEvent event) {

		String name = propertyName.getText();
		try {
			operations.doOp(operations.generator().addProperty(name));
		} catch (IllegalOperationException ioe) {
			setInfo(ioe.getMessage());
		}
	}

	/**
	 * FXML hook. Changes a name
	 * 
	 * @param event
	 */
	@FXML
	protected void setNameEntered(ActionEvent event) {
		changeSelectName();
	}

	/**
	 * FXML hook. Deletes the selected item
	 * 
	 * @param e
	 *            Trigger event
	 */
	@FXML
	protected void deleteItemAction(ActionEvent e) {
		Debug.logInfo("DeleteItemAction", 4, Debug.SYSTEM_GUI);
		if (itemNode != null) {
			if (itemNode) {
				String name = nodes.getSelectionModel().getSelectedItem();
				try {
					operations.doOp(operations.generator().removeNode(name));
				} catch (IllegalOperationException e1) {
					setInfo(e1.getMessage());
				}
			} else {
				String name = routes.getSelectionModel().getSelectedItem();
				try {
					operations.doOp(operations.generator().removeRoute(name));
				} catch (IllegalOperationException e1) {
					setInfo(e1.getMessage());
				}
			}
		}
	}

	/**
	 * FXML hook. Deletes a property from an item
	 * 
	 * @param e
	 */
	@FXML
	protected void deleteItemProperty(ActionEvent e) {
		if (itemNode != null) {
			try {
				String[] s = itemProperties.getSelectionModel().getSelectedItem().split("=");
				String prop = s[0];
				String type = operations.narrativeOperations().getPropertyType(prop);
				if (itemNode) {
					operations.doOp(operations.generator()
							.deletePropertyFromNode(nodes.getSelectionModel().getSelectedItem(), s[0], type));
				} else {
					operations.doOp(operations.generator()
							.deletePropertyFromRoute(routes.getSelectionModel().getSelectedItem(), s[0], type));
				}
			} catch (IllegalOperationException ioe) {
				setInfo(ioe.getMessage());
			}
		}
	}

	/**
	 * FXML hook. Sets a node to choice
	 * 
	 * @param event
	 *            Trigger event.
	 */
	@FXML
	protected void setChoice(ActionEvent event) {
		if (itemNode != null && itemNode) {
			String node = nodes.getSelectionModel().getSelectedItem();
			try {
				if (!operations.narrativeOperations().isChoiceNode(node)) {
					operations.doOp(operations.generator().swapChoiceOrSynch(node));
				}
			} catch (IllegalOperationException e) {
				setInfo(e.getMessage());
			}
		}
	}

	/**
	 * FXML hook. Sets a node to synch
	 * 
	 * @param event
	 *            Trigger event.
	 */
	@FXML
	protected void setSynch(ActionEvent event) {
		if (itemNode != null && itemNode) {
			String node = nodes.getSelectionModel().getSelectedItem();
			try {
				if (operations.narrativeOperations().isChoiceNode(node)) {
					operations.doOp(operations.generator().swapChoiceOrSynch(node));
				}
			} catch (IllegalOperationException e) {
				setInfo(e.getMessage());
			}
		}
	}

	/**
	 * FXML hook. Sets a routes start
	 * 
	 * @param event
	 *            Trigger event.
	 */
	@FXML
	protected void changeStartAction(ActionEvent event) {
		if (itemNode != null && !itemNode) {
			String edge = routes.getSelectionModel().getSelectedItem();
			String start = routeStart.getValue();
			GraphEdge gEdge = graph.getEdges().get(edge);
			if (gEdge == null || !gEdge.getFrom().getName().equals(start)) {
				try {
					operations.doOp(operations.generator().setStart(edge, start));

				} catch (IllegalOperationException e) {
					setInfo(e.getMessage());
					if (gEdge != null) {
						routeEnd.setValue(gEdge.getFrom().getName());
					} else {
						Debug.logError("Could not find: " + edge, 3, Debug.SYSTEM_GUI);
					}
				}
			}
		}
	}

	/**
	 * FXML hook. Sets a routes end
	 * 
	 * @param event
	 *            Trigger event.
	 */
	@FXML
	protected void changeEndAction(ActionEvent event) {
		if (itemNode != null && !itemNode) {
			String edge = routes.getSelectionModel().getSelectedItem();
			String end = routeEnd.getValue();
			GraphEdge gEdge = graph.getEdges().get(edge);
			if (gEdge == null || !gEdge.getTo().getName().equals(end)) {
				try {
					operations.doOp(operations.generator().setEnd(edge, end));
				} catch (IllegalOperationException e) {
					setInfo(e.getMessage());
					if (gEdge != null) {
						routeEnd.setValue(gEdge.getTo().getName());
					} else {
						Debug.logError("Could not find: " + edge, 3, Debug.SYSTEM_GUI);
					}
				}
			}
		}
	}

	/**
	 * FXML hook. Triggers mode change on shift
	 * 
	 * @param event
	 *            Trigger event.
	 */
	@FXML
	protected void onKeyPress(KeyEvent event) {
		Debug.logInfo("Key Pressed: " + event, 5, Debug.SYSTEM_GUI);
		if (event.isConsumed())
			return;
		switch (event.getCode()) {
		case DELETE:
		case BACK_SPACE:
			removeSelect();
			break;
		default:
		}
	}

	/**
	 * FXML hook. Triggers mode change on shift
	 * 
	 * @param event
	 *            Trigger event.
	 */
	@FXML
	protected void onKeyRelease(KeyEvent event) {
		Debug.logInfo("Key Released: " + event, 5, Debug.SYSTEM_GUI);
		if (event.isConsumed())
			return;
		switch (event.getCode()) {
		default:
		}
	}

	/**
	 * Sets the infobar text
	 * 
	 * @param template
	 *            String template
	 * @param values
	 *            Values to interpolate into the text
	 */
	public void setInfo(String info) {
		Debug.logInfo(info, 3, Debug.SYSTEM_GUI);
		infoBar.setText(info);
	}

	/**
	 * Gets the property name text field
	 * 
	 * @return the property name
	 */
	public TextField getPropertyName() {
		return propertyName;
	}

	/**
	 * Gets the panel of properties on the right
	 * 
	 * @return the properties
	 */
	public Accordion getProperties() {
		return properties;
	}

	/**
	 * Get the operations
	 * 
	 * @return the operations
	 */
	public OperationsManager getOperations() {
		return operations;
	}

	/**
	 * Gets the graph object being used
	 * 
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Adds the node Id {@code name} to the list of nodes displayed on the left.
	 * 
	 * @param name
	 */
	public void insertNodeNameIntoList(String name) {
		int i = 0;
		while (i < nodes.getItems().size() && nodes.getItems().get(i).compareTo(name) < 0) {
			i++;
		}
		nodes.getItems().add(i, name);
	}

	/**
	 * Adds the route Id {@code name} to the list of nodes displayed on the
	 * left.
	 * 
	 * @param name
	 */
	public void insertRouteNameIntoList(String name) {
		int i = 0;
		while (i < routes.getItems().size() && routes.getItems().get(i).compareTo(name) < 0) {
			i++;
		}
		routes.getItems().add(i, name);
	}

	/**
	 * Removes a node name from the list on the left
	 * 
	 * @param name
	 *            Name to remove
	 */
	public void removeNodeNameFromList(String name) {
		nodes.getSelectionModel().clearSelection();
		nodes.getItems().remove(name);
		tool.resetSelection();
	}

	/**
	 * Removes a route name from the list on the left
	 * 
	 * @param name
	 *            Name to remove
	 */
	public void removeRouteNameFromList(String name) {
		routes.getSelectionModel().clearSelection();
		routes.getItems().remove(name);
	}

	/**
	 * Select a node for editing
	 * 
	 * @param name
	 */
	public void selectNode(String name) {
		nodes.getSelectionModel().select(name);
		itemNode = true;
		initSelect();
	}

	/**
	 * Select a route for editing
	 * 
	 * @param name
	 */
	public void selectRoute(String name) {
		routes.getSelectionModel().select(name);
		itemNode = false;
		tool.resetSelection();
		initSelect();
	}

	/**
	 * De-selects selected item
	 */
	public void deselect() {
		tool.resetSelection();
		nodes.getSelectionModel().clearSelection();
		routes.getSelectionModel().clearSelection();
		itemNode = null;
		initSelect();
	}

	/**
	 * Initialises components with new selection
	 */
	public void initSelect() {
		itemEditor.setExpanded(itemNode != null);
		itemEditor.setDisable(itemNode == null);
		nodeEditor.setExpanded((itemNode != null) && itemNode);
		nodeEditor.setDisable((itemNode == null) || !itemNode);
		routeEditor.setExpanded((itemNode != null) && !itemNode);
		routeEditor.setDisable((itemNode == null) || itemNode);
		if (itemNode == null) {
			itemName.setText("");
			itemProperties.getItems().clear();
		} else if (itemNode) {
			String node = nodes.getSelectionModel().getSelectedItem();
			itemNode = null;
			itemName.setText(node);
			itemNode = true;
			itemProperties.getItems().clear();
			itemProperties.getItems().addAll(operations.narrativeOperations().getNodeProperties(node));
			try {
				if (operations.narrativeOperations().isChoiceNode(node)) {
					nodeChoice.fire();
				} else {
					nodeSynch.fire();
				}
			} catch (IllegalOperationException e) {
				setInfo(e.getMessage());
			}
		} else {
			String edge = routes.getSelectionModel().getSelectedItem();
			itemNode = null;
			itemName.setText(edge);
			itemNode = false;
			itemProperties.getItems().clear();
			itemProperties.getItems().addAll(operations.narrativeOperations().getRouteProperties(edge));
			GraphEdge gEdge = graph.getEdges().get(edge);
			if (gEdge != null) {
				routeStart.setValue(gEdge.getFrom().getName());
				routeEnd.setValue(gEdge.getTo().getName());
			}
		}
	}

	/**
	 * Assigns a property to a node
	 * 
	 * @param property
	 *            Property name
	 * @param type
	 *            Property type
	 * @param value
	 *            Value of property (guiOperations should check type before
	 *            adding
	 * @throws GraphElementNotFoundException
	 */
	public void assignProperty(String property, String type, String value) throws GraphElementNotFoundException {
		Debug.logInfo("Assigning " + property + ":" + type + "=" + value + " to " + propertiesSource, 4,
				Debug.SYSTEM_GUI);
		if (propertiesSource != null) {
			try {
				Object o = propertiesSource.getUserData();
				if (o instanceof GraphNode) {
					GraphNode n = ((GraphNode) o);
					operations.doOp(operations.generator().assignPropertyToNode(n.getName(), property, type, value));
					if (itemNode != null && itemNode) {
						initSelect();
					}
				} else if (o instanceof GraphEdge) {
					GraphEdge e = ((GraphEdge) o);
					operations.doOp(operations.generator().assignPropertyToRoute(e.getName(), property, type, value));
					if (itemNode != null && !itemNode) {
						initSelect();
					}
				}
				propertiesSource = null;
			} catch (IllegalOperationException e) {
				setInfo(e.getMessage());
			}
		}
	}

	/**
	 * Changes the name of the selection
	 * 
	 * @throws GraphElementNotFoundException
	 */
	public void changeSelectName() {
		if (itemNode != null) {
			String prevName = itemNode ? nodes.getSelectionModel().getSelectedItem()
					: routes.getSelectionModel().getSelectedItem();
			String newName = itemName.getText();
			if (!newName.equals(prevName)) {
				try {
					if (itemNode) {
						operations.doOp(operations.generator().renameNode(prevName, newName));
					} else {
						operations.doOp(operations.generator().renameRoute(prevName, newName));
					}
				} catch (IllegalOperationException ioe) {
					setInfo(ioe.getMessage());
				}
			}
		}
	}

	/**
	 * Removes selected item
	 */
	public void removeSelect() {
		Debug.logInfo("Remove Selection", 4, Debug.SYSTEM_GUI);
		if (itemNode != null) {
			try {
				if (itemNode) {
					String name = nodes.getSelectionModel().getSelectedItem();
					operations.doOp(operations.generator().removeNode(name));
					itemNode = null;
				} else {
					String name = routes.getSelectionModel().getSelectedItem();
					operations.doOp(operations.generator().removeRoute(name));
					itemNode = null;
				}
				initSelect();
			} catch (IllegalOperationException ioe) {
				setInfo(ioe.getMessage());
			}
		}
	}

	/**
	 * Gets the context menu for graph components
	 * 
	 * @return the context menu
	 */
	public ContextMenu getContextMenuForGraph() {
		return propertiesMenu;
	}

	/**
	 * Sets the item that the context menu has been accessed from
	 * 
	 * @param n
	 */
	public void setPropertiesSource(Node n) {
		propertiesSource = n;
	}

	/**
	 * Gets the item that the context menu has been accessed from
	 * 
	 * @return
	 */
	public Node getPropertiesSource() {
		return propertiesSource;
	}

	/**
	 * Gets the area the graph is in
	 * 
	 * @return
	 */
	public Pane getGraphArea() {
		return graphArea;
	}

	/**
	 * Clears everything
	 */
	public void clear() {
		graph.clear();
		graphArea.getChildren().clear();
		nodes.getItems().clear();
		routes.getItems().clear();
	}
}
