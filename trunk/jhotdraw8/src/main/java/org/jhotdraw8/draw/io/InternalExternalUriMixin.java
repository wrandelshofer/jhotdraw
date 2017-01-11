/* @(#)InternalExternalUriMixin.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.net.URI;

/**
 * InternalExternalUriMixin.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface InternalExternalUriMixin {

    URI getExternalHome();

    void setExternalHome(URI uri);

    URI getInternalHome();

    void setInternalHome(URI uri);

    default URI toExternal(URI uri) {
        if (uri == null) {
            return null;
        }
        URI internal = getInternalHome();
        URI external = getExternalHome();
        if (internal != null) {
            uri = internal.resolve(uri);
        }
        if (external != null) {
            uri = external.relativize(uri);
        }
        return uri;
    }

    default URI toInternal(URI uri) {
        if (uri == null) {
            return null;
        }
        URI internal = getInternalHome();
        URI external = getExternalHome();
        if (external != null) {
            uri = external.resolve(uri);
        }
        if (internal != null) {
            uri = internal.relativize(uri);
        }
        return uri;
    }

}
