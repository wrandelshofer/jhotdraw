/*
 * @(#)QuadConsumer.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util.function;

/**
 * Represents a consumer that accepts 4 arguments.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <V> the type of the third argument to the function
 * @param <W> the type of the fourth argument to the function
 */
@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
    /**
     * Applies this consumer to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @param w the fourth function argument
     */
    void accept(T t, U u, V v, W w);

}
