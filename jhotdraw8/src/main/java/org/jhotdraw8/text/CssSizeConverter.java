/* @(#)CssSizeConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.draw.io.IdFactory;
import org.jhotdraw8.draw.io.SimpleIdFactory;

/**
 * CssSizeConverter.
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
 * // FIXME should return a Size object and not just a Double.
 *
 * @author Werner Randelshofer
 */
public class CssSizeConverter implements Converter<Double> {

    private final static NumberConverter numberConverter = new NumberConverter();
    private final static SimpleIdFactory defaultFactory = new SimpleIdFactory();

    @Override
    public void toString(Appendable out, IdFactory idFactory, Double value) throws IOException {
        if (value == null) {
            out.append("none");
            return;
        }
        numberConverter.toString(out, idFactory, value);
    }

    @Override
    public Double fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        if (idFactory == null) {
            idFactory = defaultFactory;
        }
        CssTokenizerInterface tt = new CssTokenizer(buf);
        tt.skipWhitespace();
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "none".equals(tt.currentStringValue())) {
            tt.skipWhitespace();
            return null;
        } else {
            tt.pushBack();
        }
        Double value = null;
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_DIMENSION:
                value = tt.currentNumericValue().doubleValue();
                value = idFactory.convert(value, tt.currentUnitValue(), "px");
                break;
            case CssTokenizerInterface.TT_PERCENTAGE:
                value = tt.currentNumericValue().doubleValue();
                value = idFactory.convert(value, "%", "px");
                break;
            case CssTokenizerInterface.TT_NUMBER:
                value = tt.currentNumericValue().doubleValue();
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
                        throw new ParseException("number expected:" + tt.currentStringValue(), tt.getPosition());
                }
                break;
            }
            default:
                throw new ParseException("number expected", tt.getPosition());
        }
        tt.skipWhitespace();
        return value;
    }

    @Override
    public Double getDefaultValue() {
        return 0.0;
    }
}
