/* @(#)Point2DConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.geometry.Point2D;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point2DConverter extends AbstractCssConverter<Point2D> {

    private final boolean withSpace;

    public Point2DConverter(boolean nullable) {
        this(nullable, true);
    }

    public Point2DConverter(boolean nullable, boolean withSpace) {
        super(nullable);
        this.withSpace = withSpace;
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Point2D⟩: ⟨x⟩,⟨y⟩";
    }

    @Nonnull
    @Override
    public Point2D parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final double x, y;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Point2D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Point2D⟩: ⟨y⟩ expected.");
        y = tt.currentNumberNonnull().doubleValue();

        return new Point2D(x, y);
    }

    @Override
    protected <TT extends Point2D> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
        out.accept(new CssToken(CssTokenType.TT_COMMA));
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
    }
}
