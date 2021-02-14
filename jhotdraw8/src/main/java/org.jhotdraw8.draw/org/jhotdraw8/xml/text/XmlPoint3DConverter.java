/*
 * @(#)XmlPoint3DConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import javafx.geometry.Point3D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * Converts a {@code javafx.geometry.Point3D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class XmlPoint3DConverter implements Converter<Point3D> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,number}|[ ]+}", new XmlConverterFactory());

    @Override
    public void toString(Appendable out, @Nullable IdSupplier idSupplier, @NonNull Point3D value) throws IOException {
        if (value.getZ() == 0.0) {
            formatter.toStr(out, idSupplier, 2, value.getX(), value.getY());
        } else {
            formatter.toStr(out, idSupplier, 3, value.getX(), value.getY(), value.getZ());
        }
    }

    @Override
    public @NonNull Point3D fromString(@NonNull CharBuffer buf, @Nullable IdResolver idResolver) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        switch ((int) v[0]) {
        case 2:
            return new Point3D((double) v[1], (double) v[2], 0.0);
        case 3:
            return new Point3D((double) v[1], (double) v[2], (double) v[3]);
        default:
            throw new ParseException("Point3D with 2 to 3 values expected.", buf.position());
        }
    }

    @Override
    public @NonNull Point3D getDefaultValue() {
        return new Point3D(0, 0, 0);
    }
}
