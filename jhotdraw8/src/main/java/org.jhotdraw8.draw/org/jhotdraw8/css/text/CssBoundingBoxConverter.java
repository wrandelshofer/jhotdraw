/*
 * @(#)CssBoundingBoxConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.geometry.BoundingBox;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.BoundingBox} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
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

    @Nonnull
    @Override
    public BoundingBox parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final double x, y, width, height;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨min-x⟩ expected.");
        x = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨min-y⟩ expected.");
        y = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨width⟩ expected.");
        width = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨height⟩ expected.");
        height = tt.currentNumberNonnull().doubleValue();

        return new BoundingBox(x, y, width, height);
    }

    @Override
    protected <TT extends BoundingBox> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
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
