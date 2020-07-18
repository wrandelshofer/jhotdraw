/* @(#)NetLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.net;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

public class NetLabels {
    private NetLabels() {
        // prevent instance creation
    }
    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.net.Labels"));
        labels.setBaseClass(NetLabels.class);
        return labels;
    }
}
