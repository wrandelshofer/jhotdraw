/* @(#)AbstractFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.css.SimpleStyleablePropertyBean;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleableStyleManager;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.FigureKey;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends SimpleStyleablePropertyBean implements Figure {

    private Transform parentToDrawing;
    private Transform drawingToParent;
    private Transform localToDrawing;
    private Transform drawingToLocal;
    private Transform localToParent;
    private Transform parentToLocal;

    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<Figure>(this, PARENT_PROPERTY) {

        @Override
        protected void fireValueChangedEvent() {
            if (!isSuitableParent(get())) {
                throw new IllegalArgumentException(get()+" is not a suitable parent for this figure.");
            }
            super.fireValueChangedEvent();
        }

    };
    private ReadOnlySetProperty<Figure> connectedFigures = new ReadOnlySetWrapper<>(this, CONNECTED_FIGURES_PROPERTY, FXCollections.observableSet(new HashSet<Figure>())).getReadOnlyProperty();

    @Override
    public final ReadOnlySetProperty<Figure> connectedFiguresProperty() {
        return connectedFigures;
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
     * This method whether the provided figure is a suitable parent for this
     * figure.
     * <p>
     * This implementation returns false if {@code newParent} is a
     * {@link Drawing}. Because only {@link Layer}s may have {@code Drawing} as
     * a parent.
     *
     * @param newParent The new parent figure.
     * @return true if {@code newParent} is an acceptable parent
     */
    @Override
    public boolean isSuitableParent(Figure newParent) {
        return newParent != null && !(newParent instanceof Drawing);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>();
        for (Key<?> key : Figure.getSupportedKeys(this)) {
            if (key instanceof StyleableKey<?>) {
                StyleableKey<?> sk = (StyleableKey<?>) key;

                CssMetaData<? extends Styleable, ?> md = sk.getCssMetaData();
                list.add(md);
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
            for (Figure child : getChildren()) {
                child.applyCss();
            }
        }
        invalidateTransforms();
    }

    @Override
    public String toString() {
        String className = getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1);
        StringBuilder buf = new StringBuilder();
        buf.append(className).append('@')//
                .append(Integer.toHexString(hashCode()))//
                .append('{');//
        boolean isFirst = true;
        for (Map.Entry<Key<?>, Object> e : getProperties().entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                buf.append(',');
            }
            buf.append(e.getKey());
            buf.append('=');
            if (e.getValue() instanceof Figure) {
                Figure f = (Figure) e.getValue();
                className = f.getClass().getName();
                className = className.substring(className.lastIndexOf('.') + 1);
                buf.append(className).append('@').append(f.hashCode());
            } else {
                buf.append(e.getValue());
            }
        }
        buf.append(", connections={");
        isFirst = true;
        for (Figure f : connectedFigures) {
            if (isFirst) {
                isFirst = false;
            } else {
                buf.append(',');
            }
            className = f.getClass().getName();
            className = className.substring(className.lastIndexOf('.') + 1);
            buf.append(className).append('@').append(f.hashCode());
        }
        buf.append("}}");
        return buf.toString();
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void removeConnectionTarget(Figure connectedFigure) {
        // empty
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void removeAllConnectionTargets() {
        // empty
    }

    /**
     * This implementation is empty.
     *
     * @param drawing the drawing to which this figure has been added
     */
    @Override
    public void removeNotify(Drawing drawing) {
    }

    /**
     * This implementation is empty.
     *
     * @param drawing the drawing from which this figure has been removed
     */
    @Override
    public void addNotify(Drawing drawing) {
    }

    /**
     * Calls {@link #invalidateTransforms()}.
     */
    public void transformNotify() {
        invalidateTransforms();
    }

    /**
     * Clears all cached transformation matrices.
     */
    public void invalidateTransforms() {
        parentToDrawing
                = drawingToParent
                = localToDrawing
                = drawingToLocal
                = localToParent
                = parentToLocal = null;
    }

    /**
     * Warning: this method caches the transformation.
     *
     * @return transform
     */
    @Override
    public Transform getParentToDrawing() {
        if (parentToDrawing == null) {
            parentToDrawing = Figure.super.getParentToDrawing();
        }
        return parentToDrawing;
    }

    /**
     * Warning: this method caches the transformation.
     *
     * @return transform
     */
    @Override
    public Transform getLocalToDrawing() {
        if (localToDrawing == null) {
            localToDrawing = Figure.super.getLocalToDrawing();
        }
        return localToDrawing;
    }

    /**
     * Warning: this method caches the transformation.
     *
     * @return transform
     */
    @Override
    public Transform getDrawingToParent() {
        if (drawingToParent == null) {
            drawingToParent = Figure.super.getDrawingToParent();
        }
        return drawingToParent;
    }

    /**
     * Warning: this method caches the transformation.
     *
     * @return transform
     */
    @Override
    public Transform getDrawingToLocal() {
        if (drawingToLocal == null) {
            drawingToLocal = Figure.super.getDrawingToLocal();
        }
        return drawingToLocal;
    }

    /**
     * Warning: this method caches the transformation.
     *
     * @return transform
     */
    @Override
    public Transform getLocalToParent() {
        if (localToParent == null) {
            localToParent = Figure.super.getLocalToParent();
        }
        return localToParent;
    }

    /**
     * Warning: this method caches the transformation.
     *
     * @return transform
     */
    @Override
    public Transform getParentToLocal() {
        if (parentToLocal == null) {
            parentToLocal = Figure.super.getParentToLocal();
        }
        return parentToLocal;
    }

}
