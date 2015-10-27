/* @(#)ReversedObservableList.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.TransformationList;

/**
 * Reversed list provides a view on an underlying list with items ordered in
 * reverse.
 *
 * @author Werner Randelshofer
 */
public class ReversedObservableList<E> extends TransformationList<E, E> {

    private int size;

    public ReversedObservableList(ObservableList<E> source) {
        super(source);
        size = source.size();
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends E> c) {
        beginChange();
        while (c.next()) {
            if (c.wasPermutated()) {
                int[] perm = new int[c.getTo() - c.getFrom()];
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    perm[i - c.getFrom()] = size - c.getPermutation(i) - 1;
                }
                nextPermutation(size - c.getTo() - 2, size - c.getFrom(), perm);
            } else if (c.wasUpdated()) {
                //update item
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    nextUpdate(size - i - 1);
                }
            } else {
                if (c.wasRemoved()) {
                    LinkedList<E> reversed = new LinkedList<E>();
                    for (E e : c.getRemoved()) {
                        reversed.addFirst(e);
                    }
                    nextRemove(size - c.getTo() - 2, reversed);
                    size = size - c.getRemovedSize();
                }
                if (c.wasAdded()) {
                    size = size + c.getAddedSize();
                    nextAdd(size - c.getTo() - 2, size - c.getFrom());

                }
            }
        }
        assert size == getSource().size();
        endChange();
    }

    @Override
    public int getSourceIndex(int index) {
        return size() - index - 1;
    }

    @Override
    public E get(int index) {
        return getSource().get(getSourceIndex(index));
    }

    @Override
    public int size() {
        return getSource().size();
    }

}
