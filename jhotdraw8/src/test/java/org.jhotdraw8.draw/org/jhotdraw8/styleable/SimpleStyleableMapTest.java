/* @(#)SimpleStyleableMapTest
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;


import javafx.css.StyleOrigin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author werni
 */
public class SimpleStyleableMapTest {

    public SimpleStyleableMapTest() {
    }


    /**
     * Test of getOrDefault method, of class SimpleStyleableMap.
     */
    @Test
    public void testGetOrDefault() {
        System.out.println("getOrDefault");

        // GIVEN
        SimpleStyleableMap instance = new SimpleStyleableMap();

        // WHEN getOrDefault and no value has been put
        Object key = "dummyKey";
        Object defaultValue = "defaultValue";
        Object result = instance.getOrDefault(key, defaultValue);

        // THEN must return default value
        assertEquals(defaultValue, result);

        // WHEN getOrDefault and a value has been put
        Object putValue = "putValue";
        instance.put(key, putValue);
        result = instance.getOrDefault(key, defaultValue);

        // THEN must return put value
        assertEquals(putValue, result);

        // WHEN key is removed
        instance.remove(key);
        result = instance.getOrDefault(key, defaultValue);
        // THEN must return default value
        assertEquals(defaultValue, result);
    }

    /**
     * Test of containsKey method, of class SimpleStyleableMap.
     */
    @Test
    public void testContainsKey() {
        System.out.println("containsKey");

        // GIVEN
        SimpleStyleableMap instance = new SimpleStyleableMap();

        // WHEN getOrDefault and no value has been put
        Object key = "dummyKey";
        boolean result = instance.containsKey(key);

        // THEN must return false
        assertEquals(false, result);

        // WHEN getOrDefault and a value has been put
        Object putValue = "putValue";
        instance.put(key, putValue);
        result = instance.containsKey(key);

        // THEN must returnt rue
        assertEquals(true, result);

        // WHEN key is removed
        instance.remove(key);
        result = instance.containsKey(key);
        // THEN must return default value
        assertEquals(false, result);
    }

    /**
     * Test of containsKey method, of class SimpleStyleableMap.
     */
    @Test
    public void testContainsKeyAuthor() {
        System.out.println("containsKey");

        // GIVEN
        SimpleStyleableMap<String, Object> instance = new SimpleStyleableMap<>();

        // WHEN getOrDefault and no value has been put
        String key = "dummyKey";
        boolean result = instance.containsKey(StyleOrigin.AUTHOR, key);

        // THEN must return false
        assertEquals(false, result);

        // WHEN getOrDefault and a value has been put
        Object putValue = "putValue";
        instance.put(StyleOrigin.AUTHOR, key, putValue);
        result = instance.containsKey(StyleOrigin.AUTHOR, key);

        // THEN must returnt rue
        assertEquals(true, result);

        // WHEN key is removed
        instance.remove(StyleOrigin.AUTHOR, key);
        result = instance.containsKey(StyleOrigin.AUTHOR, key);
        // THEN must return default value
        assertEquals(false, result);
    }


}
