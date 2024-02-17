/* @(#)AttributeToggler.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

import org.jhotdraw.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * AttributeToggler toggles an attribute of the selected figures between two
 * different values.
 * If the name of a compatible JTextComponent action is specified, the toggler
 * checks if the current permant focus owner is a JTextComponent, and if it is,
 * it will apply the text action to the JTextComponent.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class AttributeToggler<T> extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private DrawingEditor editor;
    private AttributeKey<T> key;
    private T value1;
    private T value2;
    @Nullable private Action compatibleTextAction;
    
    /** Creates a new instance. */
    public AttributeToggler(DrawingEditor editor, AttributeKey<T> key, T value1, T value2) {
        this(editor, key, value1, value2, null);
    }
    public AttributeToggler(DrawingEditor editor, AttributeKey<T> key, T value1, T value2, @Nullable  Action compatibleTextAction) {
        this.editor = editor;
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
        this.compatibleTextAction = compatibleTextAction;
    }
    
    public DrawingView getView() {
        return editor.getActiveView();
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (compatibleTextAction != null) {
            Component focusOwner = KeyboardFocusManager.
                    getCurrentKeyboardFocusManager().
                    getPermanentFocusOwner();
            if (focusOwner != null && focusOwner instanceof JTextComponent) {
                compatibleTextAction.actionPerformed(evt);
                return;
            }
        }
        
        // Determine the new value
        Iterator<Figure> i = getView().getSelectedFigures().iterator();
        T toggleValue = value1;
        if (i.hasNext()) {
            Figure f = i.next();
            Object attr = f.get(key);
            if (value1 == null && attr == null ||
                    (value1 != null && attr != null && attr.equals(value1))) {
                toggleValue = value2;
            }
        }
        final T newValue = toggleValue;
        
        //--
        final ArrayList<Figure> selectedFigures = new ArrayList<Figure>(getView().getSelectedFigures());
        final ArrayList<Object> restoreData = new ArrayList<Object>(selectedFigures.size());
        for (Figure figure : selectedFigures) {
            restoreData.add(figure.getAttributesRestoreData());
            figure.willChange();
            figure.set(key, newValue);
            figure.changed();
        }
        UndoableEdit edit = new AbstractUndoableEdit() {
    private static final long serialVersionUID = 1L;
            @Override
            public String getPresentationName() {
                String name = (String) getValue(ActionUtil.UNDO_PRESENTATION_NAME_KEY);
                if (name == null) {
                    name = (String) getValue(AbstractAction.NAME);
                }
                if (name == null) {
                    ResourceBundleUtil labels = DrawLabels.getLabels();
                    name = labels.getString("attribute.text");
                }
                return name;
            }
            @Override
            public void undo() {
                super.undo();
                Iterator<Object> iRestore = restoreData.iterator();
                for (Figure figure : selectedFigures) {
                    figure.willChange();
                    figure.restoreAttributesTo(iRestore.next());
                    figure.changed();
                }
            }
            @Override
            public void redo() {
                super.redo();
                for (Figure figure : selectedFigures) {
                    //restoreData.add(figure.getAttributesRestoreData());
                    figure.willChange();
                    figure.set(key, newValue);
                    figure.changed();
                }
            }
        };
        getView().getDrawing().fireUndoableEditHappened(edit);
    }
}
