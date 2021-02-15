/*
 * @(#)CssRectangle2DConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.CssRectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class CssRectangle2DConverter extends AbstractCssConverter<CssRectangle2D> {
    private final boolean withSpace;
    private final boolean withComma;

    private CssSizeConverter sizeConverter = new CssSizeConverter(false);

    public CssRectangle2DConverter(boolean nullable) {
        this(nullable, true, false);
    }

    public CssRectangle2DConverter(boolean nullable, boolean withSpace, boolean withComma) {
        super(nullable);
        this.withSpace = withSpace || !withComma;
        this.withComma = withComma;
    }

    @Override
    public @NonNull CssRectangle2D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final CssSize x, y, width, height;
        x = sizeConverter.parse(tt, idResolver);
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        y = sizeConverter.parse(tt, idResolver);
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        width = sizeConverter.parse(tt, idResolver);
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        height = sizeConverter.parse(tt, idResolver);

        return new CssRectangle2D(x, y, width, height);
    }

    @Override
    protected <TT extends CssRectangle2D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        sizeConverter.produceTokens(value.getMinX(), idSupplier, out);
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        sizeConverter.produceTokens(value.getMinY(), idSupplier, out);
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        sizeConverter.produceTokens(value.getWidth(), idSupplier, out);
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        sizeConverter.produceTokens(value.getHeight(), idSupplier, out);
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨CssRectangle2D⟩: ⟨x⟩ ⟨y⟩ ⟨width⟩ ⟨height⟩";
    }
}
