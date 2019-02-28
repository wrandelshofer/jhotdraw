/* @(#)NullableObjectFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.collection.ObjectKey;

/**
 * NullableObjectFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ObjectFigureKey<T> extends ObjectKey<T> implements FigureKey<T>,
        NonnullMapAccessor<T> {

    final static long serialVersionUID = 1L;

    private final DirtyMask dirtyMask;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param key          The name of the name.
     * @param clazz        The type of the value.
     * @param defaultValue The default value.
     * @param dirtyMask    the dirty bits
     */
    public ObjectFigureKey(@Nonnull String key, @Nonnull Class<T> clazz, @Nonnull DirtyMask dirtyMask, @Nonnull T defaultValue) {
        this(key, clazz, null, dirtyMask, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and disallowing null values.
     *
     * @param name           The name of the key.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue   The default value.
     * @param dirtyMask      the dirty bits
     */
    public ObjectFigureKey(@Nonnull String name, @Nonnull Class<?> clazz, @Nullable Class<?>[] typeParameters, @Nonnull DirtyMask dirtyMask, @Nonnull T defaultValue) {
        super(name, clazz, typeParameters, false, false, defaultValue);
        this.dirtyMask = dirtyMask;
    }

    public DirtyMask getDirtyMask() {
        return dirtyMask;
    }

}
