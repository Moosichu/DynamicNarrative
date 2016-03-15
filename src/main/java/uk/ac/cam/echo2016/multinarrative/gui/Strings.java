package uk.ac.cam.echo2016.multinarrative.gui;

/**
 * Class for constant strings
 * 
 * @author jr650, eyx20
 */
public class Strings {

	public static final String PROPERTY_ADDED = "Added Property: %1";
	public static final String PROPERTY_REMOVED = "Removed Property: %1";
	public static final String PROPERTY_VALUE_STRING = "Value%1";
	public static final String PROPERTY_VALUE_NUM = "%1";

	public static final String SYSTEM_PROPERTY = "%1 is a System property";
	public static final String ADD_EMPTY_STRING = "Please type a property name";
	public static final String ALREADY_EXISTS = "Property already exists";
	public static final String PROPERTY_DOES_NOT_EXIST = "Property does not exist";
	public static final String INVALID_TYPE = "Type cannot be resolved.";
	public static final String ITEM_DOES_NOT_EXIST = "%1 does not exist";
	public static final String ITEM_ALREADY_EXISTS = "%1 already exists";
	public static final String COULD_NOT_RENAME = "Couldn't rename %1";
	public static final String CYCLE_EXISTS = "Operation would cause a cycle";
	public static final String CANNOT_FORMAT = "%1 is not a %2";
	public static final String NON_UNIQUE_START = "Could not find a unique start point";
	public static final String CANNOT_EDIT_BOOLEAN = "Boolean types cannot be edited";

	public static final String ROUTE_PREFIX = "Route-%1";
	public static final String NODE_PREFIX = "Node-%1";

	public static final String CANNOT_UNDO = "Nothing to undo";
	public static final String CANNOT_REDO = "Nothing to redo";

	public static final String GUI_X = "GUI.X";
	public static final String GUI_Y = "GUI.Y";

	public static final String TYPE_STRING = "String";
	public static final String TYPE_INTEGER = "Integer";
	public static final String TYPE_BOOLEAN = "Boolean";
	public static final String TYPE_BYTE = "Byte";
	public static final String TYPE_SHORT = "Short";
	public static final String TYPE_LONG = "Long";
	public static final String TYPE_FLOAT = "Float";
	public static final String TYPE_DOUBLE = "Double";

	public static final String ABOUT_TEXT = "Graphical User Interface for DyNaMo (Dynamic Narrative Modelling)\n\nDemonstration version.";

	private Strings() {
	}

	/**
	 * Populates occurrences of %1, %2 %3 etc. a string with arguments
	 * @param template template string
	 * @param values values to use
	 * @return
	 */
	public static String populateString(String template, Object... values) {
		String r = template;
		for (int i = 0; i < values.length; i++) {
			r = r.replace((CharSequence) ("%" + (i + 1)), values[i].toString());
		}
		return r;
	}

	/**
	 * Populates occurrences of %1, %2 %3 etc. a string with arguments
	 * @param template template string
	 * @param values values to use
	 * @return
	 */
	public static String populateString(String template, String... values) {
		String r = template;
		for (int i = 0; i < values.length; i++) {
			r = r.replace((CharSequence) ("%" + (i + 1)), values[i]);
		}
		return r;
	}
}
