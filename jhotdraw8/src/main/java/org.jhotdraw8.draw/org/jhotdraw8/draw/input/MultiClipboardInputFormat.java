/*
 * @(#)MultiClipboardInputFormat.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.input;

import javafx.scene.input.Clipboard;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * MultiClipboardOutputFormat.
 *
 * @author Werner Randelshofer
 */
public class MultiClipboardInputFormat implements ClipboardInputFormat {
    private @Nullable List<Supplier<ClipboardInputFormat>> formatSuppliers;

    private ClipboardInputFormat[] formats;

    @SuppressWarnings("unchecked")
    public MultiClipboardInputFormat(@NonNull List<Supplier<ClipboardInputFormat>> formatSuppliers) {
        this.formatSuppliers = formatSuppliers;
    }

    public MultiClipboardInputFormat(ClipboardInputFormat... formats) {
        this.formats = formats;
    }

    private void createFormats() {
        if (formatSuppliers != null) {
            int n = formatSuppliers.size();
            formats = new ClipboardInputFormat[n];
            for (int i = 0; i < n; i++) {
                formats[i] = formatSuppliers.get(i).get();
            }
            formatSuppliers = null;
        }
    }


    @Override
    public Set<Figure> read(Clipboard clipboard, DrawingModel model, Drawing drawing, Figure layer) throws IOException {
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
