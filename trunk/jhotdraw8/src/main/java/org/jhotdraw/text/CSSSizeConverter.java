/* @(#)CSSSizeConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Point2D;

/**
 * CSSSizeConverter.
 * @author Werner Randelshofer
 */
public class CSSSizeConverter implements Converter<Double> {
   private final PatternConverter formatter = new PatternConverter("{0,number}{1,choice,#0|}", new XMLConverterFactory());

    @Override
    public void toString(Appendable out, Double value) throws IOException {
        formatter.toString(out, new Object[]{value});
    }

    @Override
    public Double fromString(CharBuffer buf) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return (double) v[0];
    }
}
