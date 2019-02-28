/* @(#)XmlPoint2DConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

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
    public void toString(Appendable out, IdFactory idFactory, @Nonnull Point2D value) throws IOException {
        formatter.toStr(out, idFactory, value.getX(), value.getY());
    }

    @Nonnull
    @Override
    public Point2D fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Point2D((double) v[0], (double) v[1]);
    }

    @Nonnull
    @Override
    public Point2D getDefaultValue() {
        return new Point2D(0, 0);
    }
}
