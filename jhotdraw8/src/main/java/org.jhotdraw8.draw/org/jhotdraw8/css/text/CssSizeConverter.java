/* @(#)CssDoubleConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

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
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSizeConverter implements CssConverter<CssSize> {

    private final boolean nullable;

    public CssSizeConverter(boolean nullable) {
        this.nullable = nullable;
    }


    @Nullable
    @Override
    public CssSize parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (nullable) {
            if (tt.next() == CssTokenType.TT_IDENT && CssTokenType.IDENT_NONE.equals(tt.currentString())) {
                //tt.skipWhitespace();
                return nullable ? null : CssSize.ZERO;
            } else {
                tt.pushBack();
            }
        }
        Number value = null;
        String units;
        switch (tt.next()) {
            case CssTokenType.TT_DIMENSION:
                value = tt.currentNumberNonnull();
                units = tt.currentString();
                break;
            case CssTokenType.TT_PERCENTAGE:
                value = tt.currentNumberNonnull();
                units = "%";
                break;
            case CssTokenType.TT_NUMBER:
                value = tt.currentNumberNonnull();
                units = null;
                break;
            case CssTokenType.TT_IDENT: {
                units = null;
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
                break;
            }
            default:
                throw new ParseException("number expected", tt.getStartPosition());
        }
        return new CssSize(value.doubleValue(), units);
    }


    @Override
    public <TT extends CssSize> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else if (value.getUnits() == null || "".equals(value.getUnits())) {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getValue(), ""));
        } else {
            switch (value.getUnits()) {
                case "%":
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, value.getValue(), "%"));
                    break;
                default:
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, value.getValue(), value.getUnits()));
                    break;
            }
        }
    }

    @Nullable
    @Override
    public CssSize getDefaultValue() {
        return null;
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Size⟩: ⟨size⟩ | ⟨percentage⟩% | ⟨size⟩⟨Units⟩"
                + "\nFormat of ⟨Units⟩: mm | cm | em | ex | in | pc | px | pt";
    }

}
