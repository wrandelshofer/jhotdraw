/* @(#)Labels.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.inspector;

import java.util.ResourceBundle;
import org.jhotdraw8.util.Resources;

/**
 * Labels.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Labels {

    public final static String RESOURCE_BUNDLE = "org.jhotdraw8.draw.inspector.Labels";

    public static ResourceBundle getBundle() {
        return Resources.getBundle(RESOURCE_BUNDLE);
    }

    public static Resources getResources() {
        return Resources.getResources(RESOURCE_BUNDLE);
    }
}
