/* @(#)FigureSelectorModelTest.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.css;

import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.figure.LabelFigure;
import org.jhotdraw8.draw.key.NullablePaintableStyleableKey;
import org.jhotdraw8.text.Converter;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * FigureSelectorModelTest.
 *
 * @author Werner Randelshofer
 */
public class FigureSelectorModelTest {

    public FigureSelectorModelTest() {
    }

    /**
     * Test of getProperties method, of class SimplePropertyBean.
     */
    @Test
    public void testNullValueIsNotSameAsDefaultPropertyValue() throws ParseException {
        System.out.println("testNullValueIsNotSameAsDefaultPropertyValue");
        LabelFigure figure = new LabelFigure();
        FigureSelectorModel instance = new FigureSelectorModel();

        final NullablePaintableStyleableKey key = FillableFigure.FILL;
        final String attrName = key.getCssName();
        final String namespace = key.getCssNamespace();
        final Converter<Paintable> converter = key.getCssConverter();


        assertNotNull(key.getDefaultValue(), "need a key with a non-null default value for this test");

        assertEquals("initial", instance.getAttributeAsString(figure, namespace, attrName), "no value has been set, must be 'initial'");

        instance.setAttribute(figure, StyleOrigin.USER, namespace, attrName, ImmutableLists.of(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE)));

        assertNull(figure.get(key), "figure.get(key) value has been explicitly set to null");

        assertEquals(instance.getAttributeAsString(figure, namespace, attrName), converter.toString(null), "model.get(figure,key) value has been explicitly set to null");

    }

}
