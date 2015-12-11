/* @(#)CssRegexConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw.css.CssTokenizer;
import org.jhotdraw.draw.io.IdFactory;

/**
 * CssRegexConverter.
 *
 * Parses the following EBNF:
 * <pre>
 * Regex := '/', [ Find ],  '/', [ Replace ], '/';
 * Find := String;
 * Replace := String;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CssRegexConverter implements Converter<Regex> {

    private final CssStringConverter stringConverter = new CssStringConverter();
    private final boolean nullable;

    public CssRegexConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, Regex value) throws IOException {
        if (value == null) {
            if (nullable) {
                out.append("none");
                return;
            } else {
                throw new IllegalArgumentException("value is null");
            }
        }
        out.append('/');
        if (value.getFind() != null) {
            stringConverter.toString(out, value.getFind());
        }
        out.append('/');
        if (value.getReplace() != null) {
            stringConverter.toString(out, value.getReplace());
        }
        out.append('/');
    }

    private void appendExpr(Appendable out, String expr) throws IOException {
        out.append(expr.replace("/", "\\/"));
    }

    @Override
    public Regex fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new StringReader(in.toString()));

        String find = null;
        String replace = null;

        tt.skipWhitespace();
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if (!nullable) {
                throw new ParseException("'/' expected", tt.getPosition());
            }
            if (!"none".equals(tt.currentStringValue())) {
                throw new ParseException("none or '/' expected", tt.getPosition());
            }
            in.position(in.limit());
            return null;
        } else {
            tt.pushBack();
        }

        if (tt.nextToken() != '/') {
            throw new ParseException("first / expected", tt.getPosition());
        }
        tt.skipWhitespace();
        if (tt.nextToken() == CssTokenizer.TT_STRING) {
            find = tt.currentStringValue();
            tt.skipWhitespace();
        } else {
            tt.pushBack();
        }
        if (tt.nextToken() != '/') {
            throw new ParseException("second / expected", tt.getPosition());
        }
        tt.skipWhitespace();
        if (tt.nextToken() == CssTokenizer.TT_STRING) {
            replace = tt.currentStringValue();
        } else {
            tt.pushBack();
        }
        tt.skipWhitespace();
        if (tt.nextToken() != '/') {
            throw new ParseException("third / expected", tt.getPosition());
        }

        in.position(in.limit());
        return new Regex(find, replace);
    }

    @Override
    public Regex getDefaultValue() {
        return new Regex();
    }

}
