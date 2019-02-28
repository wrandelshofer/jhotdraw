/* @(#)StyleableMapNGTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.key.NullablePaintableStyleableFigureKey;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author werni
 */
public class StyleableMapNGTest {

    public StyleableMapNGTest() {
    }

    @Test
    public void testNullValueIsNotSameAsDefaultPropertyValue() {
        System.out.println("testNullValueIsNotSameAsDefaultPropertyValue");
        StyleableMap<Key<?>, Object> instance = new SimpleStyleableMap<>();
        final NullablePaintableStyleableFigureKey key = FillableFigure.FILL;

        assertNotNull(key.getDefaultValue(), "need a key with a non-null default value for this test");
        assertFalse(instance.containsKey(key), "value has not been set, map must not contain key");
        assertEquals(instance.get(key), null, "value has not been set, must deliver null");

        instance.put(key, null);

        assertNull(instance.get(key), "value has been explicitly set to null");
        assertTrue(instance.containsKey(key), "map must contain key after explicit set");

        instance.remove(key);

        assertEquals(instance.get(key), null, "key has been removed, value must be nulll again");
        assertFalse(instance.containsKey(key), "key has been removed, map must not contain key");

    }

    @Test
    public void testGetStyled() {
        StyleableMap<String, String> map = new SimpleStyleableMap<>();
        map.put("1", "user");
        map.put(StyleOrigin.AUTHOR, "1", "author");
        map.put(StyleOrigin.USER_AGENT, "1", "userAgent");

        assertEquals(map.getStyledMap().get("1"), "author");
        assertEquals(map.getMap(StyleOrigin.USER).get("1"), "user");
        assertEquals(map.getMap(StyleOrigin.USER_AGENT).get("1"), "userAgent");

    }

    @Test
    public void testContainsKey() {
        StyleableMap<String, String> map = new SimpleStyleableMap<>();
        map.put("1", "user");

        assertTrue(map.containsKey("1"));
        assertNull(map.get("2"));
        assertFalse(map.containsKey("2"));

        map.put("3", "user");
        assertTrue(map.containsKey("1"));
        assertTrue(map.containsKey("3"));
        assertNull(map.get("2"));
        assertFalse(map.containsKey("2"));
    }

    @Test
    public void testPut() {
        StyleableMap<String, String> map = new SimpleStyleableMap<>();

        map.put("1", "user");
        map.put(StyleOrigin.AUTHOR, "1", "author");

        assertEquals(map.size(), 1);
        assertEquals(map.get("1"), "user");
        assertEquals(map.get(StyleOrigin.USER, "1"), "user");
        assertEquals(map.get(StyleOrigin.AUTHOR, "1"), "author");
        assertEquals(map.get(StyleOrigin.AUTHOR, "1"), "author");
    }

    @Test
    public void testIterator1() {
        StyleableMap<String, String> map = new SimpleStyleableMap<>();

        map.put("1", "user1");
        map.put("2", "user2");
        map.put("3", "user3");
        map.put("4", "user4");

        map.put(StyleOrigin.AUTHOR, "2", "author2");
        map.put(StyleOrigin.AUTHOR, "3", "author3");

        for (Map.Entry<String, String> e : map.entrySet()) {
            System.out.println(e);
        }
        System.out.println("---");
    }

    @Test
    public void testIterator2() {
        StyleableMap<String, String> map = new SimpleStyleableMap<>();

        map.put(StyleOrigin.AUTHOR, "1", "author1");
        map.put(StyleOrigin.AUTHOR, "2", "author2");
        map.put(StyleOrigin.AUTHOR, "3", "author3");
        map.put(StyleOrigin.AUTHOR, "4", "author4");

        map.put(StyleOrigin.USER, "2", "user2");
        map.put(StyleOrigin.USER, "3", "user3");

        for (Map.Entry<String, String> e : map.entrySet()) {
            System.out.println(e);
        }
        System.out.println("---");
    }

}
