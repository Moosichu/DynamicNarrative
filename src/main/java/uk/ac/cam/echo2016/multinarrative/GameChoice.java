package uk.ac.cam.echo2016.multinarrative;

import java.util.ArrayList;

/**
 * The object returned by {@code NarrativeInstance} when a {@code Route} is completed. The action 
 * stored by the object is determined by the {@code onEntry} function in {@code Node}, implemented
 * in both {@code SynchronizationNode} and {@code ChoiceNode}.
 * 
 * @see NarrativeInstance#endRoute
 * @see Node#onEntry
 * @see SynchronizationNode#onEntry
 * @see ChoiceNode#onEntry
 *  
 * @author tr393
 * @author rjm232
 * @version 1.0
 */
public class GameChoice { 

    public static final int ACTION_MAJOR_DECISION = 0;
    public static final int ACTION_CHOOSE_ROUTE = 1;
    public static final int ACTION_CONTINUE = 2;

    protected ArrayList<Route> options;
    protected ArrayList<Node> events;
    protected int action;

    public GameChoice(int actionName, ArrayList<Route> choices, ArrayList<Node> triggers) {
        action = actionName;
        options = choices;
        events = triggers;
    }

    public boolean hasEvent() {
        return ! events.isEmpty();
    }

    public ArrayList<Node> getEvents() {
        return events;
    }

    public int getAction() {
        return action;
    }

    public ArrayList<Route> getOptions() {
        return options;
    }

    public void completeEvents() {
        for(Node n:events){
            n.createProperties();
            n.getProperties().putBoolean("System.isCompleted", true);
        }
    }
}
