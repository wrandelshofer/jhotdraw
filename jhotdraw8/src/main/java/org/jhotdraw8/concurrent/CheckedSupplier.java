/* @(#)CheckedSupplier.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.annotation.Nonnull;

/**
 * A Supplier which can throw a checked exception.
 *
 * @param <T> the result type
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CheckedSupplier<T> {
@Nonnull
@Nullable
    T supply() throws Exception;
}
