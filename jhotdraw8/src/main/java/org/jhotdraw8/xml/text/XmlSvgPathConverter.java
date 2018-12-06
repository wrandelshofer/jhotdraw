/* @(#)CssStringConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.shape.SVGPath;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

/**
 * Converts an {@code SVGPath} from/to an XML attribute text.
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

    @Nonnull
    @Override
    public SVGPath fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CharBuffer out = CharBuffer.allocate(buf.remaining());
        int count = buf.read(out);
        out.position(0);
        out.limit(count);
        SVGPath p = new SVGPath();
        final String string = out.toString();
        p.setContent("none".equals(string)?null:string);
        return p;
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nonnull SVGPath value) throws IOException {
        final String content = value.getContent();
        out.append(content==null?"none":content);
    }

    @Nonnull
    @Override
    public SVGPath getDefaultValue() {
        SVGPath p = new SVGPath();
        return p;
    }
}
