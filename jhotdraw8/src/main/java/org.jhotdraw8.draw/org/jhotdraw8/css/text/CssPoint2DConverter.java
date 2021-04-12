/*
 * @(#)CssPoint2DConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import static org.jhotdraw8.css.text.CssSizeConverter.parseSize;

/**
 * Converts a {@code javafx.geometry.CssPoint2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class CssPoint2DConverter extends AbstractCssConverter<CssPoint2D> {
    private final boolean withSpace;
    private final boolean withComma;

    public CssPoint2DConverter(boolean nullable) {
        this(nullable, true, false);
    }

    public CssPoint2DConverter(boolean nullable, boolean withSpace, boolean withComma) {
        super(nullable);
        this.withSpace = withSpace || !withComma;
        this.withComma = withComma;
    }

    @Override
    public @NonNull CssPoint2D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final CssSize x, y;
        x = parseSize(tt, "x");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        y = parseSize(tt, "y");

        return new CssPoint2D(x, y);
    }

    @Override
    protected <TT extends CssPoint2D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        CssSize x = value.getX();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, x.getValue(), x.getUnits()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssSize y = value.getY();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, y.getValue(), y.getUnits()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨CssPoint2D⟩: ⟨x⟩ ⟨y⟩";
    }
}
