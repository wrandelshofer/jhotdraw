/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * LabelFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LabelFigure extends AbstractLabelFigure implements FontableFigure, TextableFigure, StyleableFigure, LockableFigure, TransformableFigure, CompositableFigure {

    public LabelFigure() {
        this(0, 0, "");
    }

    public LabelFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public LabelFigure(double x, double y, String text, Object... keyValues) {
        set(TEXT, text);
        set(ORIGIN, new Point2D(x, y));
        for (int i = 0; i < keyValues.length; i += 2) {
            @SuppressWarnings("unchecked") // the set() method will perform the check for us
            Key<Object> key = (Key<Object>) keyValues[i];
            set(key, keyValues[i + 1]);
        }
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        super.updateNode(ctx, node);
        applyTransformableFigureProperties(node);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
    }
    
    

    @Override
    protected String getText() {
        return get(TEXT);
    }
}
