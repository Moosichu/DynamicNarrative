package uk.ac.cam.echo2016.multinarrative.gui;

/**
 * 
 * @author rjm232
 *
 */

public class UserErrorException extends Exception {

    private static final long serialVersionUID = -6531021887839054430L;

    public UserErrorException() {
        super();
    }

    public UserErrorException(String message) {
        super(message);
    }

}
