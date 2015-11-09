/* @(#)CssStringConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.paint.Paint;
import org.jhotdraw.draw.io.IdFactory;

/**
 * Converts an {@code String} to a {@code String}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssStringConverter implements Converter<String> {

    @Override
    public String fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String value=buf.toString();
        buf.position(buf.limit());
        return value;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, String value) throws IOException {
        out.append(value);
    }
    @Override
    public String getDefaultValue() {
        return "";
    }
}
