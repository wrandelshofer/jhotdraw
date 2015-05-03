/* @(#)AbstractCompositeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw;

import java.util.ArrayList;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import static org.jhotdraw.draw.Figure.CHILDREN_PROPERTY;

/**
 * AbstractCompositeFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractCompositeFigure extends AbstractFigure {
    private final ReadOnlyListProperty<Figure> children = new ReadOnlyListWrapper<>(this,CHILDREN_PROPERTY,FXCollections.observableList(new ArrayList<Figure>())).getReadOnlyProperty();
    @Override
    public ReadOnlyListProperty<Figure> childrenProperty() {
        return children;
    }
    /** Whether children may be added to this figure. */
    public boolean allowsChildren() {
        return true;
    }

}
