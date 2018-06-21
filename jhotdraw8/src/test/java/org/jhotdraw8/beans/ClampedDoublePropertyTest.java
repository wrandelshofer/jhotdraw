/* @(#)ClampedDoublePropertyTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author wr
 */
public class ClampedDoublePropertyTest {

    @Test
    public void testBind() {
        double minv = 0;
        double maxv = 1;

        ClampedDoubleProperty p1 = new ClampedDoubleProperty(null, null, 0.5, minv, maxv);
        DoubleProperty p2 = new SimpleDoubleProperty(8);
        p1.addListener((o, oldv, newv) -> {
            assertTrue(minv <= newv.doubleValue());
            assertTrue(newv.doubleValue() <= maxv);
        });
        p1.set(-7);
        p1.bind(p2);
    }

}
