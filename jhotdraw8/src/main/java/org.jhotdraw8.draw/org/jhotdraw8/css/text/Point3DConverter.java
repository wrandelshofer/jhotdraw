/*
 * @(#)Point3DConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
public class Point3DConverter extends AbstractCssConverter<Point3D> {

    private final boolean withComma = true;
    private final boolean withSpace;

    public Point3DConverter(boolean nullable) {
        this(nullable, true);
    }

    public Point3DConverter(boolean nullable, boolean withSpace) {
        super(nullable);
        this.withSpace = withSpace;
    }

    @Override
    public @NonNull String getHelpText() {
        return "Format of ⟨Point3D⟩: ⟨x⟩ ⟨y⟩ ｜ ⟨x⟩ ⟨y⟩ ⟨z⟩";
    }

    @Override
    public @NonNull Point3D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final double x, y, z;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Point3D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Point3D⟩: ⟨y⟩ expected.");
        y = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        if (tt.next() == CssTokenType.TT_NUMBER) {
            tt.pushBack();
            z = tt.currentNumberNonNull().doubleValue();
        } else {
            z = 0;
        }

        return new Point3D(x, y, z);
    }

    private void produceDelimiter(@NonNull Consumer<CssToken> out) {
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
    }

    @Override
    protected <TT extends Point3D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
        produceDelimiter(out);
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
        double z = value.getZ();
        if (z != 0.0) {
            produceDelimiter(out);
            out.accept(new CssToken(CssTokenType.TT_NUMBER, z));
        }
    }
}
