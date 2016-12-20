/* @(#)MultiClipboardOutputFormat.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.input;

import java.io.IOException;
import java.util.Set;
import javafx.scene.input.Clipboard;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;

/**
 * MultiClipboardOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id: MultiClipboardInputFormat.java 1237 2016-12-20 08:57:59Z
 * rawcoder $$
 */
public class MultiClipboardInputFormat implements ClipboardInputFormat {

    private ClipboardInputFormat[] formats;

    public MultiClipboardInputFormat(ClipboardInputFormat... formats) {
        this.formats = formats;
    }

    @Override
    public Set<Figure> read(Clipboard clipboard, DrawingModel model, Drawing drawing, Layer layer) throws IOException {
        IOException firstCause = null;
        for (ClipboardInputFormat f : formats) {
            try {
                return f.read(clipboard, model, drawing, layer);
            } catch (IOException e) {
                if (firstCause == null) {
                    firstCause = e;
                }
                //try another format
            }
        }
        if (firstCause != null) {
            throw firstCause;
        }
        throw new IOException("Unsupported clipboard content");
    }

}
