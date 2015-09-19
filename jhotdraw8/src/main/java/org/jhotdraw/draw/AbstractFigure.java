/* @(#)AbstractFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jhotdraw.beans.SimplePropertyBean;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends SimplePropertyBean implements Figure {

    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<Figure>(this, PARENT_PROPERTY) {

        @Override
        public void set(Figure newValue) {
            checkNewParent(newValue);
            super.set(newValue);
        }
    };

    @Override
    public ObjectProperty<Figure> parentProperty() {
        return parent;
    }

    /** This implementation always returns true. */
    @Override
    public boolean isSelectable() {
        return true;
    }

    /** Subclasses can override this method to check whether they are added to a
     * legal parent. This method throws an illegal argument exception if the new
     * parent is an instance of Drawing. */
    protected void checkNewParent(Figure newValue) {
        if (newValue instanceof Drawing) {
            throw new IllegalArgumentException("This figure can not be directly added to a Drawing. Illegal parent: "+newValue);
        }
    }
}
