package uk.ac.cam.echo2016.multinarrative;

import java.io.Serializable;

import android.os.BaseBundle;

/**
 * 
 * Acts as a connection between nodes. Represents the paths that characters
 * follow.
 * 
 * @author tr393
 * @author eyx20
 * @author rjm232
 * @author jr650
 * @version 1.0
 *
 */
public class Route implements Serializable, Cloneable { // TODO Documentation
    private static final long serialVersionUID = 1;
    private final String id;
    private Node start;
    private Node end;
    private BaseBundle properties;

    public Route(String id, Node start, Node end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public void setup() {
        start.getExiting().add(this);
        end.getEntering().add(this);
    }

    @Override
    public Route clone() {
        try {
            Route clone = (Route) super.clone();
            clone.properties = BaseBundle.deepcopy(this.properties);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        return id;
    }

    public Node getStart() {
        return start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public Node getEnd() {
        return end;
    }

    public void setEnd(Node end) {
        this.end = end;
    }

    public void createProperties() {
        if (properties == null)
            properties = new BaseBundle(4);
    }

    public void assignProperties(BaseBundle props) {
        properties = props;
    }

    public BaseBundle getProperties() {
        return properties;
    }

    public void setProperties(BaseBundle b) {
        properties = b;
    }
    
    @Override
    public String toString() {
        return id;
    }
}
