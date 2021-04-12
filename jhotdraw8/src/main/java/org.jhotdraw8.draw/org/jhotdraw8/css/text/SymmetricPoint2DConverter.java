/*
 * @(#)CssSymmetricPoint2DConverterOLD.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.geometry.Point2D;
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
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa. If the X and the Y-value are identical, then only one value is output.
 *
 * @author Werner Randelshofer
 */
public class SymmetricPoint2DConverter extends AbstractCssConverter<Point2D> {

    private final boolean withSpace;
    private boolean withComma;

    public SymmetricPoint2DConverter() {
        this(false, true, false);
    }

    public SymmetricPoint2DConverter(boolean nullable) {
        this(nullable, true, false);
    }

    public SymmetricPoint2DConverter(boolean nullable, boolean withSpace, boolean withComma) {
        super(nullable);
        this.withSpace = withSpace;
        this.withComma = withComma || !withSpace;
    }

    @Override
    public @NonNull Point2D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final double x, y;
        tt.requireNextToken(CssTokenType.TT_NUMBER, "x");
        x = tt.currentNumberNonNull().doubleValue();
        if (tt.next() == CssTokenType.TT_EOF) {
            y = x;
        } else {
            tt.pushBack();
            tt.skipIfPresent(CssTokenType.TT_COMMA);
            tt.requireNextToken(CssTokenType.TT_NUMBER, "y");
            y = tt.currentNumberNonNull().doubleValue();
        }

        return new Point2D(x, y);
    }

    @Override
    protected <TT extends Point2D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        double x = value.getX();
        double y = value.getY();
        out.accept(new CssToken(CssTokenType.TT_NUMBER, x));
        if (x != y) {
            if (withComma) {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
            }
            if (withSpace) {
                out.accept(new CssToken(CssTokenType.TT_S, " "));
            }
            out.accept(new CssToken(CssTokenType.TT_NUMBER, y));
        }
    }


    @Override
    public @NonNull Point2D getDefaultValue() {
        return new Point2D(0, 0);
    }

    @Override
    public @NonNull String getHelpText() {
        return "Format of ⟨SymmetricPoint2D⟩: ⟨xy⟩ ｜ ⟨x⟩ ⟨y⟩";
    }

}
