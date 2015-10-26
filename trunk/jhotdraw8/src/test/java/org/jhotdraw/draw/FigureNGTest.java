/* @(#)FigureNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.shape.RectangleFigure;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
     * Test of getDeclaredAndInheritedKeys method, of class Figure.
     */
    @Test
    public void testGetDeclaredAndInheritedKeys() {
       Set<Key<?>> figureKeys = Figure.getDeclaredAndInheritedKeys(Figure.class);
         Set<Key<?>> rectangleFigureKeys = Figure.getDeclaredAndInheritedKeys(RectangleFigure.class);
         Set<Key<?>> intersection=new HashSet<>(figureKeys);
         intersection.retainAll(rectangleFigureKeys);
System.out.println("r:"+rectangleFigureKeys);         
        assertEquals(figureKeys,intersection);
    }
    
}
