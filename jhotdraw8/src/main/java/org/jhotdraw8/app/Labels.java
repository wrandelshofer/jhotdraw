/* @(#)Labels.java
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import org.jhotdraw8.util.Resources;

/**
 * Labels.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Labels {

    private static Resources labels;

    /**
     * Prevent instanced creation.
     */
    private Labels() {
    }

    public static Resources getLabels() {
        if (labels == null) {
            labels = Resources.getResources("org.jhotdraw8.app.Labels");
        }
        return labels;
    }
;
}
