/* @(#)DrawingColorChooserHandler.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;

import javax.swing.JColorChooser;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.awt.Color;

/**
 * DrawingColorChooserHandler.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingColorChooserHandler extends AbstractDrawingViewAction
        implements ChangeListener {
    private static final long serialVersionUID = 1L;

    protected AttributeKey<Color> key;
    protected JColorChooser colorChooser;
    protected JPopupMenu popupMenu;
    protected int isUpdating;
    //protected Map<AttributeKey, Object> attributes;

    /** Creates a new instance. */
    public DrawingColorChooserHandler(DrawingEditor editor, AttributeKey<Color> key, JColorChooser colorChooser, JPopupMenu popupMenu) {
        super(editor);
        this.key = key;
        this.colorChooser = colorChooser;
        this.popupMenu = popupMenu;

        //colorChooser.addActionListener(this);
        colorChooser.getSelectionModel().addChangeListener(this);
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        /*
        if (evt.getActionCommand() == JColorChooser.APPROVE_SELECTION) {
        applySelectedColorToFigures();
        } else if (evt.getActionCommand() == JColorChooser.CANCEL_SELECTION) {
        }*/
        popupMenu.setVisible(false);
    }

    protected void applySelectedColorToFigures() {
        final Drawing drawing = getView().getDrawing();

        Color selectedColor = colorChooser.getColor();
        if (selectedColor != null && selectedColor.getAlpha() == 0) {
            selectedColor = null;
        }

        final Object restoreData = drawing.getAttributesRestoreData();
        drawing.willChange();
        drawing.set(key, selectedColor);
        drawing.changed();

        getEditor().setDefaultAttribute(key, selectedColor);
        final Color undoValue = selectedColor;
        UndoableEdit edit = new AbstractUndoableEdit() {
    private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                return AttributeKeys.FONT_FACE.getPresentationName();
                /*
                String name = (String) getValue(Actions.UNDO_PRESENTATION_NAME_KEY);
                if (name == null) {
                name = (String) getValue(AbstractAction.NAME);
                }
                if (name == null) {
                ResourceBundleUtil labels = GuiLabels.getLabels();
                name = labels.getString("attribute.text");
                }
                return name;*/
            }

            @Override
            public void undo() {
                super.undo();
                drawing.willChange();
                drawing.restoreAttributesTo(restoreData);
                drawing.changed();
            }

            @Override
            public void redo() {
                super.redo();
                // restoreData.add(figure.getAttributesRestoreData());
                drawing.willChange();
                drawing.set(key, undoValue);
                drawing.changed();
            }
        };
        fireUndoableEditHappened(edit);
    }

    @Override
    protected void updateEnabledState() {
        setEnabled(getEditor() != null && getEditor().isEnabled());
        if (getView() != null && colorChooser != null && popupMenu != null) {
            colorChooser.setEnabled(getView().getSelectionCount() > 0);
            popupMenu.setEnabled(getView().getSelectionCount() > 0);
            isUpdating++;

            Color drawingColor = getView().getDrawing().get(key);
            colorChooser.setColor(drawingColor == null ? new Color(0, true) : drawingColor);

            isUpdating--;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (isUpdating++ == 0) {
            applySelectedColorToFigures();
        }
        isUpdating--;
    }
}
