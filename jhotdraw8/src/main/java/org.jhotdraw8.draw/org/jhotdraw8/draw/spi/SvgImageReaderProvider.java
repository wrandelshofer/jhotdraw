/*
 * @(#)SvgImageReaderProvider.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.spi;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.spi.NodeReader;
import org.jhotdraw8.app.spi.NodeReaderProvider;

import java.net.URL;

public class SvgImageReaderProvider implements NodeReaderProvider {
    @Override
    public boolean canDecodeInput(@NonNull URL source) {
        return canDecodeInput(source.getFile());
    }

    @Override
    public boolean canDecodeInput(@NonNull String path) {
        return path.toLowerCase().endsWith(".svg");
    }

    @Override
    public NodeReader createReader() {
        return new SvgNodeReader();
    }
}
