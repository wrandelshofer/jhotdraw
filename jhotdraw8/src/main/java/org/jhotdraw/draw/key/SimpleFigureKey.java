/* @(#)SimpleFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;

/**
 * SimpleFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleFigureKey<T> extends SimpleKey<T> implements FigureKey<T> {

    private final DirtyMask dirtyMask;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param dirtyMask the dirty mask
     */
    public SimpleFigureKey(String key, Class<T> clazz, DirtyMask dirtyMask) {
        this(key, clazz, "", dirtyMask,null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     * @param dirtyMask the dirty bits
     */
    public SimpleFigureKey(String key,Class<T> clazz, DirtyMask dirtyMask, T defaultValue) {
        this(key, clazz, "",  dirtyMask,defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     * type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue The default value.
     * @param dirtyMask the dirty bits
     */
    public SimpleFigureKey(String name, Class<?> clazz, String typeParameters, DirtyMask dirtyMask, T defaultValue) {
        super(name, clazz, typeParameters, defaultValue);
        this.dirtyMask = dirtyMask;
    }

    public DirtyMask getDirtyMask() {
        return dirtyMask;
    }
    
    
}
