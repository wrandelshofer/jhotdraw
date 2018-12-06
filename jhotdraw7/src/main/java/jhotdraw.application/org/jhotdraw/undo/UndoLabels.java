/* @(#)UndoLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.undo;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class UndoLabels {
    private UndoLabels() {
        // prevent instance creation
    }

    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.undo.Labels"));
        labels.setBaseClass(UndoLabels.class);
        return labels;
    }
}
