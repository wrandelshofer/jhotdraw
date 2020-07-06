/*
 * @(#)ToDoubleTriFunction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

@FunctionalInterface
public interface ToDoubleTriFunction<T, U, V> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @return the function result
     */
    double applyAsDouble(T t, U u, V v);
}
