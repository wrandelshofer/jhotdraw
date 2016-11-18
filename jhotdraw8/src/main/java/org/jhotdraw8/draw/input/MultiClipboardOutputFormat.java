/* @(#)MultiClipboardOutputFormat.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.input;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.draw.Drawing;
import org.jhotdraw8.draw.figure.Figure;

/**
 * MultiClipboardOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class MultiClipboardOutputFormat implements ClipboardOutputFormat {
  private ClipboardOutputFormat[] formats;
  
  public MultiClipboardOutputFormat(ClipboardOutputFormat... formats) {
    this.formats=formats;
  }

  @Override
  public void write(Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException {
    for (ClipboardOutputFormat f:formats) {
      f.write(out, drawing, selection);
    }
  }
}
