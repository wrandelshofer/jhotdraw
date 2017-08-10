/* @(#)ExportOutputFormat.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.util.Map;
import org.jhotdraw8.collection.BooleanKey;
import org.jhotdraw8.collection.DoubleKey;
import org.jhotdraw8.collection.Key;

/**
 * ExportOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface ExportOutputFormat {

    Key<Double> EXPORT_DRAWING_DPI_KEY = new DoubleKey("exportDrawingDpi", 72.0);
    Key<Boolean> EXPORT_DRAWING_KEY = new BooleanKey("exportDrawing", true);
    Key<Double> EXPORT_PAGES_DPI_KEY = new DoubleKey("exportPagesDpi", 300.0);
    Key<Boolean> EXPORT_PAGES_KEY = new BooleanKey("exportPages", true);
    Key<Double> EXPORT_SLICES_DPI_KEY = new DoubleKey("exportSlicesDpi", 72.0);
    Key<Boolean> EXPORT_SLICES_KEY = new BooleanKey("exportSlices", true);
    Key<Boolean> EXPORT_SLICES_RESOLUTION_2X_KEY = new BooleanKey("exportSlicesResolution2", false);
    Key<Boolean> EXPORT_SLICES_RESOLUTION_3X_KEY = new BooleanKey("exportSlicesResolution3", false);
    
    /** Sets output format options.
     * @param options the options */
    void setOptions(Map<? super Key<?>, Object> options);

    
}
