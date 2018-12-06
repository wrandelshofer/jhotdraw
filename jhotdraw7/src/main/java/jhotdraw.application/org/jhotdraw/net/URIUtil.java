/* @(#)URIUtil.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.net;

import java.io.File;
import java.net.URI;

/**
 * URIUtil.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class URIUtil {

    /** Prevent instance creation. */
    private void URIUtil() {
    }

    /** Returns the name of an URI for display in the title bar of a window. */
    public static String getName(URI uri) {
        if (uri.getScheme()!=null&&"file".equals(uri.getScheme())) {
            return new File(uri).getName();
        }
        return uri.toString();
    }
}
