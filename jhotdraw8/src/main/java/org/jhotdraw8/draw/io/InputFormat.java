/* @(#)InputFormat.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;

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
     * Sets options that affect the read operations of this format.
     * @param options a map of options
     */
    public void setOptions(@Nullable Map<? super Key<?>, Object> options);

    /**
     * Reads a figure from an URI
     *
     * @param uri The uri.
     * @param drawing If you provide a non-null value, the ids of the returned
     * figure are coerced so that they do not clash with ids in the drawing.
     * Also all URIs in the figure are made relative to DOCUMENT_HOME of the
     * drawing.
     * @return the figure
     *
     * @throws java.io.IOException if an IO error occurs
     */ 
    default Figure read(@Nonnull URI uri, Drawing drawing) throws IOException {
        return read(new File(uri), drawing);
    }

    /**
     * Reads a figure from a file.
     *
     * @param file the file
     * @param drawing If you provide a non-null value, the ids of the returned
     * figure are coerced so that they do not clash with ids in the drawing.
     * Also all URIs in the figure are made relative to DOCUMENT_HOME of the
     * drawing.
     * @return the figure
     *
     * @throws java.io.IOException if an IO error occurs
     */ 
    default Figure read(@Nonnull File file, Drawing drawing) throws IOException {
        URI documentHome=file.getParentFile()==null?new File(System.getProperty("user.home")).toURI():file.getParentFile().toURI();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            return read(in, drawing,documentHome);
        }
    }

    /**
     * Reads figures from an input stream and adds them to the specified
     * drawing.
     *
     * @param in The input stream.
     * @param drawing If you provide a non-null value, the contents of the file
     * is added to the drawing. Otherwise a new drawing is created.
     * @param documentHome the URI used to resolve external references from the document
     * @return the drawing
     *
     * @throws java.io.IOException if an IO error occurs
     */ 
    public Figure read( InputStream in,  Drawing drawing, URI documentHome) throws IOException;

}
