/* @(#)NullableObjectFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.collection.ObjectKey;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

/**
 * NullableObjectFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NullableObjectFigureKey<T> extends ObjectKey<T> implements FigureKey<T> {

    final static long serialVersionUID = 1L;

    private final DirtyMask dirtyMask;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param dirtyMask the dirty mask
     */
    public NullableObjectFigureKey(@Nonnull String key, @Nonnull  Class<T> clazz, @Nonnull DirtyMask dirtyMask) {
        this(key, clazz, null, dirtyMask, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     * @param dirtyMask the dirty bits
     */
    public NullableObjectFigureKey(@Nonnull String key, @Nonnull Class<T> clazz, @Nonnull DirtyMask dirtyMask, @Nullable T defaultValue) {
        this(key, clazz, null, dirtyMask, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     * type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue The default value.
     * @param dirtyMask the dirty bits
     */
    public NullableObjectFigureKey(@Nonnull String name, @Nonnull Class<?> clazz, @Nullable Class<?>[] typeParameters, @Nonnull DirtyMask dirtyMask, @Nullable T defaultValue) {
        super(name, clazz, typeParameters, true,false, defaultValue);
        this.dirtyMask = dirtyMask;
    }

    public DirtyMask getDirtyMask() {
        return dirtyMask;
    }

}
