package uk.ac.cam.echo2016.multinarrative;

/**
 * 
 * @author tr393, eyx20
 *
 */


public class GUINarrative extends EditableNarrative { //TODO Todo's and documentation
    
    private Narrative narrative;

	public void newNarrative(String id, String start, String end) {
		Node startNode = getNode(start);
		Node endNode = getNode(end);
	    this.narrative = new Narrative(id, startNode, endNode);
//	    narrative.setStart(new SynchronizationNode(start));
//	    narrative.setEnd(new SynchronizationNode(end));
	    return;
	    }
	
	public void newSynchronizationNode(String id) {return;}
	public android.os.BaseBundle getProperties (String id) {return null;}
	public void insertChoicePoint(String id) {return;}
	public void setStartPoint(String id) {return;}
}
