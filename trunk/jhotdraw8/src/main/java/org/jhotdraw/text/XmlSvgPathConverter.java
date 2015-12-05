/* @(#)CssStringConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.shape.SVGPath;
import org.jhotdraw.draw.io.IdFactory;

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
public class XmlSvgPathConverter implements Converter<SVGPath> {


    @Override
    public SVGPath fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
         CharBuffer out = CharBuffer.allocate(buf.remaining());
        int count = buf.read(out);
        out.position(0);
        out.limit(count);
        SVGPath p=new SVGPath();
        p.setContent(out.toString());
        return p;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, SVGPath value) throws IOException {
        out.append( value.getContent());
    }

    @Override
    public SVGPath getDefaultValue() {
        SVGPath p= new SVGPath();
        return p;
    }
}
