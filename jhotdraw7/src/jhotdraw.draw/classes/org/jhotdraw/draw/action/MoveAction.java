/* @(#)MoveAction.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.undo.CompositeEdit;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.ResourceBundle;

import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Moves the selected figures by one unit.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public abstract class MoveAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    private int dx, dy;

    /** Creates a new instance. */
    public MoveAction(DrawingEditor editor, int dx, int dy) {
        super(editor);
        this.dx = dx;
        this.dy = dy;
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        CompositeEdit edit;
        AffineTransform tx = new AffineTransform();
        tx.translate(dx, dy);

        HashSet<Figure> transformedFigures = new HashSet<Figure>();
        for (Figure f : getView().getSelectedFigures()) {
            if (f.isTransformable()) {
                transformedFigures.add(f);
                f.willChange();
                f.transform(tx);
                f.changed();
            }
        }
        fireUndoableEditHappened(new TransformEdit(transformedFigures, tx));

    }

    public static class East extends MoveAction {
    private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveEast";

        public East(DrawingEditor editor) {
            super(editor, 1, 0);
            ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
            labels.configureAction(this, ID);
        }
    }

    public static class West extends MoveAction {
    private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveWest";

        public West(DrawingEditor editor) {
            super(editor, -1, 0);
            ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
            labels.configureAction(this, ID);
        }
    }

    public static class North extends MoveAction {
    private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveNorth";

        public North(DrawingEditor editor) {
            super(editor, 0, -1);
            ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
            labels.configureAction(this, ID);
        }
    }

    public static class South extends MoveAction {
    private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveSouth";

        public South(DrawingEditor editor) {
            super(editor, 0, 1);
            ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
            labels.configureAction(this, ID);
        }
    }
}