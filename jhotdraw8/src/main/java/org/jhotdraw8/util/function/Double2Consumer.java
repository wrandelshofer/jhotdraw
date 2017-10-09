/* @(#)Double2Consumer.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util.function;

/**
 * Double2Consumer.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
@FunctionalInterface
public interface Double2Consumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param v1 the input argument
     * @param v2 the input argument
     */
    void accept(double v1, double v2);

}
