/* @(#)Rectangle2DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.text;

import java.io.IOException;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;



/**
 * Rectangle2DConverter.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Rectangle2DConverter implements Converter<Rectangle2D> {
    private final PatternFormat formatter = new PatternFormat("{0,number} {1,number} {2,number} {3,number}", (type, style) -> new ConverterFormatWrapper(new RealNumberConverter()));
    
    @Override
    public String toString(Rectangle2D value) {
        return formatter.format(new Object[]{value.getMinX(), value.getMinY(),value.getWidth(),value.getHeight()});
    }

    @Override
    public Rectangle2D toValue(String value, ParsePosition pp) {
        Object[] v = formatter.parse(value, pp);
        if (pp.getErrorIndex() != -1) {
            return null;
        }
        return new Rectangle2D((double) v[0], (double) v[1],(double) v[2], (double) v[3]);
    }

}
