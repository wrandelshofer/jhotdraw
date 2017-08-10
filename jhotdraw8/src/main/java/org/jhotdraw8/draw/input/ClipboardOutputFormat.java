/* @(#)OutputFormat.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.input;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import java.util.Map;
import javafx.scene.input.DataFormat;

/**
 * OutputFormat for clipboard.
 *
 * @design.pattern Drawing Strategy, Strategy.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ClipboardOutputFormat {

    /**
     * Writes a Drawing into a clipboard
     *
     * @param out The clipboard
     * @param drawing The drawing.
     *
     * @throws java.io.IOException if an IO error occurs
     */
    default void write(Map<DataFormat, Object> out, Drawing drawing) throws IOException {
        write(out, drawing, Collections.singleton(drawing));
    }

    /**
     * Writes a selection of figures from a Drawing into a clipboard
     *
     * @param out The clipboard
     * @param drawing The drawing.
     * @param selection A selection
     *
     * @throws java.io.IOException if an IO error occurs
     */
    void write(Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException;
}
