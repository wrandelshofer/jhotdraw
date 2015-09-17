/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw;

import org.jhotdraw.collection.Key;

/**
 * FigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureKey<T> extends Key<T> {

    private final long dirtyMask;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     */
    public FigureKey(String key, Class<T> clazz) {
        this(key, clazz, "", null, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     */
    public FigureKey(String key, Class<T> clazz, T defaultValue) {
        this(key, clazz, "", defaultValue, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     * @param dirtyBits the dirty bits
     */
    public FigureKey(String key, Class<T> clazz, T defaultValue, DirtyBits... dirtyBits) {
        this(key, clazz, "", defaultValue, dirtyBits);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     * type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue The default value.
     * @param dirtyBits the dirty bits
     */
    public FigureKey(String key, Class<T> clazz, String typeParameters, T defaultValue, DirtyBits... dirtyBits) {
        super(key, clazz, typeParameters, defaultValue);
        long mask = 0;
        for (DirtyBits db : dirtyBits) {
            mask |= db.getMask();
        }
        dirtyMask = mask;
    }
}
