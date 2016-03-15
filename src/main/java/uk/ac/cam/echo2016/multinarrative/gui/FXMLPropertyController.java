package uk.ac.cam.echo2016.multinarrative.gui;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;
import uk.ac.cam.echo2016.multinarrative.gui.operations.IllegalOperationException;

/**
 * FXML managing class for a single property pane
 * 
 * @author jr650
 */
public class FXMLPropertyController implements Initializable {

	private String propName;

	private String typeName = "String";

	@FXML
	private TitledPane root;
	@FXML
	private ListView<String> values;
	@FXML
	private ComboBox<String> type;
	@FXML
	private TextField name;
	@FXML
	private BorderPane propertyPane;
	@FXML
	private Button add;
	@FXML
	private Button remove;
	@FXML
	private ColorPicker recolour;
	@FXML
	private CheckBox routeType;

	private FXMLController controller;

	private Menu menu;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		type.itemsProperty()
				.set(FXCollections.observableArrayList((HashSet<String>) (NarrativeOperations.TYPES.clone())));
		values.setCellFactory(TextFieldListCell.forListView());

		BooleanBinding noSelect = values.getSelectionModel().selectedIndexProperty().lessThan(0);
		BooleanBinding typeBoolean = type.valueProperty().isEqualTo(Strings.TYPE_BOOLEAN);
		remove.disableProperty().bind(noSelect.or(typeBoolean));
		recolour.disableProperty().bind(noSelect);
		add.disableProperty().bind(typeBoolean);

