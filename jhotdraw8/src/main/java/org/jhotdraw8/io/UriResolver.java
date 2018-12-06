/* @(#)UriResolver.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import java.net.URI;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * Takes an URI and resolves it against the specified internal URI, then
 * relativizes it against the specified external URI.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class UriResolver implements Function<URI, URI> {

    @Nullable
    private final URI internal;
    @Nullable
    private final URI external;

    /**
     * Creates a new instance.
     * @param internal the internal URI
     * @param externl the external URI
     */
    public UriResolver(@Nullable URI internal, @Nullable URI externl) {
        this.internal = internal;
        this.external = externl;
    }

    @Override
    public URI apply(URI uri) {
        if (internal != null) {
            uri = internal.resolve(uri);
        }
        if (external != null) {
            uri = external.relativize(uri);
        }
        return uri;
    }

}
