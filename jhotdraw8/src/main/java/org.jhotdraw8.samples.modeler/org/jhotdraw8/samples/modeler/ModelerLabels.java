/*
 * @(#)ModelerLabels.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.util.Resources;

import java.util.ResourceBundle;

public class ModelerLabels {
    private ModelerLabels() {

    }

    public final static String RESOURCE_BUNDLE = "org.jhotdraw8.samples.modeler.Labels";
    public final static String INSPECTOR_RESOURCE_BUNDLE = "org.jhotdraw8.samples.modeler.InspectorLabels";

    public static ResourceBundle getBundle() {
        return getResources().asResourceBundle();
    }

    @NonNull
    public static Resources getResources() {
        return Resources.getResources("org.jhotdraw8.samples.modeler", RESOURCE_BUNDLE);
    }

    public static ResourceBundle getInspectorBundle() {
        return getInspectorResources().asResourceBundle();
    }

    @NonNull
    public static Resources getInspectorResources() {
        return Resources.getResources("org.jhotdraw8.samples.modeler", RESOURCE_BUNDLE);
    }
}
