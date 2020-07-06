/*
 * @(#)NonNullKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

public interface NonNullKey<@NonNull T> extends Key<@NonNull T>, NonNullMapAccessor<@NonNull T> {
}
