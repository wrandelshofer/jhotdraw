/* @(#)CssScale2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Point2D;
import org.jhotdraw8.io.IdFactory;

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
    public void toString(Appendable out, IdFactory idFactory, Point2D value) throws IOException {
        if (value.getX() == value.getY()) {
            formatter.toStr(out, idFactory, 1, value.getX());
        } else {
            formatter.toStr(out, idFactory, 2, value.getX(), value.getY());
        }
    }

    @Override
    public Point2D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
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

    @Override
    public Point2D getDefaultValue() {
        return new Point2D(1, 1);
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Scale2D⟩: ⟨s⟩ ｜ ⟨xs⟩ ⟨ys⟩";
    }

}
