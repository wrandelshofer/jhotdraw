/* @(#)InputFormat.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.jhotdraw8.draw.Drawing;

/**
 * InputFormat.
 *
 * @design.pattern Drawing Strategy, Strategy.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface InputFormat {

    /**
     * Reads a Drawing from an URI.
     *
     * @param uri The uri.
     * @param drawing If you provide a non-null value, the contents of the file
     * is added to this drawing. Otherwise a new drawing is created.
     * @return the drawing
     *
     * @throws java.io.IOException if an IO error occurs
     */
    default Drawing read(URI uri, Drawing drawing) throws IOException {
        return read(new File(uri), drawing);
    }

    /**
     * Writes the drawing to the specified file.
     * This method ensures that all figures of the drawing are visible on
     * the image.
     *
     * @param file the file
     * @param drawing If you provide a non-null value, the contents of the file
     * is added to the drawing. Otherwise a new drawing is created.
     * @return the drawing
     *
     * @throws java.io.IOException if an IO error occurs
     */
    default Drawing read(File file, Drawing drawing) throws IOException {
        try (BufferedInputStream out = new BufferedInputStream(new FileInputStream(file))) {
            return read(out, drawing);
        }
    }

    /**
     * Reads figures from an input stream and adds them to the specified
     * drawing.
     *
     * @param in The input stream.
     * @param drawing If you provide a non-null value, the contents of the file
     * is added to the drawing. Otherwise a new drawing is created.
     * @return the drawing
     *
     * @throws java.io.IOException if an IO error occurs
     */
    public Drawing read(InputStream in, Drawing drawing) throws IOException;

}
