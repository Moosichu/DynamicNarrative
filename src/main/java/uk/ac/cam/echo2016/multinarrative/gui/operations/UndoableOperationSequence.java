package uk.ac.cam.echo2016.multinarrative.gui.operations;

import java.util.ArrayDeque;
import java.util.Deque;

import uk.ac.cam.echo2016.multinarrative.dev.Debug;
import uk.ac.cam.echo2016.multinarrative.gui.Strings;

/**
 * A sequence of operation that support undo/redo
 * @author jr650
 */
public class UndoableOperationSequence {

    public static final int UNDO_LIMIT = 64;
    public static final int REDO_LIMIT = 16;

    private int undoLimit;
    private int redoLimit;

    private boolean isUndoing = false;

    private Deque<Operation> undoHistory;
    private Deque<Operation> redoHistory;

    public UndoableOperationSequence() {
        this(UNDO_LIMIT, REDO_LIMIT);
    }

    public UndoableOperationSequence(int undoLimit, int redoLimit) {
        undoHistory = new ArrayDeque<Operation>(undoLimit);
        redoHistory = new ArrayDeque<Operation>(redoLimit);
        this.undoLimit = undoLimit;
        this.redoLimit = redoLimit;
    }

    /**
     * Executes a command and stores it on the undoHistory Stack.
     * 
     * If an exception is thrown by the command, it won't be added to the
     * history.
     * 
     * Furthermore the redoHistory is flushed.
     * 
     * @param c
     *            Operation that you wish to execute
     * @throws IllegalOperationException
     */
    public void storeAndExecute(Operation c) throws IllegalOperationException {
        if (isUndoing)
            return;
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        Debug.logInfo("Doing " + c.getClass().getSimpleName() + " at request of " + stackTrace.getFileName() + ":"
                + stackTrace.getLineNumber(), 3, Debug.SYSTEM_GUI);
        c.execute();
        if (undoHistory.size() >= undoLimit)
            undoHistory.removeLast();
        undoHistory.addFirst(c);
        redoHistory.clear();
    }

    /**
     * Undo the last command.
     */
    public void undoLastOperation() throws IllegalOperationException {
        isUndoing = true;
        if (undoHistory.isEmpty()) {
            throw new IllegalOperationException(Strings.CANNOT_UNDO);
        }
        Operation c = undoHistory.peekFirst();
        Debug.logInfo("Undoing: " + c.getClass().getSimpleName(), 3, Debug.SYSTEM_GUI);
        c.undo();
        undoHistory.removeFirst();
        if (redoHistory.size() >= redoLimit)
            redoHistory.removeLast();
        redoHistory.addFirst(c);
        isUndoing = false;
    }

    /**
     * Redo the last undo. The exception should never really be thrown as only
     * successful commands should be able to reach this point.
     * 
     * @throws IllegalOperationException
     */
    public void redoLastUndo() throws IllegalOperationException {
        isUndoing = true;
        if (redoHistory.isEmpty()) {
            throw new IllegalOperationException(Strings.CANNOT_REDO);
        }
        Operation c = redoHistory.peekFirst();
        Debug.logInfo("Redoing: " + c.getClass().getSimpleName(), 3, Debug.SYSTEM_GUI);
        c.execute();
        redoHistory.removeFirst();
        undoHistory.addFirst(c);
        isUndoing = false;
    }

    public void clearHistory() {
        undoHistory.clear();
        redoHistory.clear();
    }

}
