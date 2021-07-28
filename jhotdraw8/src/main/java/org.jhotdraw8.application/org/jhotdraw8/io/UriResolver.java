/*
 * @(#)UriResolver.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;

import java.net.URI;

/**
 * Provides utility methods for absolutizing and relativizing URIs.
 *
 * @author Werner Randelshofer
 */
public interface UriResolver {
    @NonNull URI relativize(@NonNull URI base, @NonNull URI uri);

    @NonNull URI absolutize(@NonNull URI base, @NonNull URI uri);
}
