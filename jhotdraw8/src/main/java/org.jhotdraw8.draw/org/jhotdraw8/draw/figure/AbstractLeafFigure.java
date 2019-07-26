/*
 * @(#)AbstractLeafFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This base class can be used to implement figures which do not support child
 * figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Composite, Leaf.
 */
public abstract class AbstractLeafFigure extends AbstractFigure {

    @Override
    public final ObservableList<Figure> getChildren() {
        return FXCollections.emptyObservableList();
    }

    @Override
    public final boolean isAllowsChildren() {
        return false;
    }

}
