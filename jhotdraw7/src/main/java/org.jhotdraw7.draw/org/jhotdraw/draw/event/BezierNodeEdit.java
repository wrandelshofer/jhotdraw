/* @(#)BezierNodeEdit.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * An {@code UndoableEdit} event which can undo a change of a node in
 * a {@link BezierFigure}.
 *
 * @version $Id: BezierNodeEdit.java -1   $
 * @author Werner Randelshofer
 */
public class BezierNodeEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;

    private BezierFigure owner;
    private int index;
    private BezierPath.Node oldValue;
    private BezierPath.Node newValue;

    /** Creates a new instance. */
    public BezierNodeEdit(BezierFigure owner, int index, BezierPath.Node oldValue, BezierPath.Node newValue) {
        this.owner = owner;
        this.index = index;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String getPresentationName() {
        ResourceBundleUtil labels = DrawLabels.getLabels();
        if (oldValue.mask != newValue.mask) {
            return labels.getString("edit.bezierNode.changeType.text");
        } else {
            return labels.getString("edit.bezierNode.movePoint.text");
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.setNode(index, newValue);
        owner.changed();
        if (oldValue.mask != newValue.mask) {
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.setNode(index, oldValue);
        owner.changed();
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit instanceof BezierNodeEdit) {
            BezierNodeEdit that = (BezierNodeEdit) anEdit;
            if (that.owner == this.owner && that.index == this.index) {
                this.newValue = that.newValue;
                return true;
            }
        }
        return false;
    }
}