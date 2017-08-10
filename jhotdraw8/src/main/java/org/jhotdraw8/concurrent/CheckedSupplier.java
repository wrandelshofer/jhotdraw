/* @(#)CheckedSupplier.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

/**
 * A Supplier which can throw a checked exception.
 *
 * @param <T> the result type
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CheckedSupplier<T> {

    T supply() throws Exception;
}
