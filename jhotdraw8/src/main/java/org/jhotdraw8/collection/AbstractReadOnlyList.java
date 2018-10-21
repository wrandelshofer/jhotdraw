package org.jhotdraw8.collection;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractReadOnlyList<E> implements ReadOnlyList<E> {

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ReadOnlyList))
            return false;

        Iterator<E> e1 = iterator();
        Iterator<?> e2 = ((Iterable<?>) o).iterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this)
            hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }
}
