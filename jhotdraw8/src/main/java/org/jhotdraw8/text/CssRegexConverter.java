/* @(#)CssRegexConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.io.IdFactory;

/**
 * CssRegexConverter.
 *
 * Parses the following EBNF:
 * <pre>
 * RegexReplace := "none" | "regex(" Find  ","   [ Replace ] ")" ;
 * Find := TT_STRING;
 * Replace := TT_STRING;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssRegexConverter implements Converter<RegexReplace> {

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

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨RegReplace⟩: none | replace(⟨Match⟩, ⟨Replace⟩)"
                + "\nFormat of ⟨Match⟩: \"match\""
                + "\nFormat of ⟨Replace⟩: \"replacement\"";
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable RegexReplace value) throws IOException {
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

    @Nullable
    @Override
    public RegexReplace fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizerAPI tt = new CssTokenizer(new StringReader(in.toString()));
        tt.setSkipWhitespaces(true);
        String find = null;
        String replace = null;

        String msg = nullable ? "\"replace(\" or \"none\" expected" : "\"replace(\" expected";

        switch (tt.nextToken()) {
            case CssTokenType.TT_FUNCTION:
                if ("replace".equals(tt.currentStringValue())) {
                } else {
                    throw new ParseException(msg, tt.getStartPosition());
                }
                break;
            case CssTokenType.TT_IDENT:
                if ("none".equals(tt.currentStringValue())) {
                    tt.skipWhitespace();
                    in.position(tt.getStartPosition());
                    if (nullable) {
                        return null;
                    }
                    throw new ParseException(msg, tt.getStartPosition());
                } else {
                    throw new ParseException(msg, tt.getStartPosition());
                }
            default:
                throw new ParseException(msg, tt.getStartPosition());
        }
        switch (tt.nextToken()) {
            case CssTokenType.TT_STRING:
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
            case CssTokenType.TT_STRING:
                replace = tt.currentStringValue();
                break;
            case CssTokenType.TT_EOF:
                break;
            default:
                replace = null;
                tt.pushBack();
        }
        switch (tt.nextToken()) {
            case ')':
                break;
            default:
                throw new ParseException("closing bracket \")\" expected", tt.getStartPosition());
        }
        tt.skipWhitespace();
        in.position(tt.getStartPosition());
        return new RegexReplace(find, replace);
    }

    @Nullable
    @Override
    public RegexReplace getDefaultValue() {
        return null;
    }

}
