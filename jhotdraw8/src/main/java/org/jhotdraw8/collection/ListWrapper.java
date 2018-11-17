/* @(#)ListWrapper.java
 * Copyright © 2018 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.AbstractList;
import java.util.List;

/**
 * Wraps a {@link ReadableList} in the {@link List} API.
 * <p>
 * The underlying ReadableList is referenced - not copied. This allows to pass a
 * ReadableList to a client who does not understand the ReadableList APi.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
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
