/* @(#)ListWrapper.java
 * Copyright © 2018 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.AbstractList;

public class ListWrapper<E> extends AbstractList<E> {
    private final ReadableList<E> backingList;

    public ListWrapper(ReadableList<E> backingList) {
        this.backingList = backingList;
    }

    @Override
    public E get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }
}
