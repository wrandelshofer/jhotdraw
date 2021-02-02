/*
 * @(#)TeddyLabels.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.teddy;

import org.jhotdraw8.util.Resources;

public class TeddyLabels {
    private static Resources labels;

    private TeddyLabels() {
    }

    public static Resources getResources() {
        if (labels == null) {
            labels = Resources.getResources("org.jhotdraw8.samples.teddy", "org.jhotdraw8.samples.teddy.Labels");
        }
        return labels;
    }
}
