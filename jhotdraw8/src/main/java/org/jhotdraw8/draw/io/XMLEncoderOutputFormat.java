/* @(#)XMLEncoderOutputFormat.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FigurePersistenceDelegate;
import org.jhotdraw8.draw.figure.SimpleRectangleFigure;

/**
 * XMLEncoderOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XMLEncoderOutputFormat implements OutputFormat {

    public final static DataFormat XML_SERIALIZER_FORMAT;

    static {
        DataFormat fmt = DataFormat.lookupMimeType("application/xml+ser");
        if (fmt == null) {
            fmt = new DataFormat("application/xml+ser");
        }
        XML_SERIALIZER_FORMAT = fmt;
    }

    @Override
    public void write(OutputStream out, Drawing drawing) throws IOException {
        try (XMLEncoder o = new XMLEncoder(out)) {
           /* final FigurePersistenceDelegate delegate = new FigurePersistenceDelegate();
            o.setPersistenceDelegate(Figure.class, delegate);
            o.setPersistenceDelegate(Drawing.class, delegate);
            o.setPersistenceDelegate(SimpleRectangleFigure.class, delegate);*/
            o.writeObject(drawing);
        }
    }

}
