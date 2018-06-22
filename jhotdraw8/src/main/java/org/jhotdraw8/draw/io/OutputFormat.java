/* @(#)OutputFormat.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;

/**
 * OutputFormat.
 *
 * @design.pattern Drawing Strategy, Strategy.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface OutputFormat {
    /**
     * Sets options that affect the write operations of this format.
     * @param options a map of options
     */
    public void setOptions(@Nullable Map<? super Key<?>, Object> options);

    /**
     * Writes a Drawing into an URI.
     *
     * @param uri The uri.
     * @param drawing The drawing.
     *
     * @throws java.io.IOException if an IO error occurs
     */
    default void write(@NonNull URI uri, Drawing drawing) throws IOException {
        write(new File(uri), drawing);
    }

    /**
     * Writes the drawing to the specified file. This method ensures that all
     * figures of the drawing are visible on the image.
     *
     * @param file the file
     * @param drawing the drawing
     *
     * @throws java.io.IOException if an IO error occurs
     */
    default void write(@NonNull File file, Drawing drawing) throws IOException {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            write(out, drawing);
        }
    }

    /**
     * Writes a Drawing into an output stream.
     *
     * @param out The output stream.
     * @param drawing The drawing.
     *
     * @throws java.io.IOException if an IO error occurs
     */
    void write( OutputStream out,  Drawing drawing) throws IOException;
    
}
