/*
 * @(#)CssStringOrIdentConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * Converts an {@code String} from/to a CSS ident-token or a CSS string-token.
 *
 * @author Werner Randelshofer
 */
public class CssStringOrIdentConverter implements Converter<String> {

    @Override
    public @Nullable String fromString(@NonNull CharBuffer buf, @Nullable IdResolver idResolver) throws ParseException, IOException {
        StreamCssTokenizer tt = new StreamCssTokenizer(new CharBufferReader(buf));
        if (tt.next() != CssTokenType.TT_STRING && tt.current() != CssTokenType.TT_IDENT) {
            throw new ParseException("Css String or Ident expected. " + tt.current(), buf.position());
        }
        return tt.currentString();
    }

    @Override
    public void toString(@NonNull Appendable out, @Nullable IdSupplier idSupplier, @NonNull String value) throws IOException {
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

    @Override
    public @NonNull String getDefaultValue() {
        return "";
    }
}
