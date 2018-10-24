/* @(#)CssStringConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.text.Converter;

/**
 * Converts an {@code String} from/to a CSS ident-token or a CSS string-token.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssStringOrIdentConverter implements Converter<String> {

    @Nullable
    @Override
    public String fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        StreamCssTokenizer tt = new StreamCssTokenizer(new CharBufferReader(buf));
        if (tt.next() != CssTokenType.TT_STRING && tt.current() != CssTokenType.TT_IDENT) {
            throw new ParseException("Css String or Ident expected. " + tt.current(), buf.position());
        }
        return tt.currentString();
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nonnull String value) throws IOException {
        StringBuffer buf = new StringBuffer();
        boolean isIdent = true;
        buf.append('"');
        for (char ch : value.toCharArray()) {
            switch (ch) {
                case '"':
                    buf.append('\\');
                    buf.append('"');
                    isIdent = false;
                    break;
                case ' ':
                    buf.append(ch);
                    isIdent = false;
                    break;
                case '\n':
                    buf.append('\\');
                    buf.append('\n');
                    isIdent = false;
                    break;
                default:
                    if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                        buf.append('\\');
                        String hex = Integer.toHexString(ch);
                        for (int i = 0, n = 6 - hex.length(); i < n; i++) {
                            buf.append('0');
                        }
                        buf.append(hex);
                    } else {
                        buf.append(ch);
                    }
                    break;
            }
        }
        buf.append('"');
        if (isIdent) {
            out.append(value);
        } else {
            out.append(buf.toString());
        }
    }

    @Nonnull
    @Override
    public String getDefaultValue() {
        return "";
    }
}
