/* @(#)CssNumberConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.io.CharBufferReader;
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
 * // FIXME should return a Size object and not just a Double.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssNumberConverter implements Converter<Number> {

    private final static NumberConverter numberConverter = new NumberConverter();
    private final boolean nullable;

    public CssNumberConverter() {
        this(false);
    }

    public CssNumberConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Nullable
    @Override
    public Number fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        if (buf == null) {
            return null;
        }
        int start = buf.position();
        CssTokenizerAPI tt = new CssTokenizer(new CharBufferReader(buf));
        Number sz = parseNumber(tt);
        buf.position(start + tt.getEndPosition());
        return sz;
    }

    @Nonnull
    @Override
    public Number getDefaultValue() {
        return 0;
    }

    @Nullable
    public Number parseNumber(@Nonnull CssTokenizerAPI tt) throws ParseException, IOException {
        tt.skipWhitespace();
        if (nullable && tt.nextToken() == CssTokenType.TT_IDENT && "none".equals(tt.currentStringValue())) {
            //tt.skipWhitespace();
            return null;
        } else {
            tt.pushBack();
        }
        Number value = null;
        switch (tt.nextToken()) {
            case CssTokenType.TT_NUMBER:
                value = tt.currentNumericValue();
                break;
            case CssTokenType.TT_IDENT: {
                switch (tt.currentStringValue()) {
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
                        throw new ParseException("number expected:" + tt.currentStringValue(), tt.getStartPosition());
                }
                break;
            }
            default:
                throw new ParseException("number expected", tt.getStartPosition());
        }
        return value;
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable Number value) throws IOException {
        if (value == null) {
            if (nullable) {
                out.append("none");
                return;
            } else {
                value = getDefaultValue();
            }
        }
        numberConverter.toString(out, idFactory, value);
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Numer⟩: ⟨number⟩";
    }

}
