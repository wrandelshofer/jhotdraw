/*
 * @(#)NodeReaderProvider.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.app.spi;

import org.jhotdraw8.annotation.NonNull;

import java.net.URL;

/**
 * Interface for service providers that provide a {@link NodeReader}.
 */
public interface NodeReaderProvider {
    /**
     * Returns true if readers of this service provider can read inputs from
     * the given URL.
     *
     * @param source the source URL
     * @return true if readers can read from this source
     */
    boolean canDecodeInput(@NonNull URL source);

    /**
     * Returns true if readers of this service provider can read inputs from
     * the given URL.
     *
     * @param path the source path
     * @return true if readers can read from this source
     */
    boolean canDecodeInput(@NonNull String path);

    /**
     * Creates a reader.
     *
     * @return a new reader
     */
    NodeReader createReader();
}

