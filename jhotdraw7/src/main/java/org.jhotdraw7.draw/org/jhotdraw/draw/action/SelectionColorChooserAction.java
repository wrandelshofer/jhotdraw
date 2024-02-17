/* @(#)SelectionColorChooserAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

import javax.swing.Icon;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * This is like EditorColorChooserAction, but the JColorChooser is initialized with
 * the color of the currently selected Figures.
 * <p>
 * The behavior for choosing the initial color of the JColorChooser matches with
 * {@link SelectionColorIcon }.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectionColorChooserAction extends EditorColorChooserAction {
    private static final long serialVersionUID = 1L;
    
    /** Creates a new instance. */
    public SelectionColorChooserAction(DrawingEditor editor, AttributeKey<Color> key) {
        this(editor, key, null, null);
    }
    /** Creates a new instance. */
    public SelectionColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, @Nullable Icon icon) {
        this(editor, key, null, icon);
    }
    /** Creates a new instance. */
    public SelectionColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, @Nullable String name) {
        this(editor, key, name, null);
    }
    public SelectionColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, @Nullable String name, @Nullable Icon icon) {
        this(editor, key, name, icon, new HashMap<AttributeKey<?>,Object>());
    }
    public SelectionColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, @Nullable String name, @Nullable Icon icon,
                                       @Nullable Map<AttributeKey<?>,Object> fixedAttributes) {
        super(editor, key, name, icon, fixedAttributes);
    }
    
    @Override
    protected Color getInitialColor() {
        Color initialColor = null;
        
        DrawingView v = getEditor().getActiveView();
        if (v != null && v.getSelectedFigures().size() == 1) {
            Figure f = v.getSelectedFigures().iterator().next();
            initialColor = f.get(key);
        }
        if (initialColor == null) {
            initialColor = super.getInitialColor();
        }
        return initialColor;
    }
}
