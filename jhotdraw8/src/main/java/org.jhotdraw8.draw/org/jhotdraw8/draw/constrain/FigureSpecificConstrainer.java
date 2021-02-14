/*
 * @(#)FigureSpecificConstrainer.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.constrain;

import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

import java.util.Map;

/**
 * Allows to use different constrainers for different figure types.
 * <p>
 * XXX This could be an abstract class with abstract method getConstrainer. The
 * constrainerMap and the defaultConstrainer could be moved into a concrete
 * subclass.
 * </p>
 *
 * @author Werner Randelshofer
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
    private final NonNullObjectProperty<Constrainer> defaultConstrainer = new NonNullObjectProperty<>(this, DEFAULT_CONSTRAINER_PROPERTY, new NullConstrainer());

    // ----
    // property methods
    // ----
    public @NonNull ObservableMap<Class<?>, Constrainer> constrainerMapProperty() {
        return constrainerMap;
    }

    public @NonNull NonNullObjectProperty<Constrainer> defaultConstrainerProperty() {
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
    private Constrainer getConstrainer(@NonNull Figure f) {
        Constrainer c = constrainerMap.get(f.getClass());
        return c != null ? c : defaultConstrainer.get();
    }

    @Override
    public CssPoint2D translatePoint(@NonNull Figure f, CssPoint2D p, CssPoint2D dir) {
        return getConstrainer(f).translatePoint(f, p, dir);
    }

    @Override
    public CssRectangle2D translateRectangle(@NonNull Figure f, CssRectangle2D r, CssPoint2D dir) {
        return getConstrainer(f).translateRectangle(f, r, dir);
    }

    @Override
    public double translateAngle(@NonNull Figure f, double angle, double dir) {
        return getConstrainer(f).translateAngle(f, angle, dir);
    }

    @Override
    public CssPoint2D constrainPoint(@NonNull Figure f, CssPoint2D p) {
        return getConstrainer(f).constrainPoint(f, p);
    }

    @Override
    public CssRectangle2D constrainRectangle(@NonNull Figure f, CssRectangle2D r) {
        return getConstrainer(f).constrainRectangle(f, r);
    }

    @Override
    public double constrainAngle(@NonNull Figure f, double angle) {
        return getConstrainer(f).constrainAngle(f, angle);
    }

    @Override
    public @NonNull Node getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView drawingView) {
        // empty
    }

}
