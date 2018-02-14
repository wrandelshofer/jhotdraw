/* @(#)SimplePropertyBeanNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * SimplePropertyBeanNGTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimplePropertyBeanNGTest {
    
    public SimplePropertyBeanNGTest() {
    }

    /**
     * Test of getProperties method, of class SimplePropertyBean.
     */
    @Test
    public void testNullValueIsNotSameAsDefaultPropertyValue() {
        System.out.println("testNullValueIsNotSameAsDefaultPropertyValue");
        SimplePropertyBean instance = new SimplePropertyBean();
        final PaintableStyleableFigureKey key = FillableFigure.FILL;
        
        
       assertNotNull("need a key with a non-null default value for this test",key.getDefaultValue());
       assertFalse("value has not been set, map must not contain key",instance.getProperties().containsKey(key));
        assertEquals("value has not been set, must deliver default value",instance.get(key),key.getDefaultValue());
        
        instance.set(key, null);
        
       assertNull("value has been explicitly set to null",instance.get(key));
       assertTrue("map must contain key after explicit set",instance.getProperties().containsKey(key));
       
       instance.remove(key);
       
       assertEquals("key has been removed, value must be default value",instance.get(key),key.getDefaultValue());
       assertFalse("key has been removed, map must not contain key",instance.getProperties().containsKey(key));
       
    }
    
}
