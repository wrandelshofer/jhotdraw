/* @(#)RectangleFigureNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import java.util.List;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;
import org.jhotdraw.styleable.SimpleParsedValue;
import org.jhotdraw.draw.Figure;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * RectangleFigureNGTest.
 * @author Werner Randelshofer
 */
public class RectangleFigureNGTest {
    
    /**
     * Tests the CSS support of the figure.
     */
    @Test(dataProvider="cssData")
    public void testCSS(Key<Object> key,String cssProperty, Object userValue,Object cssValue, Object expectedOutputValue) {
        RectangleFigure instance = new RectangleFigure();
        //
        System.out.println(instance.getSupportedKeys());
        //
        System.out.println(instance.getCssMetaData());
        
        instance.set(key, userValue);
        System.out.println(key+":"+instance.get(key));
        
        List<CssMetaData<? extends Styleable,?>> list=instance.getCssMetaData();
        for (CssMetaData<? extends Styleable,?> meta:list) {
            if (meta.getProperty().equals(cssProperty)) {
                @SuppressWarnings("unchecked")
                CssMetaData<Figure,Paint> fm=(CssMetaData<Figure,Paint>)meta;
                @SuppressWarnings("unchecked")
                StyleConverter<Object,Paint> converter=(StyleConverter<Object,Paint>)fm.getConverter();
                SimpleParsedValue<Object,Paint> parsedValue=new SimpleParsedValue<Object,Paint>(expectedOutputValue,converter);
                Paint convertedCssValue=converter.convert(parsedValue, null);
                fm.getStyleableProperty(instance).setValue(convertedCssValue);
            }
        }
        System.out.println(key+":"+instance.get(key));

        Object actualOutputValue=instance.get(key);
        System.out.println("actual:"+actualOutputValue+" expected:"+expectedOutputValue);
        assertEquals(actualOutputValue, expectedOutputValue);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    
    @DataProvider
    public Object[][] cssData() {
        return new Object[][]{
            {FilledShapeFigure.FILL_COLOR,"-jhotdraw-fill", Color.RED,"blue",Color.BLUE},
        };

    }     
}
