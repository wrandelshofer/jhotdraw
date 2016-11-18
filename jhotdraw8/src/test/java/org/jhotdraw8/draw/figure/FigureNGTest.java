/* @(#)FigureNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

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
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.styleable.SimpleStyleablePropertyBean;
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
     * Test of removeConnectionTarget method, of class Figure.
     */
    @Test
    public void testRemoveConnectionTarget() {
        System.out.println("removeConnectionTarget");
        Figure targetFigure = null;
        Figure instance = new FigureImpl();
        instance.removeConnectionTarget(targetFigure);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeAllConnectionTargets method, of class Figure.
     */
    @Test
    public void testRemoveAllConnectionTargets() {
        System.out.println("removeAllConnectionTargets");
        Figure instance = new FigureImpl();
        instance.removeAllConnectionTargets();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dependencyNotify method, of class Figure.
     */
    @Test
    public void testDependencyNotify() {
        System.out.println("dependencyNotify");
        Figure instance = new FigureImpl();
        instance.dependencyNotify();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transformNotify method, of class Figure.
     */
    @Test
    public void testTransformNotify() {
        System.out.println("transformNotify");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.transformNotify();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of layoutNotify method, of class Figure.
     */
    @Test
    public void testLayoutNotify() {
        System.out.println("layoutNotify");
        Figure instance = new FigureImpl();
        instance.layoutNotify();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stylesheetNotify method, of class Figure.
     */
    @Test
    public void testStylesheetNotify() {
        System.out.println("stylesheetNotify");
        Figure instance = new FigureImpl();
        instance.stylesheetNotify();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parentProperty method, of class Figure.
     */
    @Test
    public void testParentProperty() {
        System.out.println("parentProperty");
        Figure instance = new FigureImpl();
        ObjectProperty<Figure> expResult = null;
        ObjectProperty<Figure> result = instance.parentProperty();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBoundsInLocal method, of class Figure.
     */
    @Test
    public void testGetBoundsInLocal() {
        System.out.println("getBoundsInLocal");
        Figure instance = new FigureImpl();
        Bounds expResult = null;
        Bounds result = instance.getBoundsInLocal();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBoundsInParent method, of class Figure.
     */
    @Test
    public void testGetBoundsInParent() {
        System.out.println("getBoundsInParent");
        Figure instance = new FigureImpl();
        Bounds expResult = null;
        Bounds result = instance.getBoundsInParent();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reshape method, of class Figure.
     */
    @Test
    public void testReshape_Transform() {
        System.out.println("reshape");
        Transform transform = null;
        Figure instance = new FigureImpl();
        instance.reshape(transform);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reshape method, of class Figure.
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

            public void reshape(Transform t) {
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
     * Test of createNode method, of class Figure.
     */
    @Test
    public void testCreateNode() {
        System.out.println("createNode");
        RenderContext ctx = null;
        Figure instance = new FigureImpl();
        Node expResult = null;
        Node result = instance.createNode(ctx);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateNode method, of class Figure.
     */
    @Test
    public void testUpdateNode() {
        System.out.println("updateNode");
        RenderContext ctx = null;
        Node node = null;
        Figure instance = new FigureImpl();
        instance.updateNode(ctx, node);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAllowsChildren method, of class Figure.
     */
    @Test
    public void testIsAllowsChildren() {
        System.out.println("isAllowsChildren");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isAllowsChildren();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSuitableParent method, of class Figure.
     */
    @Test
    public void testIsSuitableParent() {
        System.out.println("isSuitableParent");
        Figure newParent = null;
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isSuitableParent(newParent);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isLayoutable method, of class Figure.
     */
    @Test
    public void testIsLayoutable() {
        System.out.println("isLayoutable");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isLayoutable();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSelectable method, of class Figure.
     */
    @Test
    public void testIsSelectable() {
        System.out.println("isSelectable");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isSelectable();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDeletable method, of class Figure.
     */
    @Test
    public void testIsDeletable() {
        System.out.println("isDeletable");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isDeletable();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isGroupReshapeableWith method, of class Figure.
     */
    @Test
    public void testIsGroupReshapeableWith() {
        System.out.println("isGroupReshapeableWith");
        Set<Figure> others = null;
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isGroupReshapeableWith(others);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isEditable method, of class Figure.
     */
    @Test
    public void testIsEditable() {
        System.out.println("isEditable");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isEditable();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isVisible method, of class Figure.
     */
    @Test
    public void testIsVisible() {
        System.out.println("isVisible");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isVisible();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDecomposable method, of class Figure.
     */
    @Test
    public void testIsDecomposable() {
        System.out.println("isDecomposable");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.isDecomposable();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createHandles method, of class Figure.
     */
    @Test
    public void testCreateHandles() {
        System.out.println("createHandles");
        HandleType handleType = null;
        DrawingView dv = null;
        List<Handle> list = null;
        Figure instance = new FigureImpl();
        instance.createHandles(handleType, dv, list);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findConnector method, of class Figure.
     */
    @Test
    public void testFindConnector() {
        System.out.println("findConnector");
        Point2D pointInLocal = null;
        Figure prototype = null;
        Figure instance = new FigureImpl();
        Connector expResult = null;
        Connector result = instance.findConnector(pointInLocal, prototype);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of layout method, of class Figure.
     */
    @Test
    public void testLayout() {
        System.out.println("layout");
        Figure instance = new FigureImpl();
        instance.layout();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateCss method, of class Figure.
     */
    @Test
    public void testUpdateCss() {
        System.out.println("updateCss");
        Figure instance = new FigureImpl();
        instance.updateCss();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addNotify method, of class Figure.
     */
    @Test
    public void testAddNotify() {
        System.out.println("addNotify");
        Drawing drawing = null;
        Figure instance = new FigureImpl();
        instance.addNotify(drawing);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeNotify method, of class Figure.
     */
    @Test
    public void testRemoveNotify() {
        System.out.println("removeNotify");
        Drawing drawing = null;
        Figure instance = new FigureImpl();
        instance.removeNotify(drawing);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class Figure.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Figure newChild = null;
        Figure instance = new FigureImpl();
        instance.add(newChild);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class Figure.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        Figure child = null;
        Figure instance = new FigureImpl();
        instance.remove(child);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChild method, of class Figure.
     */
    @Test
    public void testGetChild() {
        System.out.println("getChild");
        int index = 0;
        Figure instance = new FigureImpl();
        Figure expResult = null;
        Figure result = instance.getChild(index);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastChild method, of class Figure.
     */
    @Test
    public void testGetLastChild() {
        System.out.println("getLastChild");
        Figure instance = new FigureImpl();
        Figure expResult = null;
        Figure result = instance.getLastChild();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFirstChild method, of class Figure.
     */
    @Test
    public void testGetFirstChild() {
        System.out.println("getFirstChild");
        Figure instance = new FigureImpl();
        Figure expResult = null;
        Figure result = instance.getFirstChild();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChildren method, of class Figure.
     */
    @Test
    public void testGetChildren() {
        System.out.println("getChildren");
        Figure instance = new FigureImpl();
        ObservableList<Figure> expResult = null;
        ObservableList<Figure> result = instance.getChildren();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class Figure.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        Figure instance = new FigureImpl();
        Figure expResult = null;
        Figure result = instance.getParent();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRoot method, of class Figure.
     */
    @Test
    public void testGetRoot() {
        System.out.println("getRoot");
        Figure instance = new FigureImpl();
        Figure expResult = null;
        Figure result = instance.getRoot();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDrawing method, of class Figure.
     */
    @Test
    public void testGetDrawing() {
        System.out.println("getDrawing");
        Figure instance = new FigureImpl();
        Drawing expResult = null;
        Drawing result = instance.getDrawing();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLayer method, of class Figure.
     */
    @Test
    public void testGetLayer() {
        System.out.println("getLayer");
        Figure instance = new FigureImpl();
        Layer expResult = null;
        Layer result = instance.getLayer();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDependentFigures method, of class Figure.
     */
    @Test
    public void testGetDependentFigures() {
        System.out.println("getDependentFigures");
        Figure instance = new FigureImpl();
        Set<Figure> expResult = null;
        Set<Figure> result = instance.getDependentFigures();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProvidingFigures method, of class Figure.
     */
    @Test
    public void testGetProvidingFigures() {
        System.out.println("getProvidingFigures");
        Figure instance = new FigureImpl();
        Set<Figure> expResult = null;
        Set<Figure> result = instance.getProvidingFigures();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of disconnect method, of class Figure.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        Figure instance = new FigureImpl();
        instance.disconnect();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSupportedKeys method, of class Figure.
     */
    @Test
    public void testGetSupportedKeys() {
        System.out.println("getSupportedKeys");
        Figure instance = new FigureImpl();
        Set<MapAccessor<?>> expResult = null;
        Set<MapAccessor<?>> result = instance.getSupportedKeys();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDeclaredAndInheritedKeys method, of class Figure.
     */
    @Test
    public void testGetDeclaredAndInheritedKeys() {
        System.out.println("getDeclaredAndInheritedKeys");
        Class<?> clazz = null;
        Set<MapAccessor<?>> expResult = null;
        Set<MapAccessor<?>> result = Figure.getDeclaredAndInheritedKeys(clazz);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPreferredAspectRatio method, of class Figure.
     */
    @Test
    public void testGetPreferredAspectRatio() {
        System.out.println("getPreferredAspectRatio");
        Figure instance = new FigureImpl();
        double expResult = 0.0;
        double result = instance.getPreferredAspectRatio();
        assertEquals(result, expResult, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCenterInLocal method, of class Figure.
     */
    @Test
    public void testGetCenterInLocal() {
        System.out.println("getCenterInLocal");
        Figure instance = new FigureImpl();
        Point2D expResult = null;
        Point2D result = instance.getCenterInLocal();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCenterInParent method, of class Figure.
     */
    @Test
    public void testGetCenterInParent() {
        System.out.println("getCenterInParent");
        Figure instance = new FigureImpl();
        Point2D expResult = null;
        Point2D result = instance.getCenterInParent();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentToLocal method, of class Figure.
     */
    @Test
    public void testGetParentToLocal() {
        System.out.println("getParentToLocal");
        Figure instance = new FigureImpl();
        Transform expResult = null;
        Transform result = instance.getParentToLocal();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLocalToParent method, of class Figure.
     */
    @Test
    public void testGetLocalToParent() {
        System.out.println("getLocalToParent");
        Figure instance = new FigureImpl();
        Transform expResult = null;
        Transform result = instance.getLocalToParent();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWorldToLocal method, of class Figure.
     */
    @Test
    public void testGetWorldToLocal() {
        System.out.println("getWorldToLocal");
        Figure instance = new FigureImpl();
        Transform expResult = null;
        Transform result = instance.getWorldToLocal();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWorldToParent method, of class Figure.
     */
    @Test
    public void testGetWorldToParent() {
        System.out.println("getWorldToParent");
        Figure instance = new FigureImpl();
        Transform expResult = null;
        Transform result = instance.getWorldToParent();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLocalToWorld method, of class Figure.
     */
    @Test
    public void testGetLocalToWorld() {
        System.out.println("getLocalToWorld");
        Figure instance = new FigureImpl();
        Transform expResult = null;
        Transform result = instance.getLocalToWorld();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentToWorld method, of class Figure.
     */
    @Test
    public void testGetParentToWorld() {
        System.out.println("getParentToWorld");
        Figure instance = new FigureImpl();
        Transform expResult = null;
        Transform result = instance.getParentToWorld();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of worldToLocal method, of class Figure.
     */
    @Test
    public void testWorldToLocal() {
        System.out.println("worldToLocal");
        Point2D pointInWorld = null;
        Figure instance = new FigureImpl();
        Point2D expResult = null;
        Point2D result = instance.worldToLocal(pointInWorld);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of worldToParent method, of class Figure.
     */
    @Test
    public void testWorldToParent() {
        System.out.println("worldToParent");
        Point2D pointInWorld = null;
        Figure instance = new FigureImpl();
        Point2D expResult = null;
        Point2D result = instance.worldToParent(pointInWorld);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of localToWorld method, of class Figure.
     */
    @Test
    public void testLocalToWorld() {
        System.out.println("localToWorld");
        Point2D p = null;
        Figure instance = new FigureImpl();
        Point2D expResult = null;
        Point2D result = instance.localToWorld(p);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStyleableParent method, of class Figure.
     */
    @Test
    public void testGetStyleableParent() {
        System.out.println("getStyleableParent");
        Figure instance = new FigureImpl();
        Styleable expResult = null;
        Styleable result = instance.getStyleableParent();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of invalidateTransforms method, of class Figure.
     */
    @Test
    public void testInvalidateTransforms() {
        System.out.println("invalidateTransforms");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.invalidateTransforms();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPropertyChangeListeners method, of class Figure.
     */
    @Test
    public void testGetPropertyChangeListeners() {
        System.out.println("getPropertyChangeListeners");
        Figure instance = new FigureImpl();
        CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> expResult = null;
        CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> result = instance.getPropertyChangeListeners();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPropertyChangeListener method, of class Figure.
     */
    @Test
    public void testAddPropertyChangeListener() {
        System.out.println("addPropertyChangeListener");
        Listener<FigurePropertyChangeEvent> listener = null;
        Figure instance = new FigureImpl();
        instance.addPropertyChangeListener(listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removePropertyChangeListener method, of class Figure.
     */
    @Test
    public void testRemovePropertyChangeListener() {
        System.out.println("removePropertyChangeListener");
        Listener<FigurePropertyChangeEvent> listener = null;
        Figure instance = new FigureImpl();
        instance.removePropertyChangeListener(listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasPropertyChangeListeners method, of class Figure.
     */
    @Test
    public void testHasPropertyChangeListeners() {
        System.out.println("hasPropertyChangeListeners");
        Figure instance = new FigureImpl();
        boolean expResult = false;
        boolean result = instance.hasPropertyChangeListeners();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of firePropertyChangeEvent method, of class Figure.
     */
    @Test
    public void testFirePropertyChangeEvent_5args() {
        System.out.println("firePropertyChangeEvent");
        Figure source = null;
        FigurePropertyChangeEvent.EventType type = null;
        Key<Object> key = null;
        Object oldValue = null;
        Object newValue = null;
        Figure instance = new FigureImpl();
        instance.firePropertyChangeEvent(source, type, key, oldValue, newValue);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of firePropertyChangeEvent method, of class Figure.
     */
    @Test
    public void testFirePropertyChangeEvent_FigurePropertyChangeEvent() {
        System.out.println("firePropertyChangeEvent");
        FigurePropertyChangeEvent event = null;
        Figure instance = new FigureImpl();
        instance.firePropertyChangeEvent(event);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class FigureImpl extends SimpleStyleablePropertyBean implements Figure {

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

        public void reshape(Transform transform) {
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
    }

}
