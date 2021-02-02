/*
 * @(#)SvgNodeReader.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.spi;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.spi.NodeReader;
import org.jhotdraw8.svg.io.FXSvgTinyReaderNew;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

public class SvgNodeReader implements NodeReader {
    @Override
    public Node read(@NonNull URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            return read(in);
        }
    }

    @Override
    public Node read(@NonNull InputStream in) throws IOException {
        return new FXSvgTinyReaderNew().read(in, Paths.get(System.getProperty("user.home")).toUri());
        //return new FXSvgTinyReader().read(in);
    }
}
