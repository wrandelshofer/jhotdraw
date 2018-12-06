/* @(#)CssNumberConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

/**
 * CssNumberConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Number := Double ;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssNumberConverter extends AbstractCssConverter<Number> {

    public CssNumberConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public Number parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        switch (tt.current()) {
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
    public <TT extends Number> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value));
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Number⟩: ⟨number⟩";
    }

}
