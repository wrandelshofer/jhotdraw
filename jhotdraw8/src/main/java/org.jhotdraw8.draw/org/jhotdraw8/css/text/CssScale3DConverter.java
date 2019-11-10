/*
 * @(#)CssScale3DConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.geometry.Point3D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class CssScale3DConverter extends AbstractCssConverter<Point3D> {

    public CssScale3DConverter(boolean nullable) {
        super(nullable);
    }


    @NonNull
    @Override
    public Point3D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final double x, y, z;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Translate3D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        if (tt.next() == CssTokenType.TT_NUMBER) {
            y = tt.currentNumberNonNull().doubleValue();
            tt.skipIfPresent(CssTokenType.TT_COMMA);
            if (tt.next() == CssTokenType.TT_NUMBER) {
                z = tt.currentNumberNonNull().doubleValue();
            } else {
                tt.pushBack();
                z = 1;
            }
        } else {
            tt.pushBack();
            y = x;
            z = x;
        }
        return new Point3D(x, y, z);
    }

    @Override
    protected <TT extends Point3D> void produceTokensNonNull(@NonNull TT value, @Nullable IdFactory idFactory, @NonNull Consumer<CssToken> out) {
        if (value.getZ() == 1.0) {
            if (value.getX() == value.getY()) {
                out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
            } else {
                out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
            }
        } else {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getZ()));
        }
    }

    @NonNull
    @Override
    public Point3D getDefaultValue() {
        return new Point3D(1, 1, 1);
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨Scale3D⟩: ⟨s⟩ ｜ ⟨xs⟩ ⟨ys⟩ ｜ ⟨xs⟩ ⟨ys⟩ ⟨zs⟩";
    }

}
