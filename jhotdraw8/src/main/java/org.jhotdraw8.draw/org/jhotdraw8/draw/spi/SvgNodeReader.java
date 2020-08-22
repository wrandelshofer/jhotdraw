/*
 * @(#)SvgNodeReader.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.spi;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.spi.NodeReader;
import org.jhotdraw8.svg.io.SvgTinySceneGraphReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SvgNodeReader implements NodeReader {
    @Override
    public Node read(@NonNull URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            return read(in);
        }
    }

    @Override
    public Node read(@NonNull InputStream in) throws IOException {
        return new SvgTinySceneGraphReader().read(in);
    }
}