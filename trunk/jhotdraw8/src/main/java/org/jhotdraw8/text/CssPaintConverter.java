/* @(#)CssPaintConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.Locale;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import org.jhotdraw8.io.IdFactory;

/**
 * CssPaintableConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paintable := (Color|LinearGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * </pre>
 * <p>
 * FIXME currently only parses the Color and the LinearGradient productions
 * </p>
 *
 * @author Werner Randelshofer
 */
public class CssPaintConverter implements Converter<Paint> {

    protected static final CssColorConverter colorConverter = new CssColorConverter(false);
    protected static final CssLinearGradientConverter linearGradientConverter = new CssLinearGradientConverter();
    protected static final CssRadialGradientConverter radialGradientConverter = new CssRadialGradientConverter();

    public void toString(Appendable out, IdFactory idFactory, Paint value) throws IOException {
        if (value == null) {
            out.append("none");
        } else if (Color.TRANSPARENT.equals(value)) {
            out.append("transparent");
        } else if (value instanceof Color) {
            CssColor c = new CssColor((Color) value);
            colorConverter.toString(out, idFactory, c);
        } else if (value instanceof LinearGradient) {
            CssLinearGradient lg = new CssLinearGradient((LinearGradient) value);
            linearGradientConverter.toString(out, idFactory, lg);
        } else if (value instanceof RadialGradient) {
            CssRadialGradient lg = new CssRadialGradient((RadialGradient) value);
            radialGradientConverter.toString(out, idFactory, lg);
        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Override
    public Paint fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString().trim().toLowerCase(Locale.ROOT);

        int pos = buf.position();
        try {
            return Paintable.getPaint(colorConverter.fromString(buf, idFactory));
        } catch (ParseException e) {
            //its not a color
        }
        try {
            buf.position(pos);
            return Paintable.getPaint(linearGradientConverter.fromString(buf, idFactory));
        } catch (ParseException e) {
            //throw new UnsupportedOperationException("not yet implemented");
        }
        ParseException pe = new ParseException("color or gradient expected" + buf, buf.position());
        throw pe;
    }

    @Override
    public Paint getDefaultValue() {
        return null;
    }
}
