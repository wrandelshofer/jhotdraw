/*
 * @(#)GrapherLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import org.jhotdraw8.util.Resources;

public class GrapherLabels {
    private GrapherLabels() {

    }

    public final static String RESOURCE_BUNDLE = "org.jhotdraw8.samples.grapher.Labels";

    public static Resources getResources() {
        return Resources.getResources("org.jhotdraw8.samples.grapher", RESOURCE_BUNDLE);
    }

}
