package uk.ac.cam.echo2016.multinarrative;

public class NonUniqueStartException extends Exception {
    private static final long serialVersionUID = 1L;

    public NonUniqueStartException() {
        super();
    }

    public NonUniqueStartException(String message) {
        super(message);
    }
}
