/*
 * @(#)AbstractLeafFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;

/**
 * This base class can be used to implement figures which do not support child
 * figures.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Composite, Leaf.
 */
public abstract class AbstractLeafFigure extends AbstractFigure {

    @Override
    public final @NonNull ObservableList<Figure> getChildren() {
        return FXCollections.emptyObservableList();
    }

    /**
     * This method returns false.
     *
     * @return false
     */
    @Override
    public final boolean isAllowsChildren() {
        return false;
    }

    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return true;
    }

    /**
     * This method returns false for all children.
     *
     * @param newChild The new child figure.
     * @return false
     */
    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return false;
    }
}
