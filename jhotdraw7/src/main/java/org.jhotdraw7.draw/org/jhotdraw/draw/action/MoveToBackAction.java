/*
 * @(#)MoveToBackAction.java  1.0  24. November 2003
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

/**
 * MoveToBackAction.
 *
 * @author  Werner Randelshofer
 * @version 1.0 24. November 2003  Created.
 */
public class MoveToBackAction extends AbstractSelectedAction {
       private ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    
    /** Creates a new instance. */
    public MoveToBackAction(DrawingEditor editor) {
        super(editor);
        labels.configureAction(this, "moveToBack");
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        final DrawingView view = getView();
        final LinkedList<Figure> figures = new LinkedList<Figure>(view.getSelectedFigures());
        sendToBack(view, figures);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            public String getPresentationName() {
       return labels.getString("moveToBack");
            }
            public void redo() throws CannotRedoException {
                super.redo();
                MoveToBackAction.sendToBack(view, figures);
            }
            public void undo() throws CannotUndoException {
                super.undo();
                MoveToFrontAction.bringToFront(view, figures);
            }
        }
        );
    }
    public static void sendToBack(DrawingView view, Collection figures) {
        Iterator i = figures.iterator();
        Drawing drawing = view.getDrawing();
        while (i.hasNext()) {
            Figure figure = (Figure) i.next();
            drawing.sendToBack(figure);
        }
    }
}
