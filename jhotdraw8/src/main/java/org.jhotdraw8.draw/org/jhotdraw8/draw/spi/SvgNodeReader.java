/*
 * @(#)SvgNodeReader.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.spi;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.spi.NodeReader;
import org.jhotdraw8.svg.io.FXSvgTinyReader;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

public class SvgNodeReader implements NodeReader {
    @Override
    public Node read(@NonNull URL url) throws IOException {
        return new FXSvgTinyReader().read(new StreamSource(url.toString()));
    }

    @Override
    public Node read(@NonNull InputStream in) throws IOException {
        return new FXSvgTinyReader().read(new StreamSource(in));
    }
}
