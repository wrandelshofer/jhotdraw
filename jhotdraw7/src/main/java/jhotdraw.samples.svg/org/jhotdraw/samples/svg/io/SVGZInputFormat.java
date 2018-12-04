/* @(#)SVGZInputFormat.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.samples.svg.io;

import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import java.io.*;
import java.util.zip.GZIPInputStream;
import org.jhotdraw.draw.*;

/**
 * SVGZInputFormat supports reading of uncompressed and compressed SVG images.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGZInputFormat extends SVGInputFormat {
    
    /** Creates a new instance. */
    public SVGZInputFormat() {
    }
    
    @Override
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter("Scalable Vector Graphics (SVG, SVGZ)", new String[] {"svg", "svgz"});
    }
    
    @Override public void read(InputStream in, Drawing drawing, boolean replace) throws IOException {
        BufferedInputStream bin = (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in);
        bin.mark(2);
        int magic = (bin.read() & 0xff) | ((bin.read() & 0xff) << 8);
        bin.reset();
        
        if (magic == GZIPInputStream.GZIP_MAGIC) {
            super.read(new GZIPInputStream(bin), drawing, replace);
        } else {
            super.read(bin, drawing, replace);
        }
        
    }
}
