/* @(#)ClampedDoubleProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.property.SimpleDoubleProperty;
import static java.lang.Math.*;

/**
 * ClampedDoubleProperty.
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

}
