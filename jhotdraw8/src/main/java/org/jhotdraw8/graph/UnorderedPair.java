/* @(#)UnorderedPair.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.graph.Pair;
import java.util.Objects;

/**
 * UnorderedPair.
 * <p>
 * This is a value type.
 *
 * @param <V> the type of the items that form the pair
 * @author Werner Randelshofer
 * @version $Id$
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

    public boolean isIntersectionEmpty() {
        return !Objects.equals(a, b);
    }

    @Override
    public boolean equals(Object obj) {
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
        if (Objects.equals(this.b, other.a) && Objects.equals(this.a, other.b)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 13
                + a.hashCode()
                + b.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "UnorderedPair{" + "a=" + a + ", b=" + b + '}';
    }

}
