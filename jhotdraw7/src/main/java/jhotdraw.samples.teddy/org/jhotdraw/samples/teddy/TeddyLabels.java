/* @(#)TeddyLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.teddy;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class TeddyLabels {
    private TeddyLabels() {
        // prevent instance creation
    }

    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.teddy.Labels"));
        labels.setBaseClass(TeddyLabels.class);
        return labels;
    }
}
