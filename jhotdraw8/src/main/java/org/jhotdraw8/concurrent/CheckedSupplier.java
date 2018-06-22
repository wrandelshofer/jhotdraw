/* @(#)CheckedSupplier.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A Supplier which can throw a checked exception.
 *
 * @param <T> the result type
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CheckedSupplier<T> {
@NonNull
@Nullable
    T supply() throws Exception;
}
