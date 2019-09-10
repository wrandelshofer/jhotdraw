/*
 * @(#)ExportOutputFormat.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonnullObjectKey;

import java.util.Map;

/**
 * ExportOutputFormat.
 *
 * @author Werner Randelshofer
 */
public interface ExportOutputFormat {

    NonnullObjectKey<Double> EXPORT_DRAWING_DPI_KEY = new NonnullObjectKey<>("exportDrawingDpi", Double.class, 72.0);
    NonnullObjectKey<Boolean> EXPORT_DRAWING_KEY = new NonnullObjectKey<>("exportDrawing", Boolean.class, true);
    NonnullObjectKey<Double> EXPORT_PAGES_DPI_KEY = new NonnullObjectKey<>("exportPagesDpi", Double.class, 300.0);
    NonnullObjectKey<Boolean> EXPORT_PAGES_KEY = new NonnullObjectKey<>("exportPages", Boolean.class, true);
    NonnullObjectKey<Double> EXPORT_SLICES_DPI_KEY = new NonnullObjectKey<>("exportSlicesDpi", Double.class, 72.0);
    NonnullObjectKey<Boolean> EXPORT_SLICES_KEY = new NonnullObjectKey<>("exportSlices", Boolean.class, true);
    NonnullObjectKey<Boolean> EXPORT_SLICES_RESOLUTION_2X_KEY = new NonnullObjectKey<>("exportSlicesResolution2", Boolean.class, false);
    NonnullObjectKey<Boolean> EXPORT_SLICES_RESOLUTION_3X_KEY = new NonnullObjectKey<>("exportSlicesResolution3", Boolean.class, false);

    /**
     * Sets output format options.
     *
     * @param options the options
     */
    void setOptions(Map<? super Key<?>, Object> options);


}
