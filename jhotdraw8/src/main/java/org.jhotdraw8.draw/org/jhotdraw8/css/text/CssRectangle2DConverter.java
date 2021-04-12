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

import static org.jhotdraw8.css.text.CssSizeConverter.parseSize;

/**
 * Converts a {@code javafx.geometry.CssRectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class CssRectangle2DConverter extends AbstractCssConverter<CssRectangle2D> {
    private final boolean withSpace;
    private final boolean withComma;

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
        x = parseSize(tt, "x");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        y = parseSize(tt, "y");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        width = parseSize(tt, "width");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        height = parseSize(tt, "height");

        return new CssRectangle2D(x, y, width, height);
    }

    @Override
    protected <TT extends CssRectangle2D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        CssSize x = value.getMinX();
        CssSize y = value.getMinY();
        CssSize width = value.getWidth();
        CssSize height = value.getHeight();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, x.getValue(), x.getUnits()));
        produceDelimiter(out);
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, y.getValue(), y.getUnits()));
        produceDelimiter(out);
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, width.getValue(), width.getUnits()));
        produceDelimiter(out);
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, height.getValue(), height.getUnits()));
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
    public String getHelpText() {
        return "Format of ⟨CssRectangle2D⟩: ⟨x⟩ ⟨y⟩ ⟨width⟩ ⟨height⟩";
    }
}
