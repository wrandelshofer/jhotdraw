/* @(#)SimpleHiglightHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

/**
 * SimpleHiglightHandle.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleHighlightHandle extends AbstractHandle {

    private Rectangle node;

    public SimpleHighlightHandle(Figure figure, DrawingView dv) {
        super(figure, dv);

        node = new Rectangle();
        node.setFill(null);
        node.setStroke(Color.BLUE);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode() {
        Bounds r
                = dv.getDrawingToView().transform(getFigure().getBoundsInLocal());
        node.setX(Math.round(r.getMinX())-0.5);
        node.setY(Math.round(r.getMinY())-0.5);
        node.setWidth(Math.round(r.getWidth()));
        node.setHeight(Math.round(r.getHeight()));
        

        applyFigureTransform(node);
    }

}
