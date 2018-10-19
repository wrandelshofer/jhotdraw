/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

/**
 * Converts a {@code CssSizeInsets} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSizeInsetsConverter implements Converter<CssSizeInsets> {

    // FIXME must use CssParser instead of PatternConverter!!
    private final PatternConverter formatter = new PatternConverter("{0,list,{1,size}|[ ]+}", new CssConverterFactory());

    /** Whether the user may enter a null value. */
    private boolean nullable = false;

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Insets⟩: ⟨all-insets⟩｜⟨top&bottom⟩ ⟨left&right⟩｜⟨top⟩ ⟨right⟩ ⟨bottom⟩ ⟨left⟩";
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable CssSizeInsets value) throws IOException {
        if (value == null || value.getLeft() == null || value.getRight() == null | value.getTop() == null || value.getBottom() == null) {
            out.append("none");
            return;
        }
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

    @Nullable
    @Override
    public CssSizeInsets fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString();
        if (nullable && "none".equals(str.trim())) {
            return null;
        }

        Object[] v = formatter.fromString(buf);
        switch ((int) v[0]) {
            case 1:
                return new CssSizeInsets((CssSize) v[1], (CssSize) v[1], (CssSize) v[1], (CssSize) v[1]);
            case 2:
                return new CssSizeInsets((CssSize) v[1], (CssSize) v[2], (CssSize) v[1], (CssSize) v[2]);
            case 3:
                return new CssSizeInsets((CssSize) v[1], (CssSize) v[2], (CssSize) v[3], (CssSize) v[2]);
            case 4:
                return new CssSizeInsets((CssSize) v[1], (CssSize) v[2], (CssSize) v[3], (CssSize) v[4]);
            default:
                throw new ParseException("CssSizeInsets with 1 to 4 dimension values expected.", buf.position());
        }
    }

    @Nonnull
    @Override
    public CssSizeInsets getDefaultValue() {
        return  CssSizeInsets.ZERO;
    }

}
