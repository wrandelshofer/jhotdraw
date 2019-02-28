/* @(#)Rectangle2DConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.CssRectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
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

    @Nonnull
    @Override
    public CssRectangle2D parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final CssSize x, y, width, height;
        x = parseDimension(tt, "x");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        y = parseDimension(tt, "y");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        width = parseDimension(tt, "width");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        height = parseDimension(tt, "height");

        return new CssRectangle2D(x, y, width, height);
    }

    private CssSize parseDimension(CssTokenizer tt, String variable) throws ParseException, IOException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssSize(tt.currentNumberNonnull().doubleValue());
            case CssTokenType.TT_DIMENSION:
                return new CssSize(tt.currentNumberNonnull().doubleValue(), tt.currentStringNonnull());
            case CssTokenType.TT_IDENT:
                switch (tt.currentStringNonnull()) {
                    case "INF":
                        return new CssSize(Double.POSITIVE_INFINITY);
                    case "-INF":
                        return new CssSize(Double.NEGATIVE_INFINITY);
                    case "NaN":
                        return new CssSize(Double.NaN);
                    default:
                        throw new ParseException(" ⟨CssRectangle2D⟩: ⟨" + variable + "⟩ expected.", tt.getStartPosition());
                }
            default:
                throw new ParseException(" ⟨CssRectangle2D⟩: ⟨" + variable + "⟩ expected.", tt.getStartPosition());
        }
    }

    @Override
    protected <TT extends CssRectangle2D> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        CssSize minX = value.getMinX();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, minX.getValue(), minX.getUnits()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssSize minY = value.getMinY();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, minY.getValue(), minY.getUnits()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssSize width = value.getWidth();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, width.getValue(), width.getUnits()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssSize height = value.getHeight();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, height.getValue(), height.getUnits()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨CssRectangle2D⟩: ⟨x⟩ ⟨y⟩ ⟨width⟩ ⟨height⟩";
    }
}
