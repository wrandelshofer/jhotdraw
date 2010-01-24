/*
 * @(#)TransformEdit.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */


package org.jhotdraw.draw.event;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.ConnectorSubTracker;
import org.jhotdraw.util.*;
import javax.swing.undo.*;
import java.awt.geom.*;
import java.util.*;

/**
 * An {@code UndoableEdit} event which can undo a lossless transform of
 * {@link Figure}s by applying the inverse of the transform to the figures.
 * <p>
 * This object is useful for undoing lossless transformations, such as the
 * translation of figures.
 * <p>
 * If a lossy transforms is performed, such as rotation, scaling or shearing,
 * then undos should be performed with {@link TransformRestoreEdit} instead.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TransformEdit extends AbstractUndoableEdit {
    private Collection<Figure> figures;
    private AffineTransform tx;
    private Collection<Connector> oldConnectors;
    private DrawingView view;


    /**
     * @param figure
     * @param tx
     * @param view
     */
    public TransformEdit(Figure figure, AffineTransform tx, DrawingView view) {
        figures = new LinkedList<Figure>();
        ((LinkedList<Figure>) figures).add(figure);
        this.tx = (AffineTransform) tx.clone();
        this.view = view;
        this.oldConnectors = null;
    }

    /**
     * when sliding connectors
     *
     * @param figures
     * @param tx
     * @param view
     * @param oldConnectors
     */
    public TransformEdit(Collection<Figure> figures, AffineTransform tx,
            DrawingView view, Collection<Connector> oldConnectors) {
        this.figures = figures;
        this.tx = (AffineTransform) tx.clone();
        this.view = view;
        this.oldConnectors = oldConnectors;
    }

    /**
     * @param figures
     * @param tx
     * @param view
     */
    public TransformEdit(Collection<Figure> figures, AffineTransform tx, DrawingView view) {
        this.figures = figures;
        this.tx = (AffineTransform) tx.clone();
        this.view = view;
        this.oldConnectors = null;
    }
    public String getPresentationName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        return labels.getString("edit.transform.text");
    }

    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit instanceof TransformEdit) {
            TransformEdit that = (TransformEdit) anEdit;
            if (that.figures == this.figures) {
                this.tx.concatenate(that.tx);
                that.die();
                return true;
            }
        }
        return false;
    }
    public boolean replaceEdit(UndoableEdit anEdit) {
        if (anEdit instanceof TransformEdit) {
            TransformEdit that = (TransformEdit) anEdit;
            if (that.figures == this.figures) {
                this.tx.preConcatenate(that.tx);
                that.die();
                return true;
            }
        }
        return false;
    }

    public void redo() throws CannotRedoException {
        super.redo();
        ConnectorSubTracker connectorSubTracker = view.getEditor().getConnectorSubTracker();
        connectorSubTracker.adjustConnectorsForMoving(figures, ConnectorSubTracker.trackStart, 0);
        for (Figure f : figures) {
            f.willChange();
            f.transform(tx);
            f.changed();

        }
        connectorSubTracker.adjustConnectorsForMoving(figures, ConnectorSubTracker.trackStep, 0);
        connectorSubTracker.adjustConnectorsForMoving(figures, ConnectorSubTracker.trackEnd, 0);
    }


    /* (non-Javadoc)
     * @see javax.swing.undo.AbstractUndoableEdit#undo()
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        // oldConnectors is not null when sliding connections(ALT is pressed)
        boolean restoringConnectors = (oldConnectors == null) ? false : true;
        try {
            ConnectorSubTracker connectorSubTracker = view.getEditor().getConnectorSubTracker();
            if (!restoringConnectors)
                connectorSubTracker.adjustConnectorsForMoving(figures, ConnectorSubTracker.trackStart, 0);

            AffineTransform inverse = tx.createInverse();
            for (Figure f : figures) {
                f.willChange();
                f.transform(inverse);
                f.changed();
            }
            if (!restoringConnectors) {
                connectorSubTracker.adjustConnectorsForMoving(figures, ConnectorSubTracker.trackStep, 0);
                connectorSubTracker.adjustConnectorsForMoving(figures, ConnectorSubTracker.trackEnd, 0);
                for (Figure f : figures) {
                    if (f instanceof LineConnectionFigure) {
                        LineConnectionFigure conn = (LineConnectionFigure)f;
                        if (conn.getNodeCount() > 2) {
                            connectorSubTracker.touchConnector(conn.getStartConnector());
                            connectorSubTracker.touchConnector(conn.getEndConnector());
                        }
                    }
                }
            }
            else {
                //restore connectors AFTER restoring transform
                connectorSubTracker.restoreConnectors(figures, oldConnectors);
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }
    public String toString() {
        return getClass().getName()+'@'+hashCode()+" tx:"+tx;
    }
}
