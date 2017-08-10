/* @(#)UnorderedPair.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.Objects;

/**
 * UnorderedPair.
 * <p>
 * This is a value type.
 *
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
        @SuppressWarnings("unchecked")
        final UnorderedPair<V> other = (UnorderedPair) obj;
        if (!Objects.equals(this.a, other.a) && !Objects.equals(this.a, other.b)) {
            return false;
        }
        if (!Objects.equals(this.b, other.b) && !Objects.equals(this.b, other.a)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hashCode(this.a) +  Objects.hashCode(this.b);
        return hash;
    }

}
