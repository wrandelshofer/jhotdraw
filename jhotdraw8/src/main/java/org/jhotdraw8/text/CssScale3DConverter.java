/* @(#)CssScale3DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Point3D;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssScale3DConverter implements Converter<Point3D> {

    // FIXME must use CssParser instead of PatternConverter!!
    private final PatternConverter formatter = new PatternConverter("{0,list,{1,number}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, Point3D value) throws IOException {
        if (value.getZ() == 1.0) {
            if (value.getX() == value.getY()) {
                formatter.toStr(out, idFactory, 1, value.getX());
            } else {
                formatter.toStr(out, idFactory, 2, value.getX(), value.getY());
            }
        } else {
            formatter.toStr(out, idFactory, 3, value.getX(), value.getY(), value.getZ());
        }
    }

    @Override
    public Point3D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        switch ((int) v[0]) {
            case 1:
                return new Point3D(((Number) v[1]).doubleValue(), ((Number) v[1]).doubleValue(), 1.0);
            case 2:
                return new Point3D(((Number) v[1]).doubleValue(), ((Number) v[2]).doubleValue(), 1.0);
            case 3:
                return new Point3D(((Number) v[1]).doubleValue(), ((Number) v[2]).doubleValue(), ((Number) v[3]).doubleValue());
            default:
                throw new ParseException("Scale with 1 to 3 values expected.", buf.position());
        }
    }

    @Override
    public Point3D getDefaultValue() {
        return new Point3D(1, 1, 1);
    }
    
        @Override
    public String getHelpText() {
        return "Format of ⟨Scale3D⟩: ⟨s⟩ ｜ ⟨xs⟩ ⟨ys⟩ ｜ ⟨xs⟩ ⟨ys⟩ ⟨zs⟩";
    }

}
