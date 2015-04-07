/* @(#)Point2DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import javafx.geometry.Point2D;

/**
 * Point2DConverter.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point2DConverter implements Converter<Point2D> {

    private final PatternFormat formatter = new PatternFormat("{0,number} {1,number}", (type, style) -> new ConverterFormatWrapper(new RealNumberConverter()));

    @Override
    public String toString(Point2D value) {
        return formatter.format(new Object[]{value.getX(), value.getY()});
    }

    @Override
    public Point2D toValue(String value, ParsePosition pp) {
        Object[] v = formatter.parse(value, pp);
        if (pp.getErrorIndex() != -1) {
            return null;
        }
        return new Point2D((double) v[0], (double) v[1]);
    }

}
