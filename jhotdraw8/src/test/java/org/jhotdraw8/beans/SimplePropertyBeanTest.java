/* @(#)SimplePropertyBeanTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * SimplePropertyBeanTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimplePropertyBeanTest {
    
    public SimplePropertyBeanTest() {
    }

    /**
     * Test of getProperties method, of class SimplePropertyBean.
     */
    @Test
    public void testNullValueIsNotSameAsDefaultPropertyValue() {
        System.out.println("testNullValueIsNotSameAsDefaultPropertyValue");
        SimplePropertyBean instance = new SimplePropertyBean();
        final PaintableStyleableFigureKey key = FillableFigure.FILL;
        
        
       assertNotNull(key.getDefaultValue(),
               "need a key with a non-null default value for this test");
       assertFalse(instance.getProperties().containsKey(key),
               "value has not been set, map must not contain key");
        assertEquals(instance.get(key),key.getDefaultValue(),
                "value has not been set, must deliver default value");
        
        instance.set(key, null);
        
       assertNull(instance.get(key),
               "value has been explicitly set to null");
       assertTrue(instance.getProperties().containsKey(key),
               "map must contain key after explicit set");
       
       instance.remove(key);
       
       assertEquals(instance.get(key),key.getDefaultValue(),
               "key has been removed, value must be default value");
       assertFalse(instance.getProperties().containsKey(key),
               "key has been removed, map must not contain key");
       
    }
    
}
