/*
 * @(#)CssRadialGradient.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssStop;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a radial gradient.
 *
 * @author Werner Randelshofer
 */
public class CssRadialGradient implements Paintable {

    private RadialGradient radialGradient;
    private final double focusAngle;
    private final double focusDistance;
    private final double centerX;
    private final double centerY;
    private final double radius;
    private final boolean proportional;
    private final CycleMethod cycleMethod;
    private final CssStop[] cstops;

    public CssRadialGradient() {
        this(0, 0, 0.5, 0.5,
                1.0, true, CycleMethod.NO_CYCLE);
    }

    public CssRadialGradient(double focusAngle, double focusDistance, double centerX, double centerY, double radius, boolean proportional, CycleMethod cycleMethod,
                             CssStop... stops) {
        this.focusAngle = focusAngle;
        this.focusDistance = focusDistance;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.proportional = proportional;
        this.cycleMethod = cycleMethod;
        this.cstops = stops;
    }

    public CssRadialGradient(@NonNull RadialGradient radialGradient) {
        this.radialGradient = radialGradient;
        this.centerX = radialGradient.getCenterX();
        this.centerY = radialGradient.getCenterY();
        this.radius = radialGradient.getRadius();
        this.focusAngle = radialGradient.getFocusAngle();
        this.focusDistance = radialGradient.getFocusDistance();
        this.proportional = radialGradient.isProportional();
        this.cycleMethod = radialGradient.getCycleMethod();
        List<Stop> stopList = radialGradient.getStops();
        cstops = new CssStop[stopList.size()];
        for (int i = 0; i < cstops.length; i++) {
            Stop stop = stopList.get(i);
            cstops[i] = new CssStop(stop.getOffset(), new CssColor(stop.getColor()));
        }
    }

    public RadialGradient getRadialGradient() {
        if (radialGradient == null) {
            Stop[] stops = new Stop[cstops.length];
            for (int i = 0; i < cstops.length; i++) {
                CssStop cstop = cstops[i];
                double offset;
                if (cstop.getOffset() == null) {
                    int left = i, right = i;
                    for (; left > 0 && cstops[left].getOffset() == null; left--) {
                    }
                    for (; right < cstops.length - 1 && cstops[right].getOffset() == null; right++) {
                    }
                    double leftOffset = cstops[left].getOffset() == null ? 0.0 : cstops[left].getOffset();
                    double rightOffset = cstops[right].getOffset() == null ? 1.0 : cstops[right].getOffset();
                    if (i == left) {
                        offset = leftOffset;
                    } else if (i == right) {
                        offset = rightOffset;
                    } else {
                        double mix = (double) (i - left) / (right - left);
                        offset = leftOffset * (1 - mix) + rightOffset * mix;
                    }
                } else {
                    offset = cstop.getOffset();
                }

                stops[i] = new Stop(offset, cstop.getColor().getColor());
            }
            radialGradient = new RadialGradient(focusAngle, focusDistance, centerX, centerY, radius, proportional, cycleMethod, stops);
        }
        return radialGradient;
    }

    @Override
    public Paint getPaint() {
        return getRadialGradient();
    }

    public @NonNull Iterable<CssStop> getStops() {
        return Arrays.asList(cstops);
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }

    public double getFocusAngle() {
        return focusAngle;
    }

    public double getFocusDistance() {
        return focusDistance;
    }

    public boolean isProportional() {
        return proportional;
    }

    public CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CssRadialGradient other = (CssRadialGradient) obj;
        if (Double.doubleToLongBits(this.centerX) != Double.doubleToLongBits(other.centerX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.centerY) != Double.doubleToLongBits(other.centerY)) {
            return false;
        }
        if (Double.doubleToLongBits(this.radius) != Double.doubleToLongBits(other.radius)) {
            return false;
        }
        if (Double.doubleToLongBits(this.focusAngle) != Double.doubleToLongBits(other.focusAngle)) {
            return false;
        }
        if (this.proportional != other.proportional) {
            return false;
        }
        if (!Objects.equals(this.radialGradient, other.radialGradient)) {
            return false;
        }
        if (this.cycleMethod != other.cycleMethod) {
            return false;
        }
        return Arrays.deepEquals(this.cstops, other.cstops);
    }

    @Override
    public @NonNull String toString() {
        return "CssRadialGradient{" + "focusAngle=" + focusAngle + ", focusDistance=" + focusDistance + "centerX=" + centerX + ", centerY=" + centerY + ", radius=" + radius + ", proportional=" + proportional + ", " + cycleMethod + ", stops=" + Arrays.toString(cstops) + '}';
    }
}
