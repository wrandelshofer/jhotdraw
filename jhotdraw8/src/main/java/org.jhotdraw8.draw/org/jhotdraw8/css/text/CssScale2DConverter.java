/*
 * @(#)CssScale2DConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

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
public class CssScale2DConverter implements Converter<Point2D> {

    // FIXME must use CssParser instead of PatternConverter!!
    private final PatternConverter formatter = new PatternConverter("{0,list,{1,number}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @Nonnull Point2D value) throws IOException {
        if (value.getX() == value.getY()) {
            formatter.toStr(out, idFactory, 1, value.getX());
        } else {
            formatter.toStr(out, idFactory, 2, value.getX(), value.getY());
        }
    }

    @Nonnull
    @Override
    public Point2D fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        switch ((int) v[0]) {
            case 1:
                return new Point2D(((Number) v[1]).doubleValue(), ((Number) v[1]).doubleValue());
            case 2:
                return new Point2D(((Number) v[1]).doubleValue(), ((Number) v[2]).doubleValue());
            default:
                throw new ParseException("Scale with 1 to 2 values expected.", buf.position());
        }
    }

    @Nonnull
    @Override
    public Point2D getDefaultValue() {
        return new Point2D(1, 1);
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Scale2D⟩: ⟨s⟩ ｜ ⟨xs⟩ ⟨ys⟩";
    }

}
