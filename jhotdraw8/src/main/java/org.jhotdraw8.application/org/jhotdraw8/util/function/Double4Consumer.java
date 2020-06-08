/*
 * @(#)Double4Consumer.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.util.function;

/**
 * Double4Consumer.
 *
 * @author Werner Randelshofer
 */
@FunctionalInterface
public interface Double4Consumer {
    /**
     * Performs this operation on the given argument.
     *
     * @param v1 the input argument
     * @param v2 the input argument
     * @param v3 the input argument
     * @param v4 the input argument
     */
    void accept(double v1, double v2, double v3, double v4);

}
