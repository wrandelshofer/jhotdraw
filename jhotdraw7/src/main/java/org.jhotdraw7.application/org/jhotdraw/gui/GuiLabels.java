/* @(#)GuiLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.gui;


import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class GuiLabels {
    private GuiLabels() {
        // prevent instance creation
    }

    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.gui.Labels"));
        labels.setBaseClass(GuiLabels.class);
        return labels;
    }
}
