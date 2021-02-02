/*
 * @(#)DrawLabels.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import org.jhotdraw8.util.Resources;

public class DrawLabels {
    private static Resources labels;

    private DrawLabels() {
    }

    public static Resources getResources() {
        if (labels == null) {
            labels = Resources.getResources("org.jhotdraw8.draw", "org.jhotdraw8.draw.Labels");
        }
        return labels;
    }
}
