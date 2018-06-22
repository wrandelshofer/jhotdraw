/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Point2D;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code CssSize2D} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSize2DConverter implements Converter<CssSize2D> {

    private final PatternConverter formatter = new PatternConverter("{0,size} +{1,size}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @NonNull CssSize2D value) throws IOException {
        formatter.toStr(out, idFactory, value.getX(), value.getY());
    }

    @NonNull
    @Override
    public CssSize2D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new CssSize2D((CssSize) v[0], (CssSize) v[1]);
    }

    @Nullable
    @Override
    public CssSize2D getDefaultValue() {
        return new CssSize2D(new CssSize(0, null), new CssSize(0, null));
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨Size2D⟩: ⟨x⟩ ⟨y⟩";
    }

}
