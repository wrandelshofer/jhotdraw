/* @(#)Labels.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import java.util.ResourceBundle;
import org.jhotdraw8.util.Resources;

/**
 * Labels.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
