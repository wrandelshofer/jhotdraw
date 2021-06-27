/*
 * @(#)OrderedPairNonNull.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;


import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * OrderedPair.
 * <p>
 * This is a value type.
 *
 * @author Werner Randelshofer
 */
public class OrderedPairNonNull<U, V> implements Pair<U, V> {
    private final @NonNull U a;
    private final @NonNull V b;

    public OrderedPairNonNull(@NonNull U a, @NonNull V b) {
        this.a = a;
        this.b = b;
    }

    public @NonNull U first() {
        return a;
    }

    public @NonNull V second() {
        return b;
    }

    public boolean isIntersectionEmpty() {
        return !Objects.equals(a, b);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
         final OrderedPairNonNull<?, ?> other = (OrderedPairNonNull<?, ?>) obj;
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        return Objects.equals(this.b, other.b);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.a);
        hash = 59 * hash + Objects.hashCode(this.b);
        return hash;
    }

}
