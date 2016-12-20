/* @(#)PropertyBeanNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.value.ObservableValue;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.SimpleKey;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * PropertyBeanNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PropertyBeanNGTest {

    public PropertyBeanNGTest() {
    }

    /**
     * Test of getObservableValue method, of class PropertyBean.
     */
    @Test
    public void testGetObservableValue() {
        System.out.println("getObservableValue");
        Key<String> key = new SimpleKey<String>("key", String.class);
        PropertyBean bean = new SimplePropertyBean();
        ObservableValue<String> ov = bean.getObservableValue(key);
        String[] newValue = new String[1];
        ov.addListener((o, oldv, newv) -> {
            newValue[0] = newv;
        });
        bean.set(key, "hello");
        assertEquals("hello",newValue[0]);
    }

}