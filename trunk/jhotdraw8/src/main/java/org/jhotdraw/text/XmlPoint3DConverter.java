/* @(#)XmlPoint3DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import org.jhotdraw.draw.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Point3D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlPoint3DConverter implements Converter<Point3D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} {1,number} {2,number}", new XmlConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, Point3D value) throws IOException {
        formatter.toStr(out,idFactory, value.getX(), value.getY(), value.getZ());
    }

    @Override
    public Point3D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Point3D((double) v[0], (double) v[1], (double)v[3]);
    }
    @Override
    public Point3D getDefaultValue() {
        return new Point3D(0,0,0);
    }
}
