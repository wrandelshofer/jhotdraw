/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPoint2DConverter implements Converter<Point2D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} +{1,number}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @Nonnull Point2D value) throws IOException {
        formatter.toStr(out, idFactory, value.getX(), value.getY());
    }

    @Nonnull
    @Override
    public Point2D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Point2D(((Number) v[0]).doubleValue(), ((Number) v[1]).doubleValue());
    }

    @Nonnull
    @Override
    public Point2D getDefaultValue() {
        return new Point2D(0, 0);
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Point2D⟩: ⟨x⟩ ⟨y⟩";
    }

}
