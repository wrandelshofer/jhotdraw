/*
 * @(#)CssSymmetricPoint2DConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa. If the X and the Y-value are identical, then only one value is output.
 *
 * @author Werner Randelshofer
 */
public class CssSymmetricPoint2DConverter implements Converter<CssPoint2D> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,size}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @NonNull CssPoint2D value) throws IOException {
        CssSize x = value.getX();
        CssSize y = value.getY();
        if (x == y) {
            formatter.toStr(out, idFactory, 1, value.getX());
        } else {
            formatter.toStr(out, idFactory, 2, value.getX(), value.getY());
        }
    }

    @NonNull
    @Override
    public CssPoint2D fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        int count = (Integer) v[0];
        switch (count) {
            case 1:
                return new CssPoint2D(((CssSize) v[1]), ((CssSize) v[1]));
            case 2:
                return new CssPoint2D(((CssSize) v[1]), ((CssSize) v[2]));
            default:
                throw new ParseException("one or two numbers expected, found " + count + " numbers", 0);
        }
    }

    @Nullable
    @Override
    public CssPoint2D getDefaultValue() {
        return new CssPoint2D(CssSize.ZERO, CssSize.ZERO);
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨SymmetricSize2D⟩: ⟨xy⟩ ｜ ⟨x⟩ ⟨y⟩";
    }

}
