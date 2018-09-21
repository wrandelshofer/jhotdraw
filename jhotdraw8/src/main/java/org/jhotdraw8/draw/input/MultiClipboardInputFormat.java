/* @(#)MultiClipboardOutputFormat.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.input;

import java.io.IOException;
import java.util.Set;
import java.util.function.Supplier;
import javafx.scene.input.Clipboard;

import javax.annotation.Nullable;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;

/**
 * MultiClipboardOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MultiClipboardInputFormat implements ClipboardInputFormat {
    @Nullable
    private Supplier<ClipboardInputFormat>[] formatSuppliers;

    private ClipboardInputFormat[] formats;

    @SuppressWarnings("unchecked")
    public MultiClipboardInputFormat(Supplier<ClipboardInputFormat>... formatSuppliers) {
        this.formatSuppliers = formatSuppliers;
    }
    public MultiClipboardInputFormat(ClipboardInputFormat... formats) {
        this.formats = formats;
    }

    private void createFormats() {
        if (formatSuppliers!=null) {
            int n=formatSuppliers.length;
            formats=new ClipboardInputFormat[n];
            for (int i=0;i<n;i++){
                formats[i]=formatSuppliers[i].get();
            }
            formatSuppliers=null;
        }
    }


    @Override
    public Set<Figure> read(Clipboard clipboard, DrawingModel model, Drawing drawing, Layer layer) throws IOException {
        createFormats();
        
        
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
