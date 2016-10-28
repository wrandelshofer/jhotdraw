/* @(#)InputFormat.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.input;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.Figure;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.scene.input.Clipboard;

/**
 * InputFormat for clipboard.
 *
 * @design.pattern Drawing Strategy, Strategy.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ClipboardInputFormat {

    /**
     * Reads a Drawing from the clipboard URI.
     *
     * @param clipboard The clipboard.
     * @param drawing The contents of the clipboard
     * is added to this drawing.
     * @param layer If you provide a non-null value, the contents of the clipboard
     * is added to this layer. Otherwise the content is added into an unspecified layer.
     * @return the figures that were read from the clipboard
     *
     * @throws java.io.IOException if an IO error occurs
     */
    Set<Figure> read(Clipboard clipboard, Drawing drawing, Layer layer) throws IOException;
}
