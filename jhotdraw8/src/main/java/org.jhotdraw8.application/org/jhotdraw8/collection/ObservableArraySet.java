/*
 * @(#)ObservableArraySet.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.ArrayList;

public class ObservableArraySet<E> extends ArrayList<E> implements ObservableSet<E> {
    private final static long serialVersionUID = 1L;

    @Override
    public void addListener(SetChangeListener<? super E> listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(SetChangeListener<? super E> listener) {
        throw new UnsupportedOperationException();
    }
}
