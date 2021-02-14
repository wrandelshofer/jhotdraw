/*
 * @(#)ModelerLabels.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.util.Resources;

import java.util.ResourceBundle;

public class ModelerLabels {
    private ModelerLabels() {

    }

    public static final String RESOURCE_BUNDLE = "org.jhotdraw8.samples.modeler.Labels";
    public static final String INSPECTOR_RESOURCE_BUNDLE = "org.jhotdraw8.samples.modeler.InspectorLabels";

    public static ResourceBundle getBundle() {
        return getResources().asResourceBundle();
    }

    public static @NonNull Resources getResources() {
        return Resources.getResources("org.jhotdraw8.samples.modeler", RESOURCE_BUNDLE);
    }

    public static ResourceBundle getInspectorBundle() {
        return getInspectorResources().asResourceBundle();
    }

    public static @NonNull Resources getInspectorResources() {
        return Resources.getResources("org.jhotdraw8.samples.modeler", RESOURCE_BUNDLE);
    }
}
