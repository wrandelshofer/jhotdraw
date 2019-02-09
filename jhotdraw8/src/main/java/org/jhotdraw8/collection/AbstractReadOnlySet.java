/* @(#)AbstractReadOnlySet.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

public abstract class AbstractReadOnlySet<E> implements ReadOnlySet<E> {
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ReadOnlySet)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        ReadOnlyCollection<E> c = (ReadOnlyCollection<E>) o;
        if (c.size() != size())
            return false;
        try {
            return containsAll(c);
        } catch (ClassCastException | NullPointerException unused)   {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this) {
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }
}
