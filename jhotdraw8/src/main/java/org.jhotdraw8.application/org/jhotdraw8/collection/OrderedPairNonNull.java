/*
 * @(#)OrderedPair.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
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
    @NonNull
    private final U a;
    @NonNull
    private final V b;

    public OrderedPairNonNull(@NonNull U a, @NonNull V b) {
        this.a = a;
        this.b = b;
    }

    public @NonNull U first() {
        return a;
    }

    @NonNull
    public V second() {
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
        @SuppressWarnings("unchecked") final OrderedPairNonNull<U, V> other = (OrderedPairNonNull) obj;
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
