/*
 * @(#)FigureImplementationDetails.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;

/**
 * Hides implementation details from other packages.
 *
 * @author Werner Randelshofer
 */
class FigureImplementationDetails {

    // ---
    // key declarations
    // ---
    final static Key<Transform> PARENT_TO_WORLD = new ObjectKey<>("parentToWorld", Transform.class);
    final static Key<Transform> WORLD_TO_PARENT = new ObjectKey<>("worldToParent", Transform.class);
    final static Key<Transform> LOCAL_TO_WORLD = new ObjectKey<>("localToWorld", Transform.class);
    final static Key<Transform> WORLD_TO_LOCAL = new ObjectKey<>("worldToLocal", Transform.class);
    final static Key<Transform> LOCAL_TO_PARENT = new ObjectKey<>("localToParent", Transform.class);
    final static Key<Transform> PARENT_TO_LOCAL = new ObjectKey<>("parentToLocal", Transform.class);
    // ---
    // other constant declarations
    // ---
    final static Transform IDENTITY_TRANSFORM = new Translate();

    /**
     * Whether the transformation cache is enabled.
     */
    final static boolean CACHE = true;

}
