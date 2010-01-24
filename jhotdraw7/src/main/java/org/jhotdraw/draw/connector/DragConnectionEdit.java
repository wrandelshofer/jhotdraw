/**
 *
 */
package org.jhotdraw.draw.connector;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.jhotdraw.draw.LineConnectionFigure;

/**
 * <p>
 * This class can undo/redo changes to a connection.
 * <p>
 * This is only to be used for connector dragging changes or connection line
 * dragging. It assumes that points on the connection have not been added or
 * deleted; the number of points is the same.
 * <p>
 * It also assumes that the cloned lines passed as parameters are not listening
 * to the connected figures and that these clones have not been added to a
 * drawing.
 *
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *
 */
public class DragConnectionEdit extends AbstractUndoableEdit {
    private ConnectorSubTracker connectorSubTracker;
    protected final LineConnectionFigure connection;
    protected LineConnectionFigure connectionCloneAfter;
    protected LineConnectionFigure connectionCloneBefore;
    private static final long serialVersionUID = 1L;


    /**
     * @param connectorSubTracker
     * @param connection
     * @param connectionCloneAfter
     *            this object's ConnectionHandler is not listening to it's
     *            start or end figures
     * @param connectionCloneBefore
     *            this object's ConnectionHandler is not listening to it's
     *            start or end figures
     */
    public DragConnectionEdit(ConnectorSubTracker connectorSubTracker, LineConnectionFigure connection,
            LineConnectionFigure connectionCloneAfter, LineConnectionFigure connectionCloneBefore) {
        this.connectorSubTracker = connectorSubTracker;
        this.connection = connection;
        this.connectionCloneAfter = connectionCloneAfter;
        this.connectionCloneBefore = connectionCloneBefore;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.undo.AbstractUndoableEdit#getPresentationName()
     */
    @Override
    public String getPresentationName() {
        return "DragConnection Change";
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.undo.AbstractUndoableEdit#redo()
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        update(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.undo.AbstractUndoableEdit#undo()
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        update(true);
    }

    /**

     * @param undo
     */
    protected void update(boolean undo) {
        LineConnectionFigure fromConnection = null;
        if (undo)
            fromConnection = connectionCloneBefore;
        else
            fromConnection = connectionCloneAfter;

        connection.willChange();
        for (int i = 1; i < fromConnection.getNodeCount() - 2; i++) {
            connection.setPoint(i, fromConnection.getPoint(i));
        }
        connection.setStartConnector(fromConnection.getStartConnector());
        connection.setEndConnector(fromConnection.getEndConnector());
        connection.updateConnection();
        connection.changed();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.undo.AbstractUndoableEdit#addEdit(javax.swing.undo.UndoableEdit
     * )
     */
    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit instanceof DragConnectionEdit) {
            DragConnectionEdit that = (DragConnectionEdit) anEdit;
            if (that.connection == this.connection) {
                this.connectionCloneAfter = that.connectionCloneAfter;
                that.die();
                return true;
            }
        }
        return false;
    }
}