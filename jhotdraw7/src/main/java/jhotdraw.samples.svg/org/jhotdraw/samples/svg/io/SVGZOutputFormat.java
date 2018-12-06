/* @(#)SVGZOutputFormat.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.svg.io;

import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import java.io.*;
import java.util.zip.*;
import org.jhotdraw.draw.*;

/**
 * SVGZOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGZOutputFormat extends SVGOutputFormat {
    
    /** Creates a new instance. */
    public SVGZOutputFormat() {
    }
    
    @Override
    public String getFileExtension() {
        return "svgz";
    }
    @Override
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter("Compressed Scalable Vector Graphics (SVGZ)", "svgz");
    }
    
    
    @Override public void write(OutputStream out, Drawing drawing) throws IOException {
        GZIPOutputStream gout = new GZIPOutputStream(out);
        super.write(gout, drawing, drawing.getChildren());
        gout.finish();
        
    }
}
