/* @(#)PertLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.pert;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class PertLabels {
    private PertLabels() {
        // prevent instance creation
    }
    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.pert.Labels"));
        labels.setBaseClass(PertLabels.class);
        return labels;
    }
}
