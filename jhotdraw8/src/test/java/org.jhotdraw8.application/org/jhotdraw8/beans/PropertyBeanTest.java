/* @(#)PropertyBeanTest.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.value.ObservableValue;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PropertyBeanTest.
 *
 * @author Werner Randelshofer
 */
public class PropertyBeanTest {

    public PropertyBeanTest() {
    }

    /**
     * Test of valueAt method, of class PropertyBean.
     */
    @Test
    public void testGetObservableValue() {
        System.out.println("getObservableValue");
        Key<String> key = new ObjectKey<String>("key", String.class);
        PropertyBean bean = new SimplePropertyBean();
        ObservableValue<String> ov = bean.valueAt( key);
        String[] newValue = new String[1];
        ov.addListener((o, oldv, newv) -> {
            newValue[0] = newv;
        });
        bean.set(key, "hello");
        assertEquals("hello", newValue[0]);
    }

}
