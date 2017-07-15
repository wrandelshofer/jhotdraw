/* @(#)FigureSelectorModelNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.css;

import javafx.css.StyleOrigin;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.figure.SimpleLabelFigure;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.draw.key.Paintable;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import org.testng.annotations.Test;

/**
 * FigureSelectorModelNGTest.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureSelectorModelNGTest {
    
    public FigureSelectorModelNGTest() {
    }

    /**
     * Test of getProperties method, of class SimplePropertyBean.
     */
    @Test
    public void testNullValueIsNotSameAsDefaultPropertyValue() {
        System.out.println("testNullValueIsNotSameAsDefaultPropertyValue");
        SimpleLabelFigure figure = new SimpleLabelFigure();
        FigureSelectorModel instance = new FigureSelectorModel();
        
        final PaintableStyleableFigureKey key = FillableFigure.FILL;
        final String attrName=key.getCssName();
        final Converter<Paintable> converter=key.getConverter();
        
        
       assertNotNull(key.getDefaultValue(),"need a key with a non-null default value for this test");
        
        assertEquals(instance.getAttribute(figure,attrName),converter.toString(key.getDefaultValue()),"no value has been set, must be default");
        
        instance.setAttribute(figure, StyleOrigin.USER, attrName, converter.toString(null));
        
        assertNull(figure.get(key),"figure.get(key) value has been explicitely set to null");
        
       assertEquals(instance.getAttribute(figure, attrName), converter.toString(null),"model.get(figure,key) value has been explicitly set to null");
       
    }
    
}
