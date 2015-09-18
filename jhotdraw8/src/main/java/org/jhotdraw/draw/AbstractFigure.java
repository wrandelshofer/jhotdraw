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

    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<Figure>(this, PARENT_PROPERTY);

    @Override
    public ObjectProperty<Figure> parentProperty() {
        return parent;
    }

    /** This implementation always returns true. */
    @Override
    public boolean isSelectable() {
        return true;
    }
}
