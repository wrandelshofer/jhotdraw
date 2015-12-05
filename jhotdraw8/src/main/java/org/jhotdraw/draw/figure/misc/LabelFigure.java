/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure.misc;

import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.CompositableFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LockableFigure;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.StyleableFigure;
import org.jhotdraw.draw.TextHolderFigure;
import org.jhotdraw.draw.TextableFigure;
import org.jhotdraw.draw.TransformableFigure;

/**
 * SimpleLabelFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LabelFigure extends AbstractLabelFigure implements TextableFigure, TextHolderFigure, StyleableFigure, LockableFigure, TransformableFigure, CompositableFigure {

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
    public void updateNode(RenderContext drawingView, Node node) {
        super.updateNode(drawingView, node);
        applyTransformableFigureProperties(node);
        applyCompositableFigureProperties(node);
    }
    
    

    @Override
    protected String getText() {
        return get(TEXT);
    }
}
