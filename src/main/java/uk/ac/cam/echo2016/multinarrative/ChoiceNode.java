package uk.ac.cam.echo2016.multinarrative;

/**
 * Represents a major decision made in a narrative that can affect which sync point a character will end up
 * in. Has one narrative entering, and several leaving.
 * 
 * @author tr393, rjm232
 *
 */
public class ChoiceNode extends Node { // TODO Todo's and documentation

	public ChoiceNode(String id) {
		super(id);
	}

	public ChoiceNode(Node node, NarrativeInstance instance) {
		super(node, instance);
	}

	public Node copy(NarrativeInstance instance) {
		return new ChoiceNode(this, instance);
	}

	public android.os.BaseBundle startNarrative(Narrative option) { //TODO Finish Impl
		return null;
	};

	public GameChoice onEntry(Narrative completed, NarrativeInstance instance) { //TODO Finish Impl
		return null;
	}

}
