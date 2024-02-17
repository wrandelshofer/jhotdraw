/* @(#)DefaultAttributeAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.undo.CompositeEdit;
import org.jhotdraw.util.ResourceBundleUtil;

import org.jhotdraw.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * DefaultAttributeAction.
 * <p>
 * XXX - should listen to changes in the default attributes of its DrawingEditor.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class DefaultAttributeAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    private AttributeKey<?>[] keys;
    @Nullable
    private Map<AttributeKey<?>, Object> fixedAttributes;

    /** Creates a new instance. */
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey<?> key) {
        this(editor, key, null, null);
    }

    public DefaultAttributeAction(DrawingEditor editor, AttributeKey<?> key, @Nullable Map<AttributeKey<?>, Object> fixedAttributes) {
        this(editor, new AttributeKey<?>[]{key}, null, null, fixedAttributes);
    }

    public DefaultAttributeAction(DrawingEditor editor, AttributeKey<?>[] keys) {
        this(editor, keys, null, null);
    }

    /** Creates a new instance. */
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey<?> key, @Nullable Icon icon) {
        this(editor, key, null, icon);
    }

    /** Creates a new instance. */
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey<?> key, @Nullable String name) {
        this(editor, key, name, null);
    }

    public DefaultAttributeAction(DrawingEditor editor, AttributeKey<?> key, @Nullable String name, @Nullable Icon icon) {
        this(editor, new AttributeKey<?>[]{key}, name, icon);
    }

    public DefaultAttributeAction(DrawingEditor editor, AttributeKey<?>[] keys,
            @Nullable String name, @Nullable Icon icon) {
        this(editor, keys, name, icon, new HashMap<AttributeKey<?>, Object>());
    }

    public DefaultAttributeAction(DrawingEditor editor,
            AttributeKey<?>[] keys, @Nullable String name, @Nullable Icon icon,
            @Nullable Map<AttributeKey<?>, Object> fixedAttributes) {
        super(editor);
        this.keys = keys.clone();
        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        setEnabled(true);
        editor.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DefaultAttributeAction.this.keys[0].getKey())) {
                    putValue("attribute_" + DefaultAttributeAction.this.keys[0].getKey(), evt.getNewValue());
                }
            }
        });
        this.fixedAttributes = fixedAttributes;
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (getView() != null && getView().getSelectionCount() > 0) {
            ResourceBundleUtil labels =
                    DrawLabels.getLabels();
            CompositeEdit edit = new CompositeEdit(labels.getString("drawAttributeChange"));
            fireUndoableEditHappened(edit);
            changeAttribute();
            fireUndoableEditHappened(edit);
        }
    }

    @SuppressWarnings("unchecked")
    public void changeAttribute() {
        CompositeEdit edit = new CompositeEdit("attributes");
        fireUndoableEditHappened(edit);
        DrawingEditor editor = getEditor();
        for (Figure figure :getView().getSelectedFigures()) {
            figure.willChange();
            for (int j = 0; j < keys.length; j++) {
                figure.set((AttributeKey<Object>)keys[j], editor.getDefaultAttribute(keys[j]));
            }
            for (Map.Entry<AttributeKey<?>, Object> entry : fixedAttributes.entrySet()) {
                figure.set((AttributeKey<Object>)entry.getKey(), entry.getValue());

            }
            figure.changed();
        }
        fireUndoableEditHappened(edit);
    }

    public void selectionChanged(FigureSelectionEvent evt) {
        //setEnabled(getView().getSelectionCount() > 0);
    }
}
