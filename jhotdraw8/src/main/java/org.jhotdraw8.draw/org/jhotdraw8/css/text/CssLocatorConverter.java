/*
 * @(#)CssLocatorConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * CssLocatorConverter.
 * <p>
 * Currently converts relative locators only.
 *
 * @author Werner Randelshofer
 */
public class CssLocatorConverter extends AbstractCssConverter<Locator> {
    public static final String RELATIVE_FUNCTION = "relative";

    public CssLocatorConverter() {
        this(false);
    }


    public CssLocatorConverter(boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public Locator parseNonNull(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "Locator: function 'relative(' expected.");
        if (!RELATIVE_FUNCTION.equals(tt.currentStringNonNull())) {
            throw tt.createParseException("Locator: function 'relative(' expected.");
        }

        double x, y;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                x = tt.currentNumberNonNull().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                x = tt.currentNumberNonNull().doubleValue() / 100.0;
                break;
            default:
                throw tt.createParseException("BoundsLocator: x-value expected.");
        }
        switch (tt.next()) {
            case ',':
                break;
            default:
                tt.pushBack();
                break;
        }
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                y = tt.currentNumberNonNull().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                y = tt.currentNumberNonNull().doubleValue() / 100.0;
                break;
            default:
                throw tt.createParseException("BoundsLocator: y-value expected.");
        }
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "BoundsLocator: ')' expected.");

        return new BoundsLocator(x, y);
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨Locator⟩: relative(⟨x⟩%,⟨y⟩%)";
    }

    @Override
    protected <TT extends Locator> void produceTokensNonNull(@NonNull TT value, @Nullable IdFactory idFactory, @NonNull Consumer<CssToken> out) {
        if (value instanceof BoundsLocator) {
            BoundsLocator rl = (BoundsLocator) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, RELATIVE_FUNCTION));
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, rl.getRelativeX() * 100));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, rl.getRelativeY() * 100));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        } else {
            throw new UnsupportedOperationException("only BoundsLocator supported, value:" + value);
        }
    }
}
