/*
 * @(#)ImageNodeReaderProvider.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.app.spi;

import org.jhotdraw8.annotation.NonNull;

import java.net.URL;

public class ImageNodeReaderProvider implements NodeReaderProvider {
    @Override
    public boolean canDecodeInput(@NonNull URL source) {
        return canDecodeInput(source.getFile());
    }

    @Override
    public boolean canDecodeInput(@NonNull String path) {
        int p = path.lastIndexOf('.');
        String extension = path.substring(p + 1);
        switch (extension.toLowerCase()) {
        case "png":
        case "bmp":
        case "gif":
        case "jpg":
            return true;
        default:
            return false;
        }
    }

    @Override
    public NodeReader createReader() {
        return new ImageNodeReader();
    }
}
