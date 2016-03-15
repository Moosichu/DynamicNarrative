package uk.ac.cam.echo2016.multinarrative;

import java.util.ArrayList;

import android.os.BaseBundle;

/**
 * Represents a {@code MultiNarrative} that can be modified. Sub-classing this allows alternate ways of generating
 * templates that do not use {@code GUINarrative}, while retaining implementations of basic operations.
 * 
 * @author tr393
 * @author rjm232
 * @version 1.0
 * @see MultiNarrative
 * @see GUINarrative
 *
 */

public abstract class EditableNarrative extends MultiNarrative { // TODO Finish Class Documentation + Method Documentation
    private static final long serialVersionUID = 1;
    public void addRoute(Route route) {
        routes.put(route.getId(), route);
    }
    
    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }
    
    public boolean removeRoute(String id) { 
        Route route = routes.remove(id);
        if (route == null)
            return false;
        // Should not return null else graph is broken
        route.getStart().getExiting().remove(route);
        route.getEnd().getEntering().remove(route);
        
        return true;
    }
    
    public boolean removeNode(String id) {
        Node node = nodes.remove(id);
        if (node == null) 
            return false;

        for (Route route : new ArrayList<Route> (node.getEntering())) {
            removeRoute(route.getId());
        }
        for (Route route : new ArrayList<Route> (node.getExiting())) {
            removeRoute(route.getId());
        }

        return true;		
    }
    
    public boolean renameRoute(String id, String newName) { 
        Route route = routes.remove(id);
        if (route == null)
            return false;
        
        Route newRoute = new Route(newName, route.getStart(), route.getEnd());
        
        // Update references of nodes at either end
        // Note setup must be called afterwards in case of the ids being the same
        newRoute.getStart().getExiting().remove(route);
        newRoute.getEnd().getEntering().remove(route);
        newRoute.setup();
        
        
        if (route.getProperties() != null)
            newRoute.setProperties(new BaseBundle(route.getProperties()));
        
        routes.put(newName, newRoute);
        return true;
    }
    
    public boolean renameNode(String id, String newName) {
        Node node = nodes.remove(id);
        if (node == null) 
            return false;
        
        Node newNode = node.create(newName);
        if (node.getProperties() != null) 
            newNode.setProperties(new BaseBundle(node.getProperties()));
        
        // Update references to the node
        for (Route route : node.getExiting()) {
            route.setStart(newNode);
        }
        for (Route route : node.getEntering()) {
            route.setEnd(newNode);
        }

        // Assign the nodes references to routes
        for (Route route : node.getExiting()) {
            newNode.getExiting().add(route);
        }
        for (Route route : node.getEntering()) {
            newNode.getEntering().add(route);
        }
        
        nodes.put(newName, newNode);
        return true;
    }
}
