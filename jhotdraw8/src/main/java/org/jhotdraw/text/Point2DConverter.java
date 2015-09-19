/*
 * @(#)Point2DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.ParsePosition;
import javafx.geometry.Point2D;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point2DConverter implements Converter<Point2D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} {1,number}", new XMLConverterFactory());

    @Override
    public void toString(Point2D value, Appendable out) throws IOException {
        formatter.toString(new Object[]{value.getX(), value.getY()}, out);
    }

    @Override
    public Point2D fromString(CharBuffer buf) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Point2D((double) v[0], (double) v[1]);
    }

}
