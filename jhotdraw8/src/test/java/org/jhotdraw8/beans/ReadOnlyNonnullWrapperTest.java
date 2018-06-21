/* @(#)ReadOnlyNonnullWrapperTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 *
 * @author wr
 */
public class ReadOnlyNonnullWrapperTest {
    
   @Test
    public void testBind() {
        double minv = 0;
        double maxv = 1;

        ReadOnlyNonnullWrapper<String> p1 = new ReadOnlyNonnullWrapper<>(null, null, "hello");
        ObjectProperty<String> p2 = new SimpleObjectProperty<>(null);
        p1.addListener((o, oldv, newv) -> {
            assertNotNull(newv);
        });
        try {
            p1.set(null);
            fail("NPE not thrown on set");
        } catch (NullPointerException e) {

        }
        try {
            p1.bind(p2);
            fail("NPE not thrown from bind");
        } catch (NullPointerException e) {

        }
    }
    
}
