/* @(#)OutputFormat.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Drawing;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * OutputFormat.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Drawing Strategy, Strategy.
 */
public interface OutputFormat {
    /**
     * Sets options that affect the write operations of this format.
     *
     * @param options a map of options
     */
    public void setOptions(@Nullable Map<? super Key<?>, Object> options);

    /**
     * Writes a Drawing into the resource identified by the given URI.
     *
     * @param uri       The resource identifier
     * @param drawing   The drawing.
     * @param workState for progress monitoring and cancelling the operation
     * @throws java.io.IOException if an IO error occurs
     */
    default void write(@Nonnull URI uri, Drawing drawing, WorkState workState) throws IOException {
        write(Paths.get(uri), drawing, workState);
    }

    /**
     * Writes the drawing to the specified file. This method ensures that all
     * figures of the drawing are visible on the image.
     *
     * @param file      the file
     * @param drawing   the drawing
     * @param workState for progress monitoring and cancelling the operation
     * @throws java.io.IOException if an IO error occurs
     */
    default void write(@Nonnull Path file, Drawing drawing, WorkState workState) throws IOException {
        try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
            write(out, drawing, workState);
        }
    }

    /**
     * Writes a Drawing into an output stream.
     *
     * @param out       The output stream.
     * @param drawing   The drawing.
     * @param workState for progress monitoring and cancelling the operation
     * @throws java.io.IOException if an IO error occurs
     */
    void write(OutputStream out, Drawing drawing, WorkState workState) throws IOException;

}
