/* @(#)CssRectangle2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.CssDimensionRectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDimensionRectangle2DConverter extends AbstractCssConverter<CssDimensionRectangle2D> {
    private final boolean withSpace;
    private final boolean withComma;

    public CssDimensionRectangle2DConverter(boolean nullable) {
        this(nullable, true, false);
    }

    public CssDimensionRectangle2DConverter(boolean nullable, boolean withSpace, boolean withComma) {
        super(nullable);
        this.withSpace = withSpace || !withComma;
        this.withComma = withComma;
    }

    @Nonnull
    @Override
    public CssDimensionRectangle2D parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final CssDimension x, y, width, height;
        x = parseDimension(tt,"x");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        y = parseDimension(tt,"y");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        width = parseDimension(tt,"width");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        height = parseDimension(tt,"height");

        return new CssDimensionRectangle2D(x, y, width, height);
    }

    private CssDimension parseDimension(CssTokenizer tt, String variable) throws ParseException, IOException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssDimension(tt.currentNumber().doubleValue(),null);
            case CssTokenType.TT_DIMENSION:
                return new CssDimension(tt.currentNumber().doubleValue(),tt.currentString());
            default:
                throw new ParseException(" ⟨CssDimensionRectangle2D⟩: ⟨"+variable+"⟩ expected.",tt.getStartPosition());
        }
    }

    @Override
    protected <TT extends CssDimensionRectangle2D> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        CssDimension minX = value.getMinX();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, minX.getUnits(),minX.getValue()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssDimension minY = value.getMinY();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, minY.getUnits(),minY.getValue()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssDimension width = value.getWidth();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, width.getUnits(),width.getValue()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssDimension height = value.getHeight();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, height.getUnits(),height.getValue()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨CssDimensionRectangle2D⟩: ⟨x⟩ ⟨y⟩ ⟨width⟩ ⟨height⟩";
    }
}
