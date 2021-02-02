/*
 * @(#)IdResolver.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.net.URI;

public interface IdResolver {
    /**
     * Gets the object for the specified id. Returns null if the id has no
     * object.
     *
     * @param id the id
     * @return the object
     */
    @Nullable Object getObject(String id);

    /**
     * Absolutize the given external URI, so that it can be used inside
     * of a drawing (e.g. to access data from the URI).
     * <p>
     * In the internal representation of a drawing, we store all URIs with
     * absolute paths.
     * <p>
     * In the external representation of a drawing, we try to store all URIs
     * relative to the home folder of the document (document home).
     *
     * @param uri an external URI (typically relative to document home)
     * @return an internal URI (typically an absolute path)
     */
    @NonNull URI absolutize(@NonNull URI uri);

}
