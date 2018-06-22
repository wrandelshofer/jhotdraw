/* @(#)XMLEncoderOutputFormat.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.input.DataFormat;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;

/**
 * XMLEncoderOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XMLEncoderOutputFormat implements OutputFormat {
    /**
     * Holds the current options.
     */
    @NonNull
    private Map<? super Key<?>, Object> options = Collections.emptyMap();

    @Override
    public void setOptions(@Nullable Map<? super Key<?>, Object> options) {
        this.options = (options == null) ? Collections.emptyMap() : new LinkedHashMap<>(options);
    }

    public final static DataFormat XML_SERIALIZER_FORMAT;

    static {
        DataFormat fmt = DataFormat.lookupMimeType("application/xml+ser");
        if (fmt == null) {
            fmt = new DataFormat("application/xml+ser");
        }
        XML_SERIALIZER_FORMAT = fmt;
    }

    @Override
    public void write(@NonNull OutputStream out, Drawing drawing) throws IOException {
        try (XMLEncoder o = new XMLEncoder(out)) {
           /* final FigurePersistenceDelegate delegate = new FigurePersistenceDelegate();
            o.setPersistenceDelegate(Figure.class, delegate);
            o.setPersistenceDelegate(Drawing.class, delegate);
            o.setPersistenceDelegate(SimpleRectangleFigure.class, delegate);*/
            o.writeObject(drawing);
        }
    }

}
