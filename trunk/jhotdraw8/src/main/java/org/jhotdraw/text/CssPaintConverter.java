/* @(#)CssPaintConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw.draw.io.IdFactory;

/**
 * CssPaintConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paint := (Color|LinearGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * LinearGradient := [ ("from", Point, "to", Point) |  "to", SideOrCorner], "," ],
 *                   [ ( "repeat" | "reflect" ),"," ] ColorStop,{"," ColorStop})
 * SideOrCorner := ("left"|"right"),("top"|bottom");
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
public class CssPaintConverter implements Converter<Paint> {

    private XmlNumberConverter doubleConverter = new XmlNumberConverter();

    {
        doubleConverter.setMaximumFractionDigits(3);
    }

    public void toString(Appendable out, IdFactory idFactory, Paint value) throws IOException {
        if (value == null) {
            out.append("none");
        } else if (value instanceof Color) {
            Color c = (Color) value;
            if (c.getOpacity() == 1.0) {
                int rgb = ((((int) (c.getRed() * 255)) & 0xff) << 16)
                        | ((((int) (c.getGreen() * 255)) & 0xff) << 8)
                        | ((((int) (c.getBlue() * 255)) & 0xff) << 0);

                if ((rgb & 0xf0f0f0) >>> 4 == (rgb & 0x0f0f0f)) {
                    String hex = "000" + Integer.toHexString(((rgb & 0xf0000) >>> 8) | ((rgb & 0xf00) >>> 4) | (rgb & 0xf));
                    out.append("#");
                    out.append(hex.substring(hex.length() - 3));
                } else {
                    String hex = "000000" + Integer.toHexString(rgb);
                    out.append("#");
                    out.append(hex.substring(hex.length() - 6));
                }
            } else {
                out.append("rgba(");
                out.append(Integer.toString((int) (c.getRed() * 255)));
                out.append(',');
                out.append(Integer.toString((int) (c.getGreen() * 255)));
                out.append(',');
                out.append(Integer.toString((int) (c.getBlue() * 255)));
                out.append(',');
                out.append(doubleConverter.toString(c.getOpacity()));
                out.append(')');
            }

        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Override
    public Paint fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        try {
            String str = buf.toString().trim();
            Color c;
            if ("none".equals(str)) {
                c = null;
            } else {
                c = Color.web(str);
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
    public Paint getDefaultValue() {
        return null;
    }
}
