/* @(#)XmlPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Point2D;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlPoint2DConverter implements Converter<Point2D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} {1,number}", new XmlConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @NonNull Point2D value) throws IOException {
        formatter.toStr(out, idFactory, value.getX(), value.getY());
    }

    @NonNull
    @Override
    public Point2D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Point2D((double) v[0], (double) v[1]);
    }

    @NonNull
    @Override
    public Point2D getDefaultValue() {
        return new Point2D(0, 0);
    }
}
