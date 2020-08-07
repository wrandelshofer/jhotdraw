/*
 * @(#)FigureImplementationDetails.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Hides implementation details from other packages.
 *
 * @author Werner Randelshofer
 */
class FigureImplementationDetails {

    // ---
    // other constant declarations
    // ---
    final static Transform IDENTITY_TRANSFORM = new Translate();

    /**
     * Whether the transformation cache is enabled.
     */
    final static boolean CACHE = true;

}
