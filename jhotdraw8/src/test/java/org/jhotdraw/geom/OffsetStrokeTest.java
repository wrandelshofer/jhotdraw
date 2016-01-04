/* @(#)OffsetStrokeTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.geom;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.io.IOException;
import javafx.scene.shape.SVGPath;
import org.jhotdraw.svg.SvgPath2D;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

/**
 *
 * @author werni
 */
public class OffsetStrokeTest {

    public OffsetStrokeTest() {
    }

    /**
     * Test of createStrokedShape method, of class OffsetStroke.
     */
    @Test(dataProvider = "segmentData")
    public void testSegments(String svg, String expected) throws IOException {
       SvgPath2D input=Shapes.awtShapeFromSvgString(svg);
       String actual = Shapes.svgStringFromAWT(input);
       
       OffsetStroke stroke=new OffsetStroke(2,BasicStroke.JOIN_BEVEL,4);
       Shape output = stroke.createStrokedShape(input);
       
       System.out.println("input   :"+svg);
       System.out.println("expected:"+expected);
       System.out.println("actual  :"+actual);
       
       
    }

    @DataProvider
    public Object[][] segmentData() {
        return new Object[][]{//
            {"M0,0 L10,0 L10,10 L0,10",""},
            {"M0,0 L10,0 L10,10 L0,10 Z",""},
            {"M0,0 L10,0 L10,10 L0,10 L0,0",""},
            {"M0,0 L10,0 L10,10 L0,10 L0,0 Z",""},
            {"M0,0 L10,0 M20,0 L30,0",""},
            {"M0,0 L10,0 M20,0 L30,0 M40,0",""},
            {"M0,0 L10,0 M20,0 L30,0 L40,0",""},
            {"M0,0 L10,0 M20,0 L30,0 L40,0 M50,0",""},
        //
        };
    }
}
