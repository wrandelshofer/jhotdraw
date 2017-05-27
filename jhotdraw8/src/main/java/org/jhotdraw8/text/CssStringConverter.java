/* @(#)CssStringConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;

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
public class CssStringConverter implements Converter<String> {
private final String helpText;
private final char quoteChar;
private final String defaultValue;

    public CssStringConverter() {
        this('\'',null);
    }

    public CssStringConverter(char quoteChar, String helpText) {
        this.quoteChar=quoteChar;
        this.helpText = helpText;
        defaultValue=""+quoteChar+quoteChar;
    }
    

    @Override
    public String fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));
        if (tt.nextToken() != CssTokenizer.TT_STRING) {
            throw new ParseException("Css String expected. " + tt.currentToken(), buf.position());
        }
        return tt.currentStringValue();
    }

    @Override
    public String getHelpText() {
        return helpText;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, String value) throws IOException {
        out.append(quoteChar);
        for (char ch : value.toCharArray()) {
            switch (ch) {
                case ' ':
                    out.append(ch);
                    break;
                case '\\':
                    out.append('\\');
                    out.append('\\');
                    break;
                case '\n':
                    out.append('\\');
                    out.append('\n');
                    break;
                default:
                    if (ch == quoteChar) {
                        out.append('\\');
                    out.append(quoteChar);
                    }else{
                    
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
                    
                    }
                    break;
            }
        }
        out.append(quoteChar);
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }
    
    
    
}
