/* @(#)CssLinearGradient.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.jhotdraw8.css.text.CssStop;

/**
 * Represents a linear gradient.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssLinearGradient implements Paintable {

    private LinearGradient linearGradient;
    private final double startX;
    private final double startY;
    private final double endX;
    private final double endY;
    private final boolean proportional;
    private final CycleMethod cycleMethod;
    private final CssStop[] cstops;

    public CssLinearGradient(double startX, double startY, double endX, double endY, boolean proportional, CycleMethod cycleMethod,
            CssStop... stops) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.proportional = proportional;
        this.cycleMethod = cycleMethod;
        this.cstops = stops;
    }

    public CssLinearGradient(LinearGradient linearGradient) {
        this.linearGradient = linearGradient;
        this.startX = linearGradient.getStartX();
        this.startY = linearGradient.getStartY();
        this.endX = linearGradient.getEndX();
        this.endY = linearGradient.getEndY();
        this.proportional = linearGradient.isProportional();
        this.cycleMethod = linearGradient.getCycleMethod();
        List<Stop> stopList = linearGradient.getStops();
        cstops = new CssStop[stopList.size()];
        for (int i = 0; i < cstops.length; i++) {
            Stop stop = stopList.get(i);
            cstops[i] = new CssStop(stop.getOffset(), new CssColor(stop.getColor()));
        }
    }

    public LinearGradient getLinearGradient() {
        if (linearGradient == null) {
            Stop[] stops = new Stop[cstops.length];
            for (int i = 0; i < cstops.length; i++) {
                CssStop cstop = cstops[i];
                double offset;
                if (cstop.getOffset() == null) {
                    int left = i, right = i;
                    for (; left > 0 && cstops[left].getOffset() == null; left--);
                    for (; right < cstops.length - 1 && cstops[right].getOffset() == null; right++);
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
            linearGradient = new LinearGradient(startX, startY, endX, endY, proportional, cycleMethod, stops);
        }
        return linearGradient;
    }

    @Override
    public Paint getPaint() {
        return getLinearGradient();
    }

    @Nonnull
    public Iterable<CssStop> getStops() {
        return Arrays.asList(cstops);
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
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
        final CssLinearGradient other = (CssLinearGradient) obj;
        if (Double.doubleToLongBits(this.startX) != Double.doubleToLongBits(other.startX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.startY) != Double.doubleToLongBits(other.startY)) {
            return false;
        }
        if (Double.doubleToLongBits(this.endX) != Double.doubleToLongBits(other.endX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.endY) != Double.doubleToLongBits(other.endY)) {
            return false;
        }
        if (this.proportional != other.proportional) {
            return false;
        }
        if (!Objects.equals(this.linearGradient, other.linearGradient)) {
            return false;
        }
        if (this.cycleMethod != other.cycleMethod) {
            return false;
        }
        if (!Arrays.deepEquals(this.cstops, other.cstops)) {
            return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public String toString() {
        return "CssLinearGradient{" + "startX=" + startX + ", startY=" + startY + ", endX=" + endX + ", endY=" + endY + ", proportional=" + proportional + ", " + cycleMethod + ", stops=" + Arrays.toString(cstops) + '}';
    }
}
