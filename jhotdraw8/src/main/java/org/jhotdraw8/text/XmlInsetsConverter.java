/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Insets;
import javax.annotation.Nonnull;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Insets} into a {@code String} and vice
 * versa.
 * <p>
 * List of four sizes in the sequence top, right, bottom, left. If left is
 * omitted, it is the same as right. If bottom is omitted, it is the same as
 * top. If right is omitted it is the same as top.
 * <pre>
 * insets       = top , right , bottom, left ;
 * </pre> *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlInsetsConverter implements Converter<Insets> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,number}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @Nonnull Insets value) throws IOException {
        if (value.getRight() == value.getLeft()) {
            if (value.getTop() == value.getBottom()) {
                if (value.getTop() == value.getLeft()) {
                    formatter.toStr(out, idFactory, 1, value.getTop());
                } else {
                    formatter.toStr(out, idFactory, 2, value.getTop(), value.getRight());
                }
            } else {
                formatter.toStr(out, idFactory, 3, value.getTop(), value.getRight(), value.getBottom());
            }
        } else {
            formatter.toStr(out, idFactory, 4, value.getTop(), value.getRight(), value.getBottom(), value.getLeft());
        }
    }

    @Nonnull
    @Override
    public Insets fromString(@Nonnull CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        switch ((int) v[0]) {
            case 1:
                return new Insets(((Number)v[1]).doubleValue(), ((Number)v[1]).doubleValue(), ((Number)v[1]).doubleValue(), ((Number)v[1]).doubleValue());
            case 2:
                return new Insets(((Number)v[1]).doubleValue(), ((Number)v[2]).doubleValue(), ((Number)v[1]).doubleValue(), ((Number)v[2]).doubleValue());
            case 3:
                return new Insets(((Number)v[1]).doubleValue(), ((Number)v[2]).doubleValue(), ((Number)v[3]).doubleValue(), ((Number)v[2]).doubleValue());
            case 4:
                return new Insets(((Number)v[1]).doubleValue(), ((Number)v[2]).doubleValue(), ((Number)v[3]).doubleValue(), ((Number)v[4]).doubleValue());
            default:
                throw new ParseException("Insets with 1 to 4 dimension values expected.", buf.position());
        }
    }

    @Nonnull
    @Override
    public Insets getDefaultValue() {
        return new Insets(0, 0, 0, 0);
    }
}
