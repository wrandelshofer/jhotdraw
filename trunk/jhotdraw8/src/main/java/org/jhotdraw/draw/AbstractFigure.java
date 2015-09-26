/* @(#)AbstractFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import org.jhotdraw.beans.SimplePropertyBean;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.css.AbstractStyleablePropertyBean;
import org.jhotdraw.draw.css.StyleableKey;
import static org.jhotdraw.draw.Figure.CHILDREN_PROPERTY;
import org.jhotdraw.draw.css.StyleableStyleManager;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends AbstractStyleablePropertyBean implements Figure {

    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<Figure>(this, PARENT_PROPERTY) {

        @Override
        public void set(Figure newValue) {
            checkParent(newValue);
            super.set(newValue);
        }

    };
    private ReadOnlySetProperty<Figure> connections = new ReadOnlySetWrapper<>(this, CONNECTIONS_PROPERTY, FXCollections.observableSet(new HashSet<Figure>())).getReadOnlyProperty();

    private ObservableSet<PseudoClass> pseudoClassStates = FXCollections.observableSet(new HashSet<>());

    @Override
    public final ReadOnlySetProperty<Figure> connectionsProperty() {
        return connections;
    }

    @Override
    public ObjectProperty<Figure> parentProperty() {
        return parent;
    }

    /**
     * This implementation always returns true.
     */
    @Override
    public boolean isSelectable() {
        return true;
    }

    /**
     * This method should throw an illegal argument exception if the provided
     * figure is not a suitable parent for this figure.
     * <p>
     * This implementation fires an illegal argument exception if the parent is
     * an instanceof {@code Drawing}.
     *
     * @param newParent The new parent figure.
     * @throws IllegalArgumentException if newParent is an illegal parent
     */
    protected void checkParent(Figure newParent) {
        if (newParent instanceof Drawing) {
            throw new IllegalArgumentException("illegal parent:" + newParent);
        }
    }

    @Override
    public ObservableSet<PseudoClass> getPseudoClassStates() {
        return pseudoClassStates;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>();
        for (Key<?> key : Figure.getSupportedKeys(this)) {
            if (key instanceof StyleableKey) {
                StyleableKey sk = (StyleableKey) key;

                list.add(sk.createCssMetaData());
            }
        }
        return list;
    }

    @Override
    public void applyCss() {
        styleableProperties.clearNonUserProperties();
        Drawing d = getDrawing();
        if (d != null) {
            StyleableStyleManager styleManager = d.getStyleManager();
            styleManager.applyStylesTo(this);
            for (Figure child : children()) {
                child.applyCss();
            }
        }

    }
}
