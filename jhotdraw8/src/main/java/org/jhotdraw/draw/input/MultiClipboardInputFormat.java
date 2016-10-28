/* @(#)MultiClipboardOutputFormat.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.input;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.Figure;

/**
 * MultiClipboardOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class MultiClipboardInputFormat implements ClipboardInputFormat {
  private ClipboardInputFormat[] formats;
  
  public MultiClipboardInputFormat(ClipboardInputFormat... formats) {
    this.formats=formats;
  }

  @Override
  public Set<Figure> read(Clipboard clipboard, Drawing drawing, Layer layer) throws IOException {
    for (ClipboardInputFormat f:formats) {
    try {
      return f.read(clipboard, drawing, layer);
    }catch (IOException e)  {
      //try another format
    }
    }
    throw new IOException("Unsupported clipboard content");
  }

}
