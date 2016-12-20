/*
 * @(#)AbstractLeafFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This base class can be used to implement figures which do not support child
 * figures.
 *
 * @design.pattern Figure Composite, Leaf.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
