package uk.ac.cam.echo2016.multinarrative.gui.operations;

/**
 * 
 * The Operation class is used to implement most functions in OperationsManager. The
 * reason for this is that anything that affects the graph, must be 'undoable',
 * and this provides a clean interface for enabling that functionality.
 * 
 * It is an implementation of the "Command" design pattern.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Command_pattern">Wikipedia:
 *      Command pattern</a>
 * 
 * @author tr395
 *
 */
public interface Operation {

    public void execute() throws IllegalOperationException;

    public void undo() throws IllegalOperationException;
}
