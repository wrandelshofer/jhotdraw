/*
 * @(#)CheckedSupplier.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

/**
 * A Supplier which can throw a checked exception.
 *
 * @param <T> the result type
 * @author Werner Randelshofer
 */
public interface CheckedSupplier<T> {
    @NonNull
    @Nullable
    T supply() throws Exception;
}
