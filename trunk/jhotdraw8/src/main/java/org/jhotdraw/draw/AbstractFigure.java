/* @(#)AbstractFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.ArrayList;
import java.util.Optional;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.beans.SimplePropertyBean;

/**
 * AbstractFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends SimplePropertyBean implements Figure {

    private final OptionalProperty<Figure> parent = new OptionalProperty<Figure>(this, PARENT_PROPERTY);

    @Override
    public OptionalProperty<Figure> parentProperty() {
        return parent;
    }

    /** This implementation always returns true. */
    @Override
    public boolean isSelectable() {
        return true;
    }

}
