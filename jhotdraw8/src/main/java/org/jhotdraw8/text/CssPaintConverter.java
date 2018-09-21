/* @(#)CssPaintConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import javax.annotation.Nullable;
import org.jhotdraw8.draw.key.CssRadialGradient;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.CssLinearGradient;
import org.jhotdraw8.draw.key.Paintable;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

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
 * @version $Id$
 */
public class CssPaintConverter implements Converter<Paint> {

  protected static final CssPaintableConverter paintableConverter = new CssPaintableConverter();

  @Nullable
  @Override
  public Paint fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
    Paintable p = paintableConverter.fromString(buf, idFactory);
    return p == null ? null : p.getPaint();
  }

  @Nullable
  @Override
  public Paint getDefaultValue() {
    return null;
  }

  public void toString(Appendable out, IdFactory idFactory, @Nullable Paint value) throws IOException {
    Paintable p;
    if (value == null) {
      p = null;
    } else if (value instanceof Color) {
      p = new CssColor((Color) value);
    } else if (value instanceof LinearGradient) {
      p = new CssLinearGradient((LinearGradient) value);
    } else if (value instanceof RadialGradient) {
      p = new CssRadialGradient((RadialGradient) value);
    } else {
      throw new UnsupportedOperationException("unsupported value:" + value);
    }
    paintableConverter.toString(out, idFactory, p);
  }
}
