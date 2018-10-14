/* @(#)WordConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
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
 * @version $Id$
 */
public class CssIdentConverter implements Converter<String> {

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nonnull String value) throws IOException {
        out.append(new CssToken(CssToken.TT_IDENT,value).toCss());
    }

    @Nullable
    @Override
    public String fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(in));
        if (tt.nextToken() != CssToken.TT_IDENT) {
            throw new ParseException("Css Identifier expected", in.position());
        }
        return tt.currentStringValue();
    }

    @Nonnull
    @Override
    public String getDefaultValue() {
        return "";
    }
}
