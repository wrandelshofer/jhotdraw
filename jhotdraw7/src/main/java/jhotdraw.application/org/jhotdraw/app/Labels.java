/* @(#)Labels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class Labels {
    private Labels() {
        // prevent instance creation
    }
    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.app.Labels"));
        labels.setBaseClass(Labels.class);
        return labels;
    }
}
