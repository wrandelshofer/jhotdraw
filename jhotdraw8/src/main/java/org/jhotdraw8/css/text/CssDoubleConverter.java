/* @(#)CssDoubleConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.io.IdFactory;

/**
 * CssDoubleConverter.
 * <p>
 * Parses an attribute value of type double.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDoubleConverter extends AbstractCssConverter<Double> {

    public CssDoubleConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public Double parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return tt.currentNumberNonnull().doubleValue();
            case CssTokenType.TT_IDENT: {
                double value;
                switch (tt.currentStringNonnull()) {
                    case "INF":
                        value = Double.POSITIVE_INFINITY;
                        break;
                    case "-INF":
                        value = Double.NEGATIVE_INFINITY;
                        break;
                    case "NaN":
                        value = Double.NaN;
                        break;
                    default:
                        throw new ParseException("number expected:" + tt.currentString(), tt.getStartPosition());
                }
                return value;
            }
            default:
                throw new ParseException("⟨Double⟩: number expected.", tt.getStartPosition());
        }
    }

    @Override
    public <TT extends Double> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        double v = value;
        if (value.isInfinite()) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, (v > 0) ? "INF" : "-INF"));
        } else if (value.isNaN()) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, "NaN"));
        } else {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value));
        }
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Double⟩: ⟨double⟩";
    }

}
