/* @(#)GuiLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class DrawLabels {
    private DrawLabels() {
        // prevent instance creation
    }
    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.GuiLabels"));
        labels.setBaseClass(DrawLabels.class);
        return labels;
    }
}
