/* @(#)ClampedDoubleProperty.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.SimpleDoubleProperty;
import static java.lang.Math.*;

/**
 * ClampedDoubleProperty.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ClampedDoubleProperty extends SimpleDoubleProperty {

    private final double minValue;
    private final double maxValue;

    public ClampedDoubleProperty(Object bean, String name, double initialValue, double minValue, double maxValue) {
        super(bean, name, initialValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void set(double newValue) {
        super.set(max(minValue, min(newValue, maxValue)));
    }

    @Override
    public double get() {
        // note we must override get too, so that values are still clamped, 
        // when we are bound to another property
        return max(minValue, min(super.get(), maxValue));
    }
}
