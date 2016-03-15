package uk.ac.cam.echo2016.multinarrative.gui.operations;

import java.util.ArrayList;

import uk.ac.cam.echo2016.multinarrative.dev.Debug;
/**
 * Class to do and undo a bunch of things at once.
 * @author jr650
 *
 */
public class CompositeOperation implements Operation {

    private ArrayList<OperationGenerator> gens;
    private ArrayList<Operation> items;

    /**
     * Creates new composite operation from list of operations
     * @param gens generators for operations
     */
    public CompositeOperation(ArrayList<OperationGenerator> gens) {
        this.gens = gens; 
    }

    @Override
    public void execute() throws IllegalOperationException {
        if (items == null) {
            items = new ArrayList<>();
            for (OperationGenerator gen : gens) {
                Operation op = gen.generate();
                items.add(op);
                Debug.logInfo("Doing " + op.getClass().getSimpleName(), 3, Debug.SYSTEM_GUI);
                op.execute();
            }
        } else {
            for (Operation op : items) {
                Debug.logInfo("Doing " + op.getClass().getSimpleName(), 3, Debug.SYSTEM_GUI);
                op.execute();
            }
        }
    }

    @Override
    public void undo() throws IllegalOperationException {
        for (int i = items.size() - 1; i >= 0; i--) {
            Debug.logInfo("Undoing " + items.get(i).getClass().getSimpleName(), 3, Debug.SYSTEM_GUI);
            items.get(i).undo();
        }
    }

}
