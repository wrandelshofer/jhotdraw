/*
 * @(#)ExpandedTreeItemIterator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import javafx.scene.control.TreeItem;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

/**
 * Iterates in preorder sequence over all expanded tree items.
 *
 * @param <T> The type of the value property within TreeItem.
 * @author Werner Randelshofer
 */
public class ExpandedTreeItemIterator<T> implements Iterator<TreeItem<T>> {

    private final Deque<Iterator<TreeItem<T>>> stack = new ArrayDeque<>(16);

    public ExpandedTreeItemIterator(TreeItem<T> root) {
        stack.push(Collections.singleton(root).iterator());
    }

    @Override
    public boolean hasNext() {
        return (!stack.isEmpty() && stack.peek().hasNext());
    }

    @Override
    public TreeItem<T> next() {
        Iterator<TreeItem<T>> iter = stack.peek();
        TreeItem<T> node = iter.next();

        if (!iter.hasNext()) {
            stack.pop();
        }
        if (node.isExpanded()) {
            Iterator<TreeItem<T>> children = node.getChildren().iterator();
            if (children.hasNext()) {
                stack.push(children);
            }
        }
        return node;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
