/* @(#)BezierNodeMoveHandleNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * BezierNodeMoveHandleNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class BezierNodeMoveHandleNGTest {

    public BezierNodeMoveHandleNGTest() {
    }

    /**
     * Test of getCursor method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testGetCursor() {
        System.out.println("getCursor");
        BezierNodeMoveHandle instance = null;
        Cursor expResult = null;
        Cursor result = instance.getCursor();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNode method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testGetNode() {
        System.out.println("getNode");
        BezierNodeMoveHandle instance = null;
        Region expResult = null;
        Region result = instance.getNode();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateNode method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testUpdateNode() {
        System.out.println("updateNode");
        DrawingView view = null;
        BezierNodeMoveHandle instance = null;
        instance.updateNode(view);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleMousePressed method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testHandleMousePressed() {
        System.out.println("handleMousePressed");
        MouseEvent event = null;
        DrawingView view = null;
        BezierNodeMoveHandle instance = null;
        instance.handleMousePressed(event, view);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleMouseDragged method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testHandleMouseDragged() {
        System.out.println("handleMouseDragged");
        MouseEvent event = null;
        DrawingView view = null;
        BezierNodeMoveHandle instance = null;
        instance.handleMouseDragged(event, view);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of translateFigure method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testTranslateFigure() {
        System.out.println("translateFigure");
        Figure f = null;
        Point2D oldPoint = null;
        Point2D newPoint = null;
        DrawingModel model = null;
        BezierNodeMoveHandle.translateFigure(f, oldPoint, newPoint, model);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleMouseReleased method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testHandleMouseReleased() {
        System.out.println("handleMouseReleased");
        MouseEvent event = null;
        DrawingView dv = null;
        BezierNodeMoveHandle instance = null;
        instance.handleMouseReleased(event, dv);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSelectable method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testIsSelectable() {
        System.out.println("isSelectable");
        BezierNodeMoveHandle instance = null;
        boolean expResult = false;
        boolean result = instance.isSelectable();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLocationInView method, of class BezierNodeMoveHandle.
     */
    @Test
    public void testGetLocationInView() {
        System.out.println("getLocationInView");
        BezierNodeMoveHandle instance = null;
        Point2D expResult = null;
        Point2D result = instance.getLocationInView();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}