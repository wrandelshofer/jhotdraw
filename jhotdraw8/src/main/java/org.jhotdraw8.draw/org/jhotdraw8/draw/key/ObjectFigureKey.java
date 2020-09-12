/*
 * @(#)ObjectFigureKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * ObjectFigureKey.
 *
 * @author Werner Randelshofer
 */
public class ObjectFigureKey<@NonNull T> extends ObjectKey<@NonNull T> implements
        NonNullMapAccessor<@NonNull T> {

    final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param name         The name of the name.
     * @param type         The type of the value.
     * @param defaultValue The default value.
     */
    public ObjectFigureKey(@NonNull String name, @NonNull Type type, @NonNull T defaultValue) {
        super(name, type, false, false, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param name         The name of the key.
     * @param type         The type of the value.
     * @param defaultValue The default value.
     */
    public ObjectFigureKey(@NonNull String name, @NonNull TypeToken<T> type, @NonNull T defaultValue) {
        this(name, type.getType(), defaultValue);
    }

}
