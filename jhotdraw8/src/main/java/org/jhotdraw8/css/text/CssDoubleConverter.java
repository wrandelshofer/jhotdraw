/* @(#)CssDoubleConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.io.IdFactory;

/**
 * CssDoubleConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Size := Double, [Unit] ;
 * Unit := ("px"|"mm"|"cm"|in"|"pt"|"pc"]"em"|"ex") ;
 * </pre>
 * <p>
 * // FIXME should return a Size object and not just a Double.
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
    public Double parseNonnull(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.skipWhitespace();
        switch (tt.nextToken()) {
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
    public <TT extends Double> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out) {
        double v = value;
        if (value.isInfinite()) {
                out.accept(new Token(CssTokenType.TT_IDENT, (v > 0) ? "INF" : "-INF"));
        } else if (value.isNaN()) {
            out.accept(new Token(CssTokenType.TT_IDENT, "NaN"));
        } else {
            out.accept(new Token(CssTokenType.TT_NUMBER, value));
        }
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Double⟩: ⟨double⟩";
    }

}
