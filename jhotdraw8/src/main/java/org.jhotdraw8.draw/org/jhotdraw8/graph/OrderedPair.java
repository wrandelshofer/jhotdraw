/*
 * @(#)OrderedPair.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;


import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * OrderedPair.
 * <p>
 * This is a value type.
 *
 * @author Werner Randelshofer
 */
class OrderedPair<V> implements Pair<V> {

    private final V a;
    private final V b;

    public OrderedPair(V a, V b) {
        this.a = a;
        this.b = b;
    }

    public V getStart() {
        return a;
    }

    public V getEnd() {
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
        @SuppressWarnings("unchecked") final OrderedPair<V> other = (OrderedPair) obj;
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        if (!Objects.equals(this.b, other.b)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.a);
        hash = 59 * hash + Objects.hashCode(this.b);
        return hash;
    }

}
