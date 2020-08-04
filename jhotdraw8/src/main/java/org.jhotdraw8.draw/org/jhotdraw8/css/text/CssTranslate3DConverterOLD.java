/*
 * @(#)CssTranslate3DConverterOLD.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.geometry.Point3D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.Point3D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class CssTranslate3DConverterOLD extends AbstractCssConverter<Point3D> {

    public CssTranslate3DConverterOLD(boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨Translate3D⟩: ⟨x⟩ ⟨y⟩ ［⟨z⟩］";
    }

    @NonNull
    @Override
    public Point3D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final double x, y, z;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Translate3D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Translate3D⟩: ⟨y⟩ expected.");
        y = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        if (tt.next() == CssTokenType.TT_NUMBER) {
            z = tt.currentNumberNonNull().doubleValue();
        } else {
            tt.pushBack();
            z = 0;
        }

        return new Point3D(x, y, z);
    }

    @Override
    protected <TT extends Point3D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
        if (value.getZ() != 0.0) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getZ()));
        }
    }
}
