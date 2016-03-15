package uk.ac.cam.echo2016.multinarrative;

/**
 * This is thrown if an identifier for an expected {@code Route} or {@code Node}
 * is not found in the {@code MultiNarrative} being searched.
 * 
 * @author rjm232
 * @author tr393
 *
 */

public class GraphElementNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private String item;

    public GraphElementNotFoundException(String s) {
        super(s + " not found");
        item = s;
    }

    public String getItem() {
        return item;
    }

}
