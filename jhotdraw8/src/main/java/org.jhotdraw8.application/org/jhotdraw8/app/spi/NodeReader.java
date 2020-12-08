/*
 * @(#)NodeReader.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.app.spi;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * Interface for a reader that can read a JavaFX Node from a stream.
 */
public interface NodeReader {
    Node read(@NonNull URL url) throws IOException;

    default Node read(@NonNull Path path) throws IOException {
        return read(path.toUri().toURL());
    }

    Node read(@NonNull InputStream in) throws IOException;
}
