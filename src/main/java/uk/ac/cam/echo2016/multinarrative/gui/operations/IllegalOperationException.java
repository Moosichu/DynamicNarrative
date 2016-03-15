package uk.ac.cam.echo2016.multinarrative.gui.operations;

import uk.ac.cam.echo2016.multinarrative.gui.Strings;

/**
 * Exception type for when gui operations fail. Should always have user readable
 * error messages as will be shown to the user.
 * 
 * @author jr650
 */
public class IllegalOperationException extends Exception {

	private static final long serialVersionUID = 1L;

	public IllegalOperationException(String template, String... values) {
		super(Strings.populateString(template, values));
	}

	public IllegalOperationException(String template, Object... values) {
		super(Strings.populateString(template, values));
	}

	public IllegalOperationException(Throwable cause, String template, String... values) {
		super(Strings.populateString(template, values), cause);
	}

	public IllegalOperationException(Throwable cause, String template, Object... values) {
		super(Strings.populateString(template, values), cause);
	}

}
