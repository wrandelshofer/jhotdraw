/* @(#)FigureTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.beans.property.ObjectProperty;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.styleable.AbstractStyleablePropertyBean;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author werni
 */
public class FigureTest {


    /**
     * Test of reshapeInLocal method, of class Figure.
     */
    public void testReshape_4args(Rectangle initial, Rectangle reshape, Rectangle expected) {
        System.out.println("reshape");


        Transform[] actual = new Transform[1];
        Figure instance = new FigureImpl() {
            Rectangle r = new Rectangle(initial.getX(), initial.getY(), initial.getWidth(), initial.getHeight());

            public Bounds getBoundsInLocal() {
                return r.getBoundsInLocal();
            }

            public void reshapeInLocal(Transform t) {
                actual[0] = t;
                Bounds b = t.transform(getBoundsInLocal());
                r.setX(b.getMinX());
                r.setY(b.getMinY());
                r.setWidth(b.getWidth());
                r.setHeight(b.getHeight());
            }
        };
        instance.reshapeInLocal(reshape.getX(), reshape.getY(), reshape.getWidth(), reshape.getHeight());
        System.out.println(actual[0]);
        System.out.println(instance.getBoundsInLocal());
        assertEquals(expected.getBoundsInLocal(), instance.getBoundsInLocal());
    }

    @TestFactory
    public List<DynamicTest> testdataReshape_4argsFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testReshape_4args(new Rectangle(10, 20, 30, 40), new Rectangle(10, 20, 30, 40), new Rectangle(10, 20, 30, 40))),
                dynamicTest("2", () -> testReshape_4args(new Rectangle(10, 20, 30, 40), new Rectangle(50, 60, 70, 80), new Rectangle(50, 60, 70, 80))),
                dynamicTest("3", () -> testReshape_4args(new Rectangle(50, 60, 70, 80), new Rectangle(10, 20, 30, 40), new Rectangle(10, 20, 30, 40)))
        );
    }

    /**
     * Test of getDeclaredAndInheritedMapAccessors method, of class Figure.
     */
    @Test
    public void testGetDeclaredAndInheritedKeys() {
        Set<MapAccessor<?>> figureKeys = Figure.getDeclaredAndInheritedMapAccessors(Figure.class);
        Set<MapAccessor<?>> rectangleFigureKeys = Figure.getDeclaredAndInheritedMapAccessors(SimpleRectangleFigure.class);
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

        public ObjectProperty<Figure> parentProperty() {
            return null;
        }

        public Bounds getBoundsInLocal() {
            return null;
        }

        public void reshapeInLocal(Transform transform) {
        }

        public Node createNode(RenderContext ctx) {
            return null;
        }

        public void updateNode(RenderContext ctx, Node node) {
        }

        public boolean isAllowsChildren() {
            return false;
        }

        public boolean isSuitableParent(Figure newParent) {
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

        public Connector findConnector(Point2D pointInLocal, Figure prototype) {
            return null;
        }

        public void updateCss() {
        }

        public void addNotify(Drawing drawing) {
        }

        public void removeNotify(Drawing drawing) {
        }

        public ObservableList<Figure> getChildren() {
            return null;
        }

        public Set<Figure> getLayoutObservers() {
            return null;
        }

        public Transform getParentToLocal() {
            return null;
        }

        public Transform getLocalToParent() {
            return null;
        }

        public Transform getWorldToLocal() {
            return null;
        }

        public Transform getWorldToParent() {
            return null;
        }

        public Transform getLocalToWorld() {
            return null;
        }

        public Transform getParentToWorld() {
            return null;
        }

        public boolean invalidateTransforms() {
            return false;
        }

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
        public <T> T getStyled(MapAccessor<T> key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T setStyled(StyleOrigin origin, MapAccessor<T> key, T value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T remove(StyleOrigin origin, MapAccessor<T> key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeAll(StyleOrigin origin) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTypeSelector() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ObservableList<String> getStyleClass() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getStyle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Styleable getStyleableParent() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ObservableSet<PseudoClass> getPseudoClassStates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Figure getParent() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void reshapeInParent(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void transformInParent(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void transformInLocal(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
