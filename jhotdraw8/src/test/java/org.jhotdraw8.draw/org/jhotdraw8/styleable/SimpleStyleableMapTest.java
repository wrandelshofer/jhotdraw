/* @(#)SimpleStyleableMapTest
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;


import javafx.css.StyleOrigin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        SimpleStyleableMap<String, String> instance = new SimpleStyleableMap<>();

        // WHEN getOrDefault and no value has been put
        String key = "dummyKey";
        String defaultValue = "defaultValue";
        Object result = instance.getOrDefault(key, defaultValue);

        // THEN must return default value
        assertEquals(defaultValue, result);

        // WHEN getOrDefault and a value has been put
        String putValue = "putValue";
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
        SimpleStyleableMap<String, Object> instance = new SimpleStyleableMap<>();

        // WHEN no value has been put
        String key = "dummyKey";
        boolean result = instance.containsKey(key);

        // THEN must return false
        assertFalse(result);

        // WHEN a value has been put
        String putValue = "putValue";
        instance.put(key, putValue);
        result = instance.containsKey(key);

        // THEN must return rue
        assertTrue(result);

        // WHEN key is removed
        instance.remove(key);
        result = instance.containsKey(key);
        // THEN must return default value
        assertFalse(result);
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
        assertFalse(result);

        // WHEN getOrDefault and a value has been put
        Object putValue = "putValue";
        instance.put(StyleOrigin.AUTHOR, key, putValue);
        result = instance.containsKey(StyleOrigin.AUTHOR, key);

        // THEN must return rue
        assertTrue(result);
        assertEquals(0, instance.size());
        assertEquals(1, instance.getMap(StyleOrigin.AUTHOR).size());
        assertEquals(1, instance.getMap(StyleOrigin.AUTHOR).entrySet().size());

        // WHEN key is removed
        instance.removeKey(StyleOrigin.AUTHOR, key);
        result = instance.containsKey(StyleOrigin.AUTHOR, key);
        // THEN must return default value
        assertFalse(result);
        assertEquals(0, instance.size());
        assertEquals(0, instance.getMap(StyleOrigin.AUTHOR).size());
        assertEquals(0, instance.getMap(StyleOrigin.AUTHOR).entrySet().size());
    }


}
