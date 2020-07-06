/*
 * @(#)XMLEncoderOutputFormat.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
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
 */
public class XMLEncoderOutputFormat extends AbstractPropertyBean implements OutputFormat {
    /**
     * Holds the current options.
     */
    @NonNull
    private Map<? super Key<?>, Object> options = Collections.emptyMap();

    @Override
    public void putAll(@Nullable Map<Key<?>, Object> options) {
        this.options = (options == null) ? Collections.emptyMap() : new LinkedHashMap<>(options);
    }

    public final static String XML_SERIALIZER_MIME_TYPE = "application/xml+ser";

    @Override
    public void write(URI documentHome, @NonNull OutputStream out, Drawing drawing, WorkState workState) throws IOException {
        try (XMLEncoder o = new XMLEncoder(out)) {
           /* final FigurePersistenceDelegate delegate = new FigurePersistenceDelegate();
            o.setPersistenceDelegate(Figure.class, delegate);
            o.setPersistenceDelegate(Drawing.class, delegate);
            o.setPersistenceDelegate(RectangleFigure.class, delegate);*/
            o.writeObject(drawing);
        }
    }

}
