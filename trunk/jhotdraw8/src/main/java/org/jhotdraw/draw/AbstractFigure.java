/* @(#)AbstractFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static org.jhotdraw.draw.FigureKeys.*;
import java.util.ArrayList;
import java.util.Optional;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import org.jhotdraw.beans.SimplePropertyBean;

/**
 * AbstractFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends SimplePropertyBean implements Figure {
    private final ListProperty<Figure> children = new SimpleListProperty<>(this,"children",FXCollections.observableList(new ArrayList<Figure>()));
    private final ObjectProperty<Optional<Figure>> parent = new SimpleObjectProperty<Optional<Figure>>(this,"parent",Optional.empty());

    
    
    @Override
    public ListProperty<Figure> children() {
        return children;
    }

    @Override
    public ObjectProperty<Optional<Figure>> parent() {
        return parent;
    }

}
