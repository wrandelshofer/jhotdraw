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
 * Regex := '/', Find, '/', [ Replace, '/' ] ;
 * Find := RegexFindPattern;
 * Replace := RegexReplacePattern;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CssRegexConverter implements Converter<Regex> {

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
        appendExpr(out, value.getFind());
        out.append('/');
        if (!"$1".equals(value.getReplace())) {
            appendExpr(out, value.getReplace());
            out.append('/');
        }
    }

    private void appendExpr(Appendable out, String expr) throws IOException {
        out.append(expr.replace("/", "\\/"));
    }

    @Override
    public Regex fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new StringReader(in.toString()));

        StringBuilder find = new StringBuilder();
        StringBuilder replace = new StringBuilder();

        tt.skipWhitespace();
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if (!nullable) {
                throw new ParseException("'/' expected", tt.getPosition());
            }
            if (!"none".equals(tt.currentStringValue())) {
                throw new ParseException("none or '/' expected", tt.getPosition());
            }
            return null;
        } else {
            tt.pushBack();
        }

        if (tt.nextToken() != '/') {
            throw new ParseException("first / expected", tt.getPosition());
        }
        while (tt.nextToken() != CssTokenizer.TT_EOF && tt.currentToken() != '/') {
            switch (tt.currentToken()) {
            case CssTokenizer.TT_AT_KEYWORD:
                find.append('@');
                find.append(tt.currentStringValue());
                break;
            case CssTokenizer.TT_HASH:
                find.append('#');
                find.append(tt.currentStringValue());
                break;
            default:
                find.append(tt.currentStringValue());
                break;
            }
        }
        if (tt.currentToken() != '/') {
            throw new ParseException("second / expected", tt.getPosition());
        }
        while (tt.nextToken() != CssTokenizer.TT_EOF && tt.currentToken() != '/') {
            replace.append(tt.currentStringValue());
        }
        if (tt.currentToken() != '/') {
            if (replace.toString().trim().length() > 0) {
                throw new ParseException("third / expected", tt.getPosition());
            }
            replace.setLength(0);
            replace.append("$1");
        }
        tt.skipWhitespace();

        in.position(in.limit());
        return new Regex(find.toString(), replace.toString());
    }

    @Override
    public Regex getDefaultValue() {
        return new Regex();
    }

}
