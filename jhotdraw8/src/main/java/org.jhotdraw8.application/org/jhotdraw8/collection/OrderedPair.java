/*
 * @(#)OrderedPair.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;


import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * OrderedPair.
 * <p>
 * This is a value type.
 *
 * @author Werner Randelshofer
 */
public class OrderedPair<U, V> implements Pair<U, V> {

    private final U a;
    private final V b;
    /**
     * Cached hash-value for faster hashing.
     */
    private int hash;

    public OrderedPair(U a, V b) {
        this.a = a;
        this.b = b;
    }

    public U first() {
        return a;
    }

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
        @SuppressWarnings("unchecked") final OrderedPair<U, V> other = (OrderedPair) obj;
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        return Objects.equals(this.b, other.b);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = 3;
            hash = 59 * hash + Objects.hashCode(this.a);
            hash = 59 * hash + Objects.hashCode(this.b);
        }
        return hash;
    }

    @Override
    public String toString() {
        return "OrderedPair{"
                + a +
                ", " + b +
                '}';
    }
}
