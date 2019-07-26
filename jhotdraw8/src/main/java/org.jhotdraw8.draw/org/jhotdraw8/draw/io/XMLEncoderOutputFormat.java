/*
 * @(#)XMLEncoderOutputFormat.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Drawing;

import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
    @Nonnull
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
    public void write(URI documentHome, @Nonnull OutputStream out, Drawing drawing, WorkState workState) throws IOException {
        try (XMLEncoder o = new XMLEncoder(out)) {
           /* final FigurePersistenceDelegate delegate = new FigurePersistenceDelegate();
            o.setPersistenceDelegate(Figure.class, delegate);
            o.setPersistenceDelegate(Drawing.class, delegate);
            o.setPersistenceDelegate(RectangleFigure.class, delegate);*/
            o.writeObject(drawing);
        }
    }

}
