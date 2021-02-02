/*
 * @(#)UriResolver.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides utility methods for absolutizing and relativizing URIs.
 *
 * @author Werner Randelshofer
 */
public class UriResolver  {

    /**
     * Prevent instantiation.
     */
    private UriResolver() {
    }

    public static @NonNull URI relativize(@NonNull URI base, @NonNull URI uri) {
        URI relativized = uri;
        // Paths is better at relativizing URIs than URI.relativize().
        if ("file".equals(base.getScheme()) &&
                ("file".equals(relativized.getScheme()) || relativized.getScheme() == null)) {
            Path other = Paths.get(relativized.getPath());
            Path relativizedPath;
            if (other.isAbsolute()) {
                relativizedPath = Paths.get(base).relativize(other);
            } else {
                relativizedPath = other;
            }
            if (relativizedPath.isAbsolute()) {
                relativized = relativizedPath.toUri();
            } else {
                try {
                    relativized = new URI(null, null, relativizedPath.toString()
                            , null, null);
                } catch (URISyntaxException e) {
                    relativized = base;// we tried hard, but we failed
                }
            }
        } else {
            relativized = base.relativize(relativized);
        }
        return relativized;
    }
    public static @NonNull URI absolutize(@NonNull URI base, @NonNull URI uri) {
        URI absolutized = uri;
        // Paths is better at resolving URIs than URI.relativize().
        if ("file".equals(base.getScheme()) &&
                ("file".equals(absolutized.getScheme()) || absolutized.getScheme() == null)) {
            absolutized = Paths.get(base).resolve(Paths.get(absolutized.getPath())).normalize().toUri();
        } else {
            absolutized = base.resolve(absolutized);
        }
        return absolutized;
    }
}
