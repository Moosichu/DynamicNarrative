package uk.ac.cam.echo2016.multinarrative;

/**
 * 
 * @author Rjmcf
 *
 */

public class InvalidGraphException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidGraphException() {
        super();
    }

    public InvalidGraphException(String message) {
        super(message);
    }

    public InvalidGraphException(Throwable cause) {
        super(cause);
    }

    public InvalidGraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
