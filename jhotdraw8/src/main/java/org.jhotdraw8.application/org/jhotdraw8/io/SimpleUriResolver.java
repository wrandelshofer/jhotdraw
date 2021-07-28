/*
 * @(#)UriResolver.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides utility methods for absolutizing and relativizing URIs.
 *
 * @author Werner Randelshofer
 */
public class SimpleUriResolver implements UriResolver {

    @Override
    public @NonNull URI relativize(@Nullable URI base, @NonNull URI uri) {
        URI relativized = uri;
        // Paths is better at relativizing URIs than URI.relativize().
        if (base == null) {
            return uri;
        }

        if ("file".equals(base.getScheme()) &&
                ("file".equals(relativized.getScheme()) || relativized.getScheme() == null)) {
            Path other = Paths.get(relativized);
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

    @Override
    public @NonNull URI absolutize(@Nullable URI base, @NonNull URI uri) {
        if (base == null) {
            return uri;
        }
        URI absolutized = uri;
        // Paths is better at resolving URIs than URI.resolve().
        if ("file".equals(base.getScheme()) &&
                ("file".equals(absolutized.getScheme()) || absolutized.getScheme() == null)) {
            absolutized = Paths.get(base).resolve(Paths.get(absolutized.getPath())).normalize().toUri();
        } else if ("jar".equals(base.getScheme()) && null == uri.getScheme()) {
            final String baseStr = base.toString();
            final String uriStr = uri.toString();
            try {
                return new URI(baseStr + "/" + uriStr);
            } catch (URISyntaxException e) {
                return uri;
            }
        } else {
            absolutized = base.resolve(absolutized);
        }

        return absolutized;
    }

    @Override
    public @NonNull URI getParent(@NonNull URI uri) {
        if ("jar".equals(uri.getScheme())) {
            try {
                final String str = uri.toString();
                return new URI(str.substring(0, str.lastIndexOf('/')));
            } catch (final URISyntaxException e) {
                return uri;
            }
        } else {
            return uri.resolve(".");
        }
    }
}
