/* @(#)FigureSpecificConstrainer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.constrain;

import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.draw.figure.Figure;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import org.jhotdraw8.draw.DrawingView;

/**
 * Allows to use different constrainers for different figure types.
 * <p>
 * XXX This could be an abstract class with abstract method getConstrainer. The
 * constrainerMap and the defaultConstrainer could be moved into a concrete
 * subclass.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id: FigureSpecificConstrainer.java 1149 2016-11-18 11:00:10Z
 * rawcoder $
 */
public class FigureSpecificConstrainer extends AbstractConstrainer implements Constrainer {

    private final Path node = new Path();
    // ----
    // property names
    // ----

    /**
     * The name of the "constrainerMap" property.
     */
    public final String CONSTRAINER_MAP_PROPERTY = "constrainerMap";
    /**
     * The name of the "defaultConstrainer" property.
     */
    public final String DEFAULT_CONSTRAINER_PROPERTY = "defaultConstrainer";

    // ----
    // property fields
    // ----
    /**
     * Maps figure classes to constrainers.
     */
    private final ReadOnlyMapWrapper<Class<?>, Constrainer> constrainerMap
            = new ReadOnlyMapWrapper<>(this, "CONSTRAINER_MAP_PROPERTY", FXCollections.observableHashMap());

    /**
     * All figures which are not in the map use the default constrainer.
     */
    private final NonnullProperty<Constrainer> defaultConstrainer = new NonnullProperty<>(this, DEFAULT_CONSTRAINER_PROPERTY, new NullConstrainer());

    // ----
    // property methods
    // ----
    public ObservableMap<Class<?>, Constrainer> constrainerMapProperty() {
        return constrainerMap;
    }

    public NonnullProperty<Constrainer> defaultConstrainerProperty() {
        return defaultConstrainer;
    }

    public Map<Class<?>, Constrainer> getConstrainerMap() {
        return constrainerMap.get();
    }

    public Constrainer getDefaultConstrainer() {
        return defaultConstrainer.get();
    }

    public void setDefaultConstrainer(Constrainer newValue) {
        defaultConstrainer.set(newValue);
    }
    // ----
    // behavior methods
    // ----

    /**
     * Retrieves the constrainer for the specified figure.
     */
    private Constrainer getConstrainer(Figure f) {
        Constrainer c = constrainerMap.get(f.getClass());
        return c != null ? c : defaultConstrainer.get();
    }

    @Override
    public Point2D translatePoint(Figure f, Point2D p, Point2D dir) {
        return getConstrainer(f).translatePoint(f, p, dir);
    }

    @Override
    public Rectangle2D translateRectangle(Figure f, Rectangle2D r, Point2D dir) {
        return getConstrainer(f).translateRectangle(f, r, dir);
    }

    @Override
    public double translateAngle(Figure f, double angle, double dir) {
        return getConstrainer(f).translateAngle(f, angle, dir);
    }

    @Override
    public Point2D constrainPoint(Figure f, Point2D p) {
        return getConstrainer(f).constrainPoint(f, p);
    }

    @Override
    public Rectangle2D constrainRectangle(Figure f, Rectangle2D r) {
        return getConstrainer(f).constrainRectangle(f, r);
    }

    @Override
    public double constrainAngle(Figure f, double angle) {
        return getConstrainer(f).constrainAngle(f, angle);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView drawingView) {
        // empty
    }

}
