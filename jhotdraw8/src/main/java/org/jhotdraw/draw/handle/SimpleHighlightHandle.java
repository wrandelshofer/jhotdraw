/* @(#)SimpleHiglightHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.beans.InvalidationListener;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jhotdraw.draw.DrawingModel;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleDrawingModel;

/**
 * SimpleHiglightHandle.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleHighlightHandle extends AbstractHandle {

    private Rectangle node;

    public SimpleHighlightHandle(Figure figure, DrawingView dv) {
        super(figure,dv);

        node = new Rectangle();
        node.setFill(null);
        node.setStroke(Color.LIGHTBLUE);

        updateNode();
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void onMouseDragged(double dx, double dy) {
        // empty. This handle is not interactive
    }

    @Override
    protected void updateNode() {
        Bounds r = 
        dv.getDrawingToView().transform(getFigure().getLayoutBounds());
        node.setX(r.getMinX());
        node.setY(r.getMinY());
        node.setWidth(r.getWidth());
        node.setHeight(r.getHeight());
    }

}
