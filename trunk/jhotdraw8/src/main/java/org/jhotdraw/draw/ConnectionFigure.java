/* @(#)ConnectionFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.CenterConnector;
import org.jhotdraw.draw.connector.Connector;

/**
 * A <em>connection figure</em> connects two figures with a 
 * geometric path.
 * <p>
 * The location of the start and end points of the geometric path is defined by
 * {@link Connector} objects, which are supplied by the connected figures.
 * <p>
 * The geometric path of the connection figure can be laid out using a
 * {@code Liner}.
 * <p>
 * A connection figure listens to changes in the properties of the two figures
 * that it connects. If a property is changed, the connection figure updates
 * it start, middle and end points, which may result in firing an invalidation
 * event when its {@code Node} needs to be updated.
 *
 * ConnectionFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ConnectionFigure extends Figure {
    // ----
    // keys
    // ----

    /**
     * The start position of the geometric path.
     */
    public static FigureKey<Point2D> START= new FigureKey<>("start", Point2D.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY,DirtyBits.LAYOUT_BOUNDS,DirtyBits.VISUAL_BOUNDS),new Point2D(0,0));
    /**
     * The end position of the geometric path.
     */
    public static FigureKey<Point2D> END = new FigureKey<>("end", Point2D.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY,DirtyBits.LAYOUT_BOUNDS,DirtyBits.VISUAL_BOUNDS),new Point2D(0,0));
    /**
     * The start figure.
     * Is null if the figure is not connected at the start.
     * <p>
     * If the value is changed. This figure must add or remove itself from
     * the list of connections on the {@code ConnectableFigure}.</p>
     */
    public static FigureKey<ConnectableFigure> START_FIGURE = new FigureKey<>("startFigure",ConnectableFigure.class, DirtyMask.of(DirtyBits.STATE), null);
    /**
     * The end figure.
     * Is null if the figure is not connected at the end.
     * <p>
     * If the value is changed. This figure must add or remove itself from
     * the list of connections on the {@code ConnectableFigure}.</p>
     */
    public static FigureKey<ConnectableFigure> END_FIGURE = new FigureKey<>("endFigure", ConnectableFigure.class,DirtyMask.of(DirtyBits.STATE),  null);
    /**
     * The start connector.
     */
    public static FigureKey<Connector> START_CONNECTOR = new FigureKey<>("startConnector", Connector.class, DirtyMask.of(DirtyBits.STATE), new CenterConnector());
    /**
     * The end connector.
     */
    public static FigureKey<Connector> END_CONNECTOR = new FigureKey<>("endConnector", Connector.class, DirtyMask.of(DirtyBits.STATE), new CenterConnector());

}
