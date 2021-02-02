/*
 * @(#)FxmlNodeReaderProvider.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.app.spi;

import org.jhotdraw8.annotation.NonNull;

import java.net.URL;

public class FxmlNodeReaderProvider implements NodeReaderProvider {
    @Override
    public boolean canDecodeInput(@NonNull URL source) {
        return canDecodeInput(source.getFile());
    }

    @Override
    public boolean canDecodeInput(@NonNull String path) {
        return path.toLowerCase().endsWith(".fxml");
    }

    @Override
    public NodeReader createReader() {
        return new FxmlNodeReader();
    }
}
