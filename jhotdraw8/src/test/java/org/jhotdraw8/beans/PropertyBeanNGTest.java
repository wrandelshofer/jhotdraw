/* @(#)PropertyBeanNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.value.ObservableValue;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
     * Test of valueAt method, of class PropertyBean.
     */
    @Test
    public void testGetObservableValue() {
        System.out.println("getObservableValue");
        Key<String> key = new ObjectKey<String>("key", String.class);
        PropertyBean bean = new SimplePropertyBean();
        ObservableValue<String> ov = bean.valueAt(key);
        String[] newValue = new String[1];
        ov.addListener((o, oldv, newv) -> {
            newValue[0] = newv;
        });
        bean.set(key, "hello");
        assertEquals("hello",newValue[0]);
    }

}
