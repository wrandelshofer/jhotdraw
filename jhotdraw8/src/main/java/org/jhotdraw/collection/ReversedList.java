/* @(#)ReversedList.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.collection;

import java.util.LinkedList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

/**
 * Reversed list provides a view on an underlying list with items ordered in
 * reverse.
 *
 * @author Werner Randelshofer
 */
public class ReversedList<E> extends TransformationList<E, E> {

    private int size;

    public ReversedList(ObservableList<E> source) {
        super(source);
        size = source.size();
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends E> c) {
        beginChange();
        while (c.next()) {
            if (c.wasPermutated()) {
                int from=c.getFrom();
                int[] perm = new int[c.getTo() - from];
                for (int i = from; i < c.getTo(); i++) {
                    perm[i - from] = size - c.getPermutation(i);
                }
                nextPermutation(size -1 - c.getTo(), size -1 - c.getFrom(), perm);
            } else if (c.wasUpdated()) {
                //update item
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    nextUpdate(size - 1 - i);
                }
            } else {
                if (c.wasRemoved()) {
                    LinkedList<E> reversed = new LinkedList<>();
                    for (E e : c.getRemoved()) {
                        reversed.addFirst(e);
                    }
                    size = size - c.getRemovedSize();
                    // c.getTo() is not always correctly filled in by the underlying list!
                    nextRemove(size - c.getFrom()-c.getRemovedSize()+1, reversed);
                }
                if (c.wasAdded()) {
                    size = size + c.getAddedSize();
                    nextAdd(size-c.getTo(), size-c.getFrom());

                }
            }
        }
        assert size == getSource().size() : "ReversedObservableList this.size=" + this.size + " sourceSize:" + getSource().size();
        endChange();
    }

    @Override
    public int getSourceIndex(int index) {
        return size - 1 - index;
    }

    @Override
    public E get(int index) {
        return getSource().get(getSourceIndex(index));
    }
    @Override
    public E set(int index, E e) {
        @SuppressWarnings("unchecked")
        List<E> tmp=((List<E>)getSource());
        return tmp.set(getSourceIndex(index), e);
    }

    @Override
    public E remove(int index) {
        return getSource().remove(getSourceIndex(index));
    }
    @Override
    public boolean remove(Object o) {
        return getSource().remove(o);
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E element) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        src.add(size-index, element);
    }

}
