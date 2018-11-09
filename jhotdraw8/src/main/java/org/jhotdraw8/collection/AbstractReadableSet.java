/* @(#)AbstractReadableSet.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractReadableSet<E> implements ReadableSet<E> {
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ReadableSet)) {
            return false;
        }

        ReadableCollection<?> c = (ReadableCollection<?>) o;
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
