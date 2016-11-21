/* @(#)FigureNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.util.HashSet;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FigurePropertyChangeEvent;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.Drawing;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.Layer;
import org.jhotdraw8.draw.RenderContext;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.event.Listener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.styleable.AbstractStyleablePropertyBean;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;

/**
 *
 * @author werni
 */
public class FigureNGTest {

    public FigureNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of reshapeInLocal method, of class Figure.
     */
    @Test(dataProvider="dataReshape_4args")
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
                Bounds b=t.transform(getBoundsInLocal());
                r.setX(b.getMinX());
                r.setY(b.getMinY());
                r.setWidth(b.getWidth());
                r.setHeight(b.getHeight());
            }
        };
        instance.reshape(reshape.getX(), reshape.getY(), reshape.getWidth(), reshape.getHeight());
        System.out.println(actual[0]);
        System.out.println(instance.getBoundsInLocal());
        assertEquals(expected.getBoundsInLocal(),instance.getBoundsInLocal());
    }

    @DataProvider
    private static Object[][] dataReshape_4args() {
       return new Object[][] {
           {new Rectangle(10,20,30,40),new Rectangle(10,20,30,40),new Rectangle(10,20,30,40)},
           {new Rectangle(10,20,30,40),new Rectangle(50,60,70,80),new Rectangle(50,60,70,80)},
           {new Rectangle(50,60,70,80),new Rectangle(10,20,30,40),new Rectangle(10,20,30,40)} ,
       } ;
    }
    
    /**
     * Test of getDeclaredAndInheritedKeys method, of class Figure.
     */
    @Test
    public void testGetDeclaredAndInheritedKeys() {
        Set<MapAccessor<?>> figureKeys = Figure.getDeclaredAndInheritedKeys(Figure.class);
        Set<MapAccessor<?>> rectangleFigureKeys = Figure.getDeclaredAndInheritedKeys(RectangleFigure.class);
        System.out.println("rr:" + rectangleFigureKeys);
        Set<MapAccessor<?>> intersection = new HashSet<>(figureKeys);
        intersection.retainAll(rectangleFigureKeys);
        System.out.println("ri:" + intersection);
        assertEquals(figureKeys, intersection);
    }

    public class FigureImpl extends AbstractStyleablePropertyBean implements Figure {

        public void removeConnectionTarget(Figure targetFigure) {
        }

        public void removeAllConnectionTargets() {
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

        public boolean isLayoutable() {
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

        public void layout() {
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

        public Set<Figure> getDependentFigures() {
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
    }

}
