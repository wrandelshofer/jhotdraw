/* @(#)SvgPaintConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * SvgPaintConverter. 
 * 
 * SVG does not support an alpha channel in a color. The opacity must be specified in a separate attribute.
 *
 * @author Werner Randelshofer
 */
public class SvgPaintConverter extends CssPaintConverter {
        public void toString(Appendable out, IdFactory idFactory, Paint value) throws IOException {
        if (value == null) {
            out.append("none");
        } else if (Color.TRANSPARENT.equals(value)) {
            out.append("none");
        } else if (value instanceof Color) {
            Color crgba=(Color)value;
            CssColor c = new CssColor(new Color(crgba.getRed(),crgba.getGreen(),crgba.getBlue(),1.0));
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
}
