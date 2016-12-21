/* @(#)WordConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;

/**
 * CssIdentifier converter.
 * <pre>
 * ident         = [ '-' ] , nmstart , { nmchar } ;
 * name          = { nmchar }- ;
 * nmstart       = '_' | letter | nonascii | escape ;
 * nonascii      = ? U+00A0 through U+10FFFF ? ;
 * letter        = ? 'a' through 'z' or 'A' through 'Z' ?
 * unicode       = '\' , ( 6 * hexd
 *                       | hexd , 5 * [hexd] , w
 *                       );
 * escape        = ( unicode
 *                 | '\' , -( newline | hexd)
 *                 ) ;
 * nmchar        = '_' | letter | digit | '-' | nonascii | escape ;
 * num           = [ '+' | '-' ] ,
 *                 ( { digit }-
 *                 | { digit } , '.' , { digit }-
 *                 )
 *                 [ 'e'  , [ '+' | '-' ] , { digit }- ] ;
 * digit         = ? '0' through '9' ?
 * letter        = ? 'a' through 'z' ? | ? 'A' through 'Z' ? ;
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssIdentConverter implements Converter<String> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, String value) throws IOException {
        Reader r = new StringReader(value);
        int ch = r.read();

        // identifier may start with '-'
        if (ch == '-') {
            out.append((char) ch);
            ch = r.read();
        }

        if (ch == -1) {
            throw new IllegalArgumentException("nmstart missing! " + value);
        }

        // escape nmstart if necessary
        if (ch == '_' || 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || 0xA0 <= ch && ch <= 0x10FFFF) {
            out.append((char) ch);
        } else {
            switch (ch) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '\n':
                    String hex = Integer.toHexString(ch);
                    out.append('\\');
                    out.append(hex);
                    if (hex.length() < 6) {
                        out.append(' ');
                    }
                    break;
                default:
                    out.append('\\');
                    out.append((char) ch);
                    break;
            }
        }

        while (-1 != (ch = r.read())) {
            // escape nmchar if necessary
            if (ch == '_' || 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9' || ch == '-' || 0xA0 <= ch && ch <= 0x10FFFF) {
                out.append((char) ch);
            } else {
                out.append('\\');
                out.append((char) ch);
            }
        }

    }

    @Override
    public String fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(in));
        if (tt.nextToken() != CssTokenizer.TT_IDENT) {
            throw new ParseException("Css Identifier expected", in.position());
        }
        return tt.currentStringValue();
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
