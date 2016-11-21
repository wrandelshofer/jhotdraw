/* @(#)StyleableMapNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * StyleableMapNGTest.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StyleableMapNGTest {
    
      @Test
    public void testNullValueIsNotSameAsDefaultPropertyValue() {
        System.out.println("testNullValueIsNotSameAsDefaultPropertyValue");
        StyleableMap<Key<?>, Object> instance = new StyleableMap<>();
        final PaintableStyleableFigureKey key = FillableFigure.FILL_COLOR;
        
        
       assertNotNull(key.getDefaultValue(),"need a key with a non-null default value for this test");
       assertFalse(instance.containsKey(key),"value has not been set, map must not contain key");
        assertEquals(instance.get(key),null,"value has not been set, must deliver null");
        
        instance.put(key, null);
        
       assertNull(instance.get(key),"value has been explicitly set to null");
       assertTrue(instance.containsKey(key),"map must contain key after explicit set");
       
       instance.remove(key);
       
       assertEquals(instance.get(key),null,"key has been removed, value must be nulll again");
       assertFalse(instance.containsKey(key),"key has been removed, map must not contain key");

    }

}
