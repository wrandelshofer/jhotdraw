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
import org.jhotdraw8.io.IdFactory;

/**
 * SvgPaintConverter.
 *
 * SVG does not support an alpha channel in a color. The opacity must be
 * specified in a separate attribute.
 *
 * @author Werner Randelshofer
 */
public class SvgPaintConverter extends CssPaintConverter {

  public void toString(Appendable out, IdFactory idFactory, Paint value) throws IOException {
    if ((value instanceof Color) && !value.isOpaque()) {
      Color c = (Color) value;
      value = new Color(c.getRed(), c.getGreen(), c.getBlue(), 1.0);
    }
    super.toString(out, idFactory, value);
  }
}
