/* @(#)CssColorConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.paint.Color;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * CssColorConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Color := (NamedColor|LookedUpColor|RgbColor|HsbColor|ColorFunction)
 * NamedColor := Word
 * LookedUpColor := Word
 * RgbColor := ("#",Digit,Digit,Digit
 *             | "#",Digit,Digit,Digit,Digit,Digit,Digit
 *             | "rgb(", Integer, ",", Integer, ",", Integer, ")"
 *             | "rgb(" Integer, "%", ",", Integer,"%","," Integer,"%" ")"
 *             | "rgba(", Integer, ",", Integer, "," Integer, ",", Double )
 *             | "rgba(", Integer "%" "," Integer, "%" "," Integer "%" "," Double )
 *  ...TODO...
 * </pre>
 * <p>
 * FIXME currently only parses the Color production
 * </p>
 *
 * @author Werner Randelshofer
 */
public class CssColorConverter implements Converter<CssColor> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, CssColor value) throws IOException {
        if (value == null) {
            out.append("none");
        } else {
            out.append(value.getName());
        }
    }

    @Override
    public CssColor fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        try {
            String str = buf.toString().trim();
            CssColor c;
            if ("none".equals(str)) {
                c = null;
            } else {
                c = new CssColor(str, Color.web(str));
            }
            buf.position(buf.limit());
            return c;
        } catch (IllegalArgumentException e) {
            ParseException pe = new ParseException("not a color:" + buf, buf.position());
            pe.initCause(e);
            throw pe;
        }
    }

    @Override
    public CssColor getDefaultValue() {
        return null;
    }
}
