package uk.ac.cam.echo2016.multinarrative;

import java.util.ArrayList;

import android.os.BaseBundle;

/**
 * Implements a {@link Node} at a branch point on the {@code MultiNarrative}
 * structure. At this point, some decision in the game affects the route taken
 * down the graph. Only one {@code Route} should be entering this node, as
 * opposed to {@link SyncronizationNode}.
 * 
 * @author tr393
 * @author rjm232
 * @version 1.0
 * @see Node
 * @see SyncronizationNode
 * @see MultiNarrative
 */
public class ChoiceNode extends Node { // TODO Documentation
    private static final long serialVersionUID = 1;

    public ChoiceNode(String id) {
        super(id);
    }

    protected Node create(String id) {
        return new ChoiceNode(id);
    }

    @Override
    public BaseBundle startRoute(Route option, NarrativeInstance instance) {
        for (Route deadRoute : new ArrayList<>(getExiting())) {
            if (deadRoute != option) {
                instance.kill(deadRoute);
            }
        }
        instance.complete(this);
        return option.getProperties();
    };

    @Override
    public GameChoice onEntry(Route completed, NarrativeInstance instance) throws GraphElementNotFoundException {
        if (!this.getEntering().contains(completed)) {
            throw new GraphElementNotFoundException(completed.getId());
        }
        GameChoice gameChoice = new GameChoice(GameChoice.ACTION_MAJOR_DECISION, getExiting(),
                instance.getEventfulNodes());

        return gameChoice;
    }
}
