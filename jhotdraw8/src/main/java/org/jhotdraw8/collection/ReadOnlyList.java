package org.jhotdraw8.collection;



import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface ReadOnlyList<E> extends ReadOnlyCollection<E> {

    E get(int index);

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Nonnull
    @Override
    default Iterator<E> iterator() {
        return new ReadOnlyListIterator<>(this);
    }


    default List<E> toList() {
        return new ArrayList<>(new ListWrapper<>(this));
    }
}
