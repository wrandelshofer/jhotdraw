/* @(#)CssStringConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.shape.SVGPath;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.draw.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;

/**
 * Converts an SVG path to a CSS String.
 * <p>
 * The null value will be converted to the CSS identifier "none".
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSvgPathConverter implements Converter<SVGPath> {

    private final boolean nullable;

    public CssSvgPathConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public SVGPath fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));

        SVGPath p = null;
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if (!nullable) {
                throw new ParseException("String expected. " + tt.currentToken(), buf.position());
            }
            if (!"none".equals(tt.currentStringValue())) {
                throw new ParseException("none or String expected. " + tt.currentToken(), buf.position());
            }
            p = null;
        } else {
            if (tt.currentToken() != CssTokenizer.TT_STRING) {
                throw new ParseException("Css String expected. " + tt.currentToken(), buf.position());
            }
            p = new SVGPath();
            p.setContent(tt.currentStringValue());
        }
        buf.position(buf.limit());

        return p;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, SVGPath value) throws IOException {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is null");
            }
            out.append("none");
            return;
        }
        out.append('"');
        for (char ch : value.getContent().toCharArray()) {
            switch (ch) {
                case '"':
                    out.append('\\');
                    out.append('"');
                    break;
                case ' ':
                    out.append(ch);
                    break;
                case '\n':
                    out.append('\\');
                    out.append('\n');
                    break;
                default:
                    if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                        out.append('\\');
                        String hex = Integer.toHexString(ch);
                        for (int i = 0, n = 6 - hex.length(); i < n; i++) {
                            out.append('0');
                        }
                        out.append(hex);
                    } else {
                        out.append(ch);
                    }
                    break;
            }
        }
        out.append('"');
    }

    @Override
    public SVGPath getDefaultValue() {
        SVGPath p = new SVGPath();
        return p;
    }
}
