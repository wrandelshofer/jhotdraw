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

    private final DirtyMask dirtyMask;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param dirtyMask the dirty mask
     */
    public FigureKey(String key, Class<T> clazz, DirtyMask dirtyMask) {
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
    public FigureKey(String key,Class<T> clazz, DirtyMask dirtyMask, T defaultValue) {
        this(key, clazz, "",  dirtyMask,defaultValue);
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
     * @param dirtyMask the dirty bits
     */
    public FigureKey(String key, Class<?> clazz, String typeParameters, DirtyMask dirtyMask, T defaultValue) {
        super(key, clazz, typeParameters, defaultValue);
        this.dirtyMask = dirtyMask;
    }

    public DirtyMask getDirtyMask() {
        return dirtyMask;
    }
    
    
}
