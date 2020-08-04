/*
 * @(#)CssBoundingBoxConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.geometry.BoundingBox;
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
 * Converts a {@code javafx.geometry.BoundingBox} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class CssBoundingBoxConverter extends AbstractCssConverter<BoundingBox> {
    private final boolean withSpace;
    private final boolean withComma;

    public CssBoundingBoxConverter(boolean nullable) {
        this(nullable, true, false);
    }

    public CssBoundingBoxConverter(boolean nullable, boolean withSpace, boolean withComma) {
        super(nullable);
        this.withSpace = withSpace || !withComma;
        this.withComma = withComma;
    }

    @NonNull
    @Override
    public BoundingBox parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final double x, y, width, height;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨min-x⟩ expected.");
        x = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨min-y⟩ expected.");
        y = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨width⟩ expected.");
        width = tt.currentNumberNonNull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨height⟩ expected.");
        height = tt.currentNumberNonNull().doubleValue();

        return new BoundingBox(x, y, width, height);
    }

    @Override
    protected <TT extends BoundingBox> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getMinX()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getMinY()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getWidth()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getHeight()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨BoundingBox⟩: ⟨min-x⟩ ⟨min-y⟩ ⟨width⟩ ⟨height⟩";
    }
}
