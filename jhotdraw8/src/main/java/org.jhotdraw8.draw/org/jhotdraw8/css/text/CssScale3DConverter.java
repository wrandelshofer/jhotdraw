/*
 * @(#)CssScale3DConverter.java
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
public class CssScale3DConverter extends AbstractCssConverter<Point3D> {

    private final boolean withSpace;
    private final boolean withComma = false;

    public CssScale3DConverter() {
        this(false, true);
    }

    public CssScale3DConverter(boolean nullable) {
        this(nullable, true);
    }

    public CssScale3DConverter(boolean nullable, boolean withSpace) {
        super(nullable);
        this.withSpace = withSpace;
    }

    @Override
    public @NonNull Point3D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final double x, y, z;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Scale3D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonNull().doubleValue();
        if (tt.next() == CssTokenType.TT_EOF) {
            y = x;
            z = 1;
        } else {
            tt.skipIfPresent(CssTokenType.TT_COMMA);
            tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Scale3D⟩: ⟨y⟩ expected.");
            y = tt.currentNumberNonNull().doubleValue();

            if (tt.next() == CssTokenType.TT_EOF) {
                z = 1;
            } else {
                tt.skipIfPresent(CssTokenType.TT_COMMA);
                tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Scale3D⟩: ⟨z⟩ expected.");
                z = tt.currentNumberNonNull().doubleValue();
            }

        }

        return new Point3D(x, y, z);
    }

    @Override
    protected <TT extends Point3D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        double x = value.getX();
        double y = value.getY();
        double z = value.getZ();
        out.accept(new CssToken(CssTokenType.TT_NUMBER, x));
        if (x != y || z != 1) {
            produceDelimiter(out);
            out.accept(new CssToken(CssTokenType.TT_NUMBER, y));
        }
        if (z != 1) {
            produceDelimiter(out);
            out.accept(new CssToken(CssTokenType.TT_NUMBER, z));
        }
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
    public @NonNull Point3D getDefaultValue() {
        return new Point3D(1, 1, 1);
    }

    @Override
    public @NonNull String getHelpText() {
        return "Format of ⟨Scale3D⟩: ⟨s⟩ ｜ ⟨xs⟩ ⟨ys⟩ ｜ ⟨xs⟩ ⟨ys⟩ ⟨zs⟩";
    }

}
