/*
 * @(#)XmlRectangle2DConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import javafx.geometry.Rectangle2D;
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
 * Converts a {@code javafx.geometry.Rectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class XmlRectangle2DConverter implements Converter<Rectangle2D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} +{1,number} +{2,number} +{3,number}", new XmlConverterFactory());

    @Override
    public void toString(Appendable out, @Nullable IdSupplier idSupplier, @NonNull Rectangle2D value) throws IOException {
        formatter.toStr(out, idSupplier, value.getMinX(), value.getMinY(), value.getWidth(), value.getHeight());
    }

    @NonNull
    @Override
    public Rectangle2D fromString(@NonNull CharBuffer buf, @Nullable IdResolver idResolver) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Rectangle2D((double) v[0], (double) v[1], (double) v[2], (double) v[3]);
    }

    @NonNull
    @Override
    public Rectangle2D getDefaultValue() {
        return new Rectangle2D(0, 0, 1, 1);
    }
}
