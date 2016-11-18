/* @(#)CheckedSupplier.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.concurrent;

/**
 * A Supplier which can throw a checked exception.
 *
 * @param <T> the result type
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface CheckedSupplier<T> {
    T supply() throws Exception;
}
