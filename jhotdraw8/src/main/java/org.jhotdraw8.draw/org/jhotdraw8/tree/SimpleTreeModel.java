/*
 * @(#)SimpleTreeModel.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.tree;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.event.Listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SimpleTreeModel.
 *
 * @author Werner Randelshofer
 */
public class SimpleTreeModel<E> implements TreeModel<E> {

    @NonNull
    @Override
    public E getChild(E parent, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getChildCount(E node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @NonNull
    @Override
    public List<E> getChildren(E node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @NonNull
    @Override
    public CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @NonNull
    @Override
    public CopyOnWriteArrayList<Listener<TreeModelEvent<E>>> getTreeModelListeners() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertChildAt(E child, E parent, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFromParent(E child) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public @NonNull ObjectProperty<E> rootProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
