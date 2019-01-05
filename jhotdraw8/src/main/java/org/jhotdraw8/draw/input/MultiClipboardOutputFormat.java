/* @(#)MultiClipboardOutputFormat.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.input;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;

/**
 * MultiClipboardOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MultiClipboardOutputFormat implements ClipboardOutputFormat {
    @Nullable
    private Supplier<ClipboardOutputFormat>[] formatSuppliers;

    private ClipboardOutputFormat[] formats;

    @SafeVarargs
    public MultiClipboardOutputFormat(@Nonnull Supplier<ClipboardOutputFormat>... formatSuppliers) {
        this.formatSuppliers = formatSuppliers;
    }

    public MultiClipboardOutputFormat(ClipboardOutputFormat... formats) {
        this.formats = formats;
    }

    private void createFormats() {
        if (formatSuppliers != null) {
            int n = formatSuppliers.length;
            formats = new ClipboardOutputFormat[n];
            for (int i = 0; i < n; i++) {
                formats[i] = formatSuppliers[i].get();
            }
            formatSuppliers = null;
        }
    }

    @Override
    public void write(Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException {
        createFormats();

        for (ClipboardOutputFormat f : formats) {
            f.write(out, drawing, selection);
        }
    }

}
