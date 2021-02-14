/*
 * @(#)XmlSvgPathConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import javafx.scene.shape.SVGPath;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

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
 */
public class XmlSvgPathConverter implements Converter<SVGPath> {

    @Override
    public @NonNull SVGPath fromString(@NonNull CharBuffer buf, @Nullable IdResolver idResolver) throws ParseException, IOException {
        CharBuffer out = CharBuffer.allocate(buf.remaining());
        int count = buf.read(out);
        out.position(0);
        out.limit(count);
        SVGPath p = new SVGPath();
        final String string = out.toString();
        p.setContent("none".equals(string) ? null : string);
        return p;
    }

    @Override
    public void toString(@NonNull Appendable out, @Nullable IdSupplier idSupplier, @NonNull SVGPath value) throws IOException {
        final String content = value.getContent();
        out.append(content == null ? "none" : content);
    }

    @Override
    public @NonNull SVGPath getDefaultValue() {
        SVGPath p = new SVGPath();
        return p;
    }
}
