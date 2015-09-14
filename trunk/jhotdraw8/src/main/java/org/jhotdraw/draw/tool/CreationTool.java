/* @(#)CreationTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import static java.lang.Math.*;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.util.Resources;

/**
 * CreationTool.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CreationTool extends AbstractTool {

    private Supplier<Figure> factory;

    /** The created figure. */
    private Figure figure;

    /** The rubber band. */
    private double x1, y1, x2, y2;

    /** The minimum size of a created figure (in view coordinates. */
    private double minSize=2;
    
    public CreationTool(String name, Resources rsrc, Supplier<Figure> factory) {
        super(name, rsrc);
        this.factory = factory;
    }

    public void setFactory(Supplier<Figure> factory) {
        this.factory = factory;
    }

    @Override
    protected void stopEditing() {
        figure = null;
    }

    @Override
    protected void onMousePressed(MouseEvent event, DrawingView dv) {
        x1 = event.getX();
        y1 = event.getY();
        x2 = x1;
        y2 = y1;
        figure = factory.get();
        Point2D c = dv.getConstrainer().constrainPoint(dv.viewToDrawing(new Point2D(x1, y1)));
        figure.reshape(c.getX(), c.getY(), 1, 1);
        dv.getDrawing().add(figure);
    }

    @Override
    protected void onMouseReleased(MouseEvent event, DrawingView dv) {
        if (figure != null) {
            if (abs(x2 - x1)<minSize && abs(y2 - y1)<minSize) {
                Point2D c1 = dv.getConstrainer().constrainPoint(dv.viewToDrawing(x1, y1));
                Point2D c2 = dv.getConstrainer().translatePoint(dv.viewToDrawing(x1+minSize, y1+minSize),Constrainer.DIRECTION_NEAREST);
                if (c2.equals(c1)) {
                    c2 = new Point2D(c1.getX() + 10, c1.getY() + 10);
                }
                figure.reshape(c1.getX(), c1.getY(), c2.getX() - c1.getX(), c2.getY() - c1.getY());
            }
            dv.selectionProperty().clear();
            dv.selectionProperty().add(figure);
            figure = null;
        }
    }

    @Override
    protected void onMouseDragged(MouseEvent event, DrawingView dv) {
        if (figure != null) {
            x2 = event.getX();
            y2 = event.getY();
            Point2D c1 = dv.getConstrainer().constrainPoint(dv.viewToDrawing(x1, y1));
            Point2D c2 = dv.getConstrainer().constrainPoint(dv.viewToDrawing(x2, y2));
            figure.reshape(c1.getX(), c1.getY(), c2.getX() - c1.getX(), c2.getY() - c1.getY());
        }
    }

    @Override
    protected void onMouseClicked(MouseEvent event, DrawingView dv) {
    }

}
