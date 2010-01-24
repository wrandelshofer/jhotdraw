/*
 * @(#)TransformRestoreEdit.java
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
import org.jhotdraw.draw.connector.RelativeConnector;

import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
/**
 * An {@code UndoableEdit} event which can undo a lossy transform of a single
 * {@link Figure} by restoring the figure using its transform restore data.
 * <p>
 * This object is useful for undoing lossy transformations, such as the
 * rotation, scaling or shearing of a figure.
 * <p>
 * The transform restore data may consume a lot of memory. Undos of lossless
 * transforms, such as translations of a figure, should use {@link TransformEdit}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TransformRestoreEdit extends AbstractUndoableEdit {
    private Figure owner;
    private Object oldTransformRestoreData;
    private Object newTransformRestoreData;
    private Collection<Connector> oldConnectors;
    private DrawingView view;


    /**
     * This constructor is used in SVGImageFigure.
     * <p>
     * There will be no undoing/redoing of connectors (using
     * ConnectorSubTracker) for this sample
     *
     * @param owner
     * @param oldTransformRestoreData
     * @param newTransformRestoreData
     */
    public TransformRestoreEdit(Figure owner, Object oldTransformRestoreData,
            Object newTransformRestoreData) {
        this(owner, oldTransformRestoreData, newTransformRestoreData, null);
    }
    /**
     * @param owner
     * @param oldTransformRestoreData
     * @param newTransformRestoreData
     * @param view
     */
    public TransformRestoreEdit(Figure owner, Object oldTransformRestoreData,
            Object newTransformRestoreData, DrawingView view) {
        this(owner, oldTransformRestoreData, newTransformRestoreData, view, null);
    }


    /**
     * @param owner
     * @param oldTransformRestoreData
     * @param newTransformRestoreData
     * @param oldConnectors
     * @param view
     */
    public TransformRestoreEdit(Figure owner, Object oldTransformRestoreData,
            Object newTransformRestoreData, DrawingView view, Collection<Connector> oldConnectors) {
        this.owner = owner;
        this.oldTransformRestoreData = oldTransformRestoreData;
        this.newTransformRestoreData = newTransformRestoreData;
        this.view = view;
        this.oldConnectors = oldConnectors;
    }

    public String getPresentationName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        return labels.getString("edit.transform.text");
    }

    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.restoreTransformTo(oldTransformRestoreData);
        //restore connectors AFTER restoring transform
        if (oldConnectors != null && view != null) {
            ConnectorSubTracker connectorSubTracker = view.getEditor().getConnectorSubTracker();
            connectorSubTracker.restoreConnectors(owner, oldConnectors);
        }
        owner.changed();
    }

    public void redo() throws CannotRedoException {
        super.redo();
        //view is null in SVG samples for SVGImageFigure ....
        if (view == null) {
            owner.willChange();
            owner.restoreTransformTo(newTransformRestoreData);
            owner.changed();
        }
        else {
            ConnectorSubTracker connectorSubTracker = view.getEditor().getConnectorSubTracker();
            ArrayList<Figure> figures = new ArrayList<Figure>();
            figures.add(owner);
            connectorSubTracker.adjustConnectorsForResizingRestoring(figures, ConnectorSubTracker.trackStart, 0);
            owner.willChange();
            owner.restoreTransformTo(newTransformRestoreData);
            connectorSubTracker.adjustConnectorsForResizingRestoring(figures, ConnectorSubTracker.trackStep, 0);
            connectorSubTracker.adjustConnectorsForResizingRestoring(figures, ConnectorSubTracker.trackEnd, 0);
            owner.changed();
        }
    }

}
