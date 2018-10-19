/* @(#)CssStringConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.text.Converter;

/**
 * Converts an {@code String} to a quoted CSS {@code String}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssStringConverter implements Converter<String> {
    private final String helpText;
    private final char quoteChar;
    @Nonnull
    private final String defaultValue;

    public CssStringConverter() {
        this('\"', null);
    }

    public CssStringConverter(char quoteChar, String helpText) {
        this.quoteChar = quoteChar;
        this.helpText = helpText;
        defaultValue = "" + quoteChar + quoteChar;
    }


    @Nullable
    @Override
    public String fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));
        if (tt.nextToken()==CssTokenType.TT_IDENT&&CssTokenType.IDENT_NONE.equals(tt.currentString())) {
            return null;
        }else{
            tt.pushBack();
        }
        if (tt.nextToken() != CssTokenType.TT_STRING) {
            throw new ParseException("Css String expected. " + tt.currentToken(), buf.position());
        }
        return tt.currentString();
    }

    @Override
    public String getHelpText() {
        return helpText;
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable String value) throws IOException {
        if (value == null) {
            out.append(CssTokenType.IDENT_NONE);
        } else {
            out.append(new Token(CssTokenType.TT_STRING, value, quoteChar).fromToken());
        }
    }

    @Nonnull
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }


}
