/*
 * @(#)TypeToken.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.reflect;

import org.jhotdraw8.util.Preconditions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {
    private final Type runtimeType;

    public TypeToken() {
        this.runtimeType = capture();
    }

    /**
     * Returns the captured type.
     */
    final Type capture() {
        Type superclass = getClass().getGenericSuperclass();
        Preconditions.checkArgument(superclass instanceof ParameterizedType, "%s isn't parameterized", superclass);
        return ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    /**
     * Returns the represented type.
     */
    public final Type getType() {
        return runtimeType;
    }
}
