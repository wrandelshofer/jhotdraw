/* @(#)CssNumberConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
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

    @Override
    public Number fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        if (buf == null) {
            return null;
        }
        int start = buf.position();
        CssTokenizerInterface tt = new CssTokenizer(new CharBufferReader(buf));
        Number sz = parseNumber(tt);
        buf.position(start + tt.getEndPosition());
        return sz;
    }

    @Override
    public Number getDefaultValue() {
        return 0;
    }

    public Number parseNumber(CssTokenizerInterface tt) throws ParseException, IOException {
        tt.skipWhitespace();
        if (nullable && tt.nextToken() == CssTokenizer.TT_IDENT && "none".equals(tt.currentStringValue())) {
            //tt.skipWhitespace();
            return null;
        } else {
            tt.pushBack();
        }
        Number value = null;
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_NUMBER:
                value = tt.currentNumericValue();
                break;
            case CssTokenizerInterface.TT_IDENT: {
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
    public void toString(Appendable out, IdFactory idFactory, Number value) throws IOException {
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

    @Override
    public String getHelpText() {
        return "Format of ⟨Numer⟩: ⟨number⟩";
    }

}
