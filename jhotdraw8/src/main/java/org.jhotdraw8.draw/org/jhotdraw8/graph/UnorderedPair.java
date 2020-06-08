/*
 * @(#)UnorderedPair.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * UnorderedPair.
 * <p>
 * This is a value type.
 *
 * @param <V> the type of the items that form the pair
 * @author Werner Randelshofer
 */
public class UnorderedPair<V> implements Pair<V> {

    private final V a;
    private final V b;

    public UnorderedPair(V a, V b) {
        this.a = a;
        this.b = b;
    }

    public V getStart() {
        return a;
    }

    public V getEnd() {
        return b;
    }

    public boolean isStartEqualsEnd() {
        return Objects.equals(a, b);
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
        @SuppressWarnings("unchecked") final UnorderedPair<V> other = (UnorderedPair) obj;
        if (Objects.equals(this.a, other.a) && Objects.equals(this.b, other.b)) {
            return true;
        }
        return Objects.equals(this.b, other.a) && Objects.equals(this.a, other.b);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(a) + Objects.hashCode(b);
    }

    @NonNull
    @Override
    public String toString() {
        return "UnorderedPair{" + "a=" + a + ", b=" + b + '}';
    }

}
