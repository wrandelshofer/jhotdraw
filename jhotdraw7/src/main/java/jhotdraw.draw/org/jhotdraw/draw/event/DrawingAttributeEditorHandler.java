/* @(#)FigureAttributeEditorHandler.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.event;

import javax.annotation.Nullable;
import org.jhotdraw.gui.*;
import java.util.HashSet;
import java.util.Set;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;

/**
 * FigureAttributeEditorHandler mediates between an AttributeEditor and the
 * currently selected Figure's in a DrawingEditor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingAttributeEditorHandler<T> extends AbstractAttributeEditorHandler<T> {

    private Drawing drawing;

    public DrawingAttributeEditorHandler(AttributeKey<T> key, AttributeEditor<T> attributeEditor, @Nullable DrawingEditor drawingEditor) {
        super(key, attributeEditor, drawingEditor, false);
    }

    public void setDrawing(Drawing newValue) {
        drawing = newValue;
        updateAttributeEditor();
    }

    public Drawing getDrawing() {
        return drawing;
    }

    @Override
    protected Set<Figure> getEditedFigures() {
        HashSet<Figure> s = new HashSet<Figure>();
        if (drawing != null) {
            s.add(drawing);
        } else if (activeView != null) {
            s.add(activeView.getDrawing());
        }
        return s;
    }
}
