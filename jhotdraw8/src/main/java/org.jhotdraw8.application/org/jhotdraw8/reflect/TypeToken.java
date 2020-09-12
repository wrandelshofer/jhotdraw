/*
 * @(#)TypeToken.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.reflect;

import org.jhotdraw8.util.Preconditions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Provides a {@link Type} with reified type parameters.
 * <p>
 * Usage:
 * <pre>
 * {@code new TypeToken<List<String>>() {}.getType(); }
 * </pre>
 * <p>
 * Copyright (C) 2012 The Guava Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
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
