/* @(#)ODGLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.odg;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class ODGLabels {
    private ODGLabels() {
        // prevent instance creation
    }
    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.odg.Labels"));
        labels.setBaseClass(ODGLabels.class);
        return labels;
    }
}
