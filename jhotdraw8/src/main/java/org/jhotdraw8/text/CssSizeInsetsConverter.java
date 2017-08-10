/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code CssSizeInsets} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSizeInsetsConverter implements Converter<CssSizeInsets> {

   // FIXME must use CssParser instead of PatternConverter!!
    private final PatternConverter formatter = new PatternConverter("{0,list,{1,size}|[ ]+}", new CssConverterFactory());

    @Override
    public String getHelpText() {
        return "Format of ⟨Insets⟩: ⟨all-insets⟩｜⟨top&bottom⟩ ⟨left&right⟩｜⟨top⟩ ⟨right⟩ ⟨bottom⟩ ⟨left⟩";
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, CssSizeInsets value) throws IOException {
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

    @Override
    public CssSizeInsets fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
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

    @Override
    public CssSizeInsets getDefaultValue() {
        return new CssSizeInsets();
    }
    
    
}
