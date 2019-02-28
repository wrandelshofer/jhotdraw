package org.jhotdraw8.samples.modeler;

import org.jhotdraw8.util.Resources;

import java.util.ResourceBundle;

public class ModelerLabels {
    private ModelerLabels() {

    }

    public final static String RESOURCE_BUNDLE = "org.jhotdraw8.samples.modeler.Labels";
    public final static String INSPECTOR_RESOURCE_BUNDLE = "org.jhotdraw8.samples.modeler.InspectorLabels";

    public static ResourceBundle getBundle() {
        return Resources.getBundle(RESOURCE_BUNDLE);
    }

    public static Resources getResources() {
        return Resources.getResources(RESOURCE_BUNDLE);
    }

    public static ResourceBundle getInspectorBundle() {
        return Resources.getBundle(INSPECTOR_RESOURCE_BUNDLE);
    }

    public static Resources getInspectorResources() {
        return Resources.getResources(INSPECTOR_RESOURCE_BUNDLE);
    }
}