		recolour.getStyleClass().add("button");
		recolour.valueProperty()
				.addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
					try {
						Color c = controller.getOperations().narrativeOperations().getColor(propName,
								values.getSelectionModel().getSelectedItem());
						if (!c.equals(newValue)) {
							controller.getOperations().doOp(controller.getOperations().generator().setColor(propName,
									values.getSelectionModel().getSelectedItem(), newValue, this));
						}
					} catch (Exception e) {
						controller.setInfo(e.getMessage());
					}
				});
		values.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
					if (newValue != null) {
						Color c;
						try {
							c = controller.getOperations().narrativeOperations().getColor(propName, newValue);
							recolour.valueProperty().set(c);
						} catch (Exception e) {
							controller.setInfo(e.getMessage());
						}
					}
				});
		values.setOnEditCommit(event -> {
			String oldValue = values.getItems().get(event.getIndex());
			String newValue = event.getNewValue();
			try {
				controller.getOperations().doOp(controller.getOperations().generator().renameValue(propName, typeName,
						oldValue, newValue, this));
				values.getItems().set(event.getIndex(), newValue);
				menu.getItems().get(event.getIndex()).setText(newValue);
				event.consume();
			} catch (Exception e) {
				e.printStackTrace();
				controller.setInfo(e.getMessage());
			}
		});

		name.textProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
					textChangeAction();
				});
		routeType.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					try {
						if (controller.getOperations().narrativeOperations().isRouteType(propName) != newValue) {
							controller.getOperations().doOp(
									controller.getOperations().generator().setRouteType(propName, newValue, routeType));
						}
					} catch (IllegalOperationException ioe) {
						controller.setInfo(ioe.getMessage());
					}
				});
		name.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
			if (event.getCharacter().equals("=")) {
				event.consume();
			}
		});
	}

	/**
	 * Loads info, called from ControllerOperations on creation
	 * 
	 * @param name
	 *            Name of property
	 * @param parent
	 *            FXMLController for parent application
	 * @param menu
	 *            the global context menu item for this property
	 */
	public void init(String name, FXMLController parent, Menu menu) {
		propName = name;
		controller = parent;
		this.name.setText(name);
		this.menu = menu;
	}

	/**
	 * When text is changed in the
	 */
	protected void textChangeAction() {
		String newName = name.getText();
		if (!newName.equals(propName)) {
			try {
				controller.getOperations()
						.doOp(controller.getOperations().generator().renameProperty(propName, newName, this));
			} catch (IllegalOperationException ioe) {
				controller.setInfo(ioe.getMessage());
				name.setText(propName);
			}
		}
	}

	/**
	 * Sets the name of this property
	 * 
	 * @param newName
	 */
	public void setName(String newName) {
		propName = newName;
		menu.setText(newName);
		name.setText(newName);
	}

	/**
	 * FXML hook deletes this property
	 */
	@FXML
	protected void deleteButtonAction(ActionEvent event) {
		try {
			controller.getOperations().doOp(controller.getOperations().generator().removeProperty(this));
		} catch (IllegalOperationException ioe) {
			controller.setInfo(ioe.getMessage());
		}
	}

	/**
	 * Adds a new value
	 * 
	 * @param s
	 *            Name of value to add
	 * @param index
	 *            the position to insert (useful for undo)
	 */
	public void addValue(String s, int index) {
		values.getItems().add(index, s);
		MenuItem item = new MenuItem(s);
		item.setOnAction(trigger -> {
			try {
				controller.assignProperty(propName, typeName, item.getText());
			} catch (Exception e) {
				controller.setInfo(e.getMessage());
			}
		});
		menu.getItems().add(index, item);
	}

	/**
	 * Removes a value
	 * 
	 * @param s
	 *            the value to remove
	 */
	public void removeValue(String s) {
		int index = values.getItems().indexOf(s);
		values.getItems().remove(s);
		menu.getItems().remove(index);
	}

	/**
	 * FXML hook adds a new value
	 */
	@FXML
	protected void addValueAction(ActionEvent event) {
		try {
			String s = controller.getOperations().narrativeOperations().getDefaultValue(propName, typeName);
			controller.getOperations().doOp(controller.getOperations().generator().addValue(propName, typeName, s,
					values.getItems().size(), this));

		} catch (IllegalOperationException e) {
			controller.setInfo(e.getMessage());
		}
	}

	/**
	 * FXML hook removes a value
	 */
	@FXML
	protected void removeValueAction(ActionEvent event) {
		String selected = values.getSelectionModel().getSelectedItem();
		try {
			controller.getOperations()
					.doOp(controller.getOperations().generator().removeValue(propName, typeName, selected, this));
		} catch (IllegalOperationException ioe) {
			controller.setInfo(ioe.getMessage());
		}
	}

	/**
	 * Gets the index of the given value (used by undo to know where to reinsert
	 * when undoing a deletion)
	 * 
	 * @param s
	 *            value to lookup
	 * @return Integer representing position in items, or -1 if not present
	 */
	public int getIndexOf(String s) {
		return values.getItems().indexOf(s);
	}

	@FXML
	protected void changeTypeAction(ActionEvent event) {
		try {
			if (!controller.getOperations().narrativeOperations().getPropertyType(propName).equals(type.getValue())) {
				controller.getOperations()
						.doOp(controller.getOperations().generator().setPropertyType(propName, type.getValue(), this));
			}
			typeName = type.getValue();
		} catch (IllegalOperationException e) {
			controller.setInfo(e.getMessage());
			type.setValue(typeName);
		}
	}

	/**
	 * Gets the menu object for this controller
	 * 
	 * @return the menu
	 */
	public Menu getMenu() {
		return menu;
	}

	/**
	 * Gets the root pane for this controller
	 * 
	 * @return the root pane
	 */
	public TitledPane getRoot() {
		return root;
	}

	/**
	 * Gets the current property name
	 * 
	 * @return
	 */
	public String getName() {
		return propName;
	}

	/**
	 * Recolours a property value
	 * 
	 * @param value
	 *            value to colour
	 * @param c
	 *            colour to set to
	 */
	public void recolour(String value, Color c) {
		if (values.getSelectionModel().getSelectedIndex() > -1
				&& values.getSelectionModel().getSelectedItem().equals(value)) {
			recolour.setValue(c);
		}
	}

	/**
	 * Changes the type
	 * 
	 * @param s
	 *            new type
	 */
	public void setType(String s) {
		Debug.logInfo("Set type of " + propName + " to " + s, 3, Debug.SYSTEM_GUI);
		typeName = s;
		type.setValue(s);
	}

	/**
	 * Gets the values in the frame
	 * 
	 * @return
	 */
	public ListView<String> getValues() {
		return values;
	}

	/**
	 * Sets/removes this as a route type
	 * 
	 * @param b
	 */
	public void setAsRouteType(boolean b) {
		routeType.setSelected(b);
	}
}
