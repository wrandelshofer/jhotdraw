/* @(#)CssStringConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import org.jhotdraw.css.CssTokenizer;
import org.jhotdraw.draw.io.IdFactory;
import org.jhotdraw.io.CharBufferReader;

/**
 * Converts an {@code String} to a quoted CSS {@code String}.
 * <pre>
 * unicode       = '\' , ( 6 * hexd
 *                       | hexd , 5 * [hexd] , w
 *                       );
 * escape        = ( unicode
 *                 | '\' , -( newline | hexd)
 *                 ) ;
 * string        = string1 | string2 ;
 * string1       = '"' , { -( '"' ) | '\\' , newline |  escape } , '"' ;
 * string2       = "'" , { -( "'" ) | '\\' , newline |  escape } , "'" ;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSvgPathConverter implements Converter<SVGPath> {


    @Override
    public SVGPath fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));
        if (tt.nextToken() != CssTokenizer.TT_STRING) {
            throw new ParseException("Css String expected. "+tt.currentToken(), buf.position());
        }
        SVGPath p=new SVGPath();
        p.setContent(tt.currentStringValue());
        return p;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, SVGPath value) throws IOException {
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
                    if (Character.isISOControl(ch)||Character.isWhitespace(ch)) {
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
        SVGPath p= new SVGPath();
        return p;
    }
}
