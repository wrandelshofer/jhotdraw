/* @(#)CssRegexConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;

/**
 * CssRegexConverter.
 *
 * Parses the following EBNF:
 * <pre>
 * Regex := "none" | "regex(" Find  ","   [ Replace ] ")" ;
 * Find := TT_STRING;
 * Replace := TT_STRING;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssRegexConverter implements Converter<Regex> {

    private final CssStringConverter stringConverter = new CssStringConverter();
    private final boolean nullable;

    /**
     * Creates a new instance.
     *
     * @param nullable Whether the regular expression is nullable.
     */
    public CssRegexConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨RegReplace⟩: none | replace(⟨Match⟩, ⟨Replace⟩)"
                + "\nFormat of ⟨Match⟩: \"match\""
                + "\nFormat of ⟨Replace⟩: \"replacement\"";
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
        String find = value.getFind();
        if (find == null) {
            out.append("none");
            return;
        }
        out.append("replace(");

        stringConverter.toString(out, find);

        String replace = value.getReplace();
        if (replace != null) {
            out.append(", ");
            stringConverter.toString(out, replace);
        }
        out.append(')');
    }

    @Override
    public Regex fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizerInterface tt = new CssTokenizer(new StringReader(in.toString()));
        tt.setSkipWhitespaces(true);
        String find = null;
        String replace = null;

        switch (tt.nextToken()) {
            case CssTokenizer.TT_FUNCTION:
                if ("replace".equals(tt.currentStringValue())) {
                } else {
                    throw new ParseException("\"replace(\" or \"none\" expected", tt.getStartPosition());
                }
                break;
            case CssTokenizer.TT_IDENT:
                if ("none".equals(tt.currentStringValue())) {
                    tt.skipWhitespace();
                    in.position(tt.getStartPosition());
                    return new Regex();
                } else {
                    throw new ParseException("\"replace(\" or \"none\" expected", tt.getStartPosition());
                }
            default:
                throw new ParseException("find string expected", tt.getStartPosition());
        }
        switch (tt.nextToken()) {
            case CssTokenizer.TT_STRING:
                find = tt.currentStringValue();
                break;
            default:
                throw new ParseException("find string expected", tt.getStartPosition());
        }

        switch (tt.nextToken()) {
            case ',':
                break;
            default:
                tt.pushBack();
                break;
        }

        switch (tt.nextToken()) {
            case CssTokenizer.TT_STRING:
                replace = tt.currentStringValue();
                break;
            case CssTokenizer.TT_EOF:
                break;
            default:
                throw new ParseException("replace string expected", tt.getStartPosition());
        }
        switch (tt.nextToken()) {
            case ')':
                break;
            default:
                throw new ParseException("closing bracket \")\" expected", tt.getStartPosition());
        }
        tt.skipWhitespace();
        in.position(tt.getStartPosition());
        return new Regex(find, replace);
    }

    @Override
    public Regex getDefaultValue() {
        return new Regex();
    }

}
