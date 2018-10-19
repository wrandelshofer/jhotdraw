/* @(#)CssRectangle2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.geometry.Rectangle2D;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Rectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssRectangle2DConverter  extends AbstractCssConverter<Rectangle2D> {
    private final boolean withSpace;

    public CssRectangle2DConverter(boolean nullable) {
        this(nullable, true);
    }

    public CssRectangle2DConverter(boolean nullable, boolean withSpace) {
        super(nullable);
        this.withSpace = withSpace;
    }

    @Nonnull
    @Override
    public Rectangle2D parseNonnull(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.setSkipWhitespaces(true);
        final double x, y, width, height;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Rectangle2D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Rectangle2D⟩: ⟨y⟩ expected.");
        y = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Rectangle2D⟩: ⟨width⟩ expected.");
        width = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Rectangle2D⟩: ⟨height⟩ expected.");
        height = tt.currentNumberNonnull().doubleValue();

        return new Rectangle2D(x, y, width, height);
    }

    @Override
    protected <TT extends Rectangle2D> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out) {
        out.accept(new Token(CssTokenType.TT_NUMBER, value.getMinX()));
        out.accept(new Token(CssTokenType.TT_COMMA));
        if (withSpace) {
            out.accept(new Token(CssTokenType.TT_S, " "));
        }
        out.accept(new Token(CssTokenType.TT_NUMBER, value.getMinY()));
        out.accept(new Token(CssTokenType.TT_COMMA));
        if (withSpace) {
            out.accept(new Token(CssTokenType.TT_S, " "));
        }
        out.accept(new Token(CssTokenType.TT_NUMBER, value.getWidth()));
        out.accept(new Token(CssTokenType.TT_COMMA));
        if (withSpace) {
            out.accept(new Token(CssTokenType.TT_S, " "));
        }
        out.accept(new Token(CssTokenType.TT_NUMBER, value.getHeight()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Rectangle2D⟩: ⟨x⟩ ⟨y⟩ ⟨width⟩ ⟨height⟩";
    }
}
