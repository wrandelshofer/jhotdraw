/* @(#)ReadOnlyNonnullWrapperNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author wr
 */
public class ReadOnlyNonnullWrapperNGTest {
    
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
