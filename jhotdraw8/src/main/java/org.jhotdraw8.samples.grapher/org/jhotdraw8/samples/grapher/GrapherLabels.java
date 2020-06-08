/*
 * @(#)GrapherLabels.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.util.Resources;

public class GrapherLabels {
    private GrapherLabels() {

    }

    public final static String RESOURCE_BUNDLE = "org.jhotdraw8.samples.grapher.Labels";

    private final static String MODULE_NAME = "org.jhotdraw8.samples.grapher";

    @NonNull
    public static Resources getResources() {
        return Resources.getResources(MODULE_NAME, RESOURCE_BUNDLE);
    }

}
