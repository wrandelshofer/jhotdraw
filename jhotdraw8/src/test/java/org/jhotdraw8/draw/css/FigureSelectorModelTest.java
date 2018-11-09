/* @(#)FigureSelectorModelTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.css;

import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.figure.SimpleLabelFigure;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.text.Converter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * FigureSelectorModelTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureSelectorModelTest {

    public FigureSelectorModelTest() {
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
        final String attrName = key.getCssName();
        final Converter<Paintable> converter = key.getConverter();


        assertNotNull(key.getDefaultValue(), "need a key with a non-null default value for this test");

        assertNull( instance.getAttributeAsString(figure, attrName), "no value has been set, must be null");

        instance.setAttribute(figure, StyleOrigin.USER, attrName, ImmutableList.of(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE)));

        assertNull(figure.get(key), "figure.get(key) value has been explicitly set to null");

        assertEquals(instance.getAttributeAsString(figure, attrName), converter.toString(null), "model.get(figure,key) value has been explicitly set to null");

    }

}
