/* @(#)ReadableListIterator.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Iterator;

public class ReadableListIterator<E> implements Iterator<E> {
    private final ReadableList<E> list;
    int index = 0;
    final int size;

    public ReadableListIterator(ReadableList<E> list) {
        this.list = list;
        this.size = list.size();
    }

    @Override
    public boolean hasNext() {
        return index < size;
    }

    @Override
    public E next() {
        return list.get(index++);
    }

}
