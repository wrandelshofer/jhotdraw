/* @(#)ChopRectangleConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import java.awt.geom.Rectangle2D;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.ConnectableFigure;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.FigureKey;
import org.jhotdraw.geom.Geom;
import static org.jhotdraw.draw.shape.AbstractShapeFigure.*;
/**
 * ChopRectangleConnector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChopRectangleConnector extends AbstractConnector {

    @Override
    public void updateStartPosition(ConnectionFigure connection) {
        updateStartEndPosition(connection, ConnectionFigure.START_FIGURE, ConnectionFigure.START, ConnectionFigure.END_FIGURE, ConnectionFigure.END);
    }

    private void updateStartEndPosition(ConnectionFigure connection,
            FigureKey<ConnectableFigure> targetFigureKey,
            FigureKey<Point2D> targetKey,
            FigureKey<ConnectableFigure> fromFigureKey,
            FigureKey<Point2D> fromKey) {

        // FIXME - implement me properly
        final Point2D from;
        final Figure targetFigure = connection.get(targetFigureKey);
        final Figure fromFigure = connection.get(fromFigureKey);
        if (fromFigure != null) {
            final Bounds b = fromFigure.getBoundsInDrawing();
            from = new Point2D(b.getMinX() + b.getWidth() / 2.0, (b.getMinY()
                    + b.getHeight() / 2.0));
        } else {
            from = connection.get(ConnectionFigure.END);
        }
        Point2D target = chop(targetFigure, from);
        connection.set(targetKey, target);
    }

    @Override
    public void updateEndPosition(ConnectionFigure connection) {
        updateStartEndPosition(connection,
                ConnectionFigure.END_FIGURE, ConnectionFigure.END,
                ConnectionFigure.START_FIGURE, ConnectionFigure.START
        );
    }

    protected Point2D chop(Figure target, Point2D from) {
        // FIXME implement me properly
        // target = getConnectorTarget(target);
        Bounds bounds = target.getBoundsInDrawing();
        Rectangle2D.Double r = new Rectangle2D.Double(bounds.getMinX(),bounds.getMinY(),
        bounds.getWidth(),bounds.getHeight());
        if (target.get(STROKE_PAINT) != null) {
            double grow;
            switch (target.get(STROKE_TYPE)) {
                case CENTERED:
                default :
                    grow = target.get(STROKE_WIDTH) / 2d;
                    break;
                case OUTSIDE :
                    grow = target.get(STROKE_WIDTH);
                    break;
                case INSIDE :
                    grow = 0d;
                    break;
            }
            Geom.grow(r, grow, grow);
        }
        java.awt.geom.Point2D.Double from2D = new java.awt.geom.Point2D.Double(from.getX(),from.getY());
        java.awt.geom.Point2D.Double p= Geom.angleToPoint(r, Geom.pointToAngle(r, from2D));
        return new Point2D(p.getX(),p.getY());
    }

}
