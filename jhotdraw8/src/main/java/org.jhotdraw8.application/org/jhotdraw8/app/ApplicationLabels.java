/*
 * @(#)ApplicationLabels.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import org.jhotdraw8.util.Resources;

/**
 * ApplicationLabels.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ApplicationLabels {

    private static Resources labels;
    private static Resources guilabels;

    /**
     * Prevent instanced creation.
     */
    private ApplicationLabels() {
    }

    public static Resources getResources() {
        if (labels == null) {
            labels = Resources.getResources("org.jhotdraw8.application", "org.jhotdraw8.app.Labels");
        }
        return labels;
    }

    public static Resources getGuiResources() {
        if (guilabels == null) {
            guilabels = Resources.getResources("org.jhotdraw8.application", "org.jhotdraw8.gui.Labels");
        }
        return guilabels;
    }


}
