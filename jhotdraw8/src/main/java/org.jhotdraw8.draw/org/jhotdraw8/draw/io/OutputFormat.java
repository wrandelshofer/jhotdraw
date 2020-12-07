/*
 * @(#)OutputFormat.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Drawing;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * OutputFormat.
 *
 * @author Werner Randelshofer
 * @design.pattern Drawing Strategy, Strategy.
 */
public interface OutputFormat extends PropertyBean {
    /**
     * Writes a Drawing into the resource identified by the given URI.
     *
     * @param uri       The resource identifier
     * @param drawing   The drawing.
     * @param workState for progress monitoring and cancelling the operation
     * @throws java.io.IOException if an IO error occurs
     */
    default void write(@NonNull URI uri, Drawing drawing, WorkState workState) throws IOException {
        write(Paths.get(uri), drawing, workState);
    }

    /**
     * Writes the drawing to the specified file. This method ensures that all
     * figures of the drawing are visible on the image.
     *
     *
     * @param file      the file
     * @param drawing   the drawing
     * @param workState for progress monitoring and cancelling the operation
     * @throws java.io.IOException if an IO error occurs
     */
    default void write(@NonNull Path file, Drawing drawing, WorkState workState) throws IOException {
        try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
            write(out, file.getParent().toUri(), drawing, workState);
        }
    }

    /**
     * Writes a Drawing into an output stream.
     *
     *
     * @param out       The output stream.
     * @param documentHome
     * @param drawing   The drawing.
     * @param workState for progress monitoring and cancelling the operation
     * @throws java.io.IOException if an IO error occurs
     */
    void write(OutputStream out, URI documentHome, Drawing drawing, WorkState workState) throws IOException;

}
