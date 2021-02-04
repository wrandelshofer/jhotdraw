/* @(#)FigureTest.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.styleable.AbstractStyleablePropertyBean;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author werni
 */
public class FigureTest {


    /**
     * Test of getDeclaredAndInheritedMapAccessors method, of class Figure.
     */
    @Test
    public void testGetDeclaredAndInheritedKeys() {
        Set<MapAccessor<?>> figureKeys = Figure.getDeclaredAndInheritedMapAccessors(Figure.class);
        Set<MapAccessor<?>> rectangleFigureKeys = Figure.getDeclaredAndInheritedMapAccessors(RectangleFigure.class);
        System.out.println("rr:" + rectangleFigureKeys);
        Set<MapAccessor<?>> intersection = new HashSet<>(figureKeys);
        intersection.retainAll(rectangleFigureKeys);
        System.out.println("ri:" + intersection);
        assertEquals(figureKeys, intersection);
    }

    public class FigureImpl extends AbstractStyleablePropertyBean implements Figure {

        public void removeLayoutSubject(Figure targetFigure) {
        }

        public void removeAllLayoutSubjects() {
        }

        public @NonNull ObjectProperty<Figure> parentProperty() {
            return null;
        }

        @NonNull
        public Bounds getLayoutBounds() {
            return null;
        }

        @NonNull
        public CssRectangle2D getCssLayoutBounds() {
            return new CssRectangle2D(getLayoutBounds());
        }

        public void reshapeInLocal(Transform transform) {
        }

        @Override
        public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
            // empty
        }

        @Nullable
        public Node createNode(RenderContext ctx) {
            return null;
        }

        public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        }

        public boolean isAllowsChildren() {
            return false;
        }

        public boolean isSuitableParent(@NonNull Figure newParent) {
            return false;
        }

        @Override
        public boolean isSuitableChild(@NonNull Figure newChild) {
            return false;
        }

        public boolean isSelectable() {
            return false;
        }

        public boolean isDeletable() {
            return false;
        }

        public boolean isEditable() {
            return false;
        }

        @Nullable
        public Connector findConnector(Point2D pointInLocal, Figure prototype) {
            return null;
        }

        public void updateCss(RenderContext ctx) {
        }

        public void addNotify(Drawing drawing) {
        }

        public void removeNotify(Drawing drawing) {
        }

        public @NonNull ObservableList<Figure> getChildren() {
            return FXCollections.emptyObservableList();
        }

        @Nullable
        public ObservableSet<Figure> getLayoutObservers() {
            return null;
        }

        public @NonNull Transform getParentToLocal() {
            return FXTransforms.IDENTITY;
        }

        @Override
        public @NonNull Bounds getBoundsInLocal() {
            return getLayoutBounds();
        }

        public @NonNull Transform getLocalToParent() {
            return FXTransforms.IDENTITY;
        }

        public @NonNull Transform getWorldToLocal() {
            return FXTransforms.IDENTITY;
        }

        public @NonNull Transform getWorldToParent() {
            return FXTransforms.IDENTITY;
        }

        public @NonNull Transform getLocalToWorld() {
            return FXTransforms.IDENTITY;
        }

        public @NonNull Transform getParentToWorld() {
            return FXTransforms.IDENTITY;
        }

        public void invalidateTransforms() {

        }

        @Nullable
        public CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> getPropertyChangeListeners() {
            return null;
        }

        public boolean hasPropertyChangeListeners() {
            return false;
        }

        @Override
        public <T> StyleableProperty<T> getStyleableProperty(MapAccessor<T> key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T getStyled(@NonNull MapAccessor<T> key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T setStyled(@NonNull StyleOrigin origin, @NonNull MapAccessor<T> key, T value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T remove(@NonNull StyleOrigin origin, @NonNull MapAccessor<T> key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeAll(@NonNull StyleOrigin origin) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @NonNull
        @Override
        public String getTypeSelector() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @NonNull
        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @NonNull
        @Override
        public ObservableList<String> getStyleClass() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @NonNull
        @Override
        public String getStyle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @NonNull
        @Override
        public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @NonNull
        @Override
        public Styleable getStyleableParent() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @NonNull
        @Override
        public ObservableSet<PseudoClass> getPseudoClassStates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ObservableSet<String> getPseudoClass() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Figure getParent() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void reshapeInParent(@NonNull Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void transformInParent(@NonNull Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void transformInLocal(@NonNull Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
