/* @(#)Labels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.svg;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class Labels {
    private Labels() {
        // prevent instance creation
    }
    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.svg.Labels"));
        labels.setBaseClass(Labels.class);
        return labels;
    }
}
