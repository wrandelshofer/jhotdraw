/* @(#)FigureImplementationDetails.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.figure;

import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;

/**
 * Hides implementation details from other packages.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
class FigureImplementationDetails {
    // ---
    // key declarations
    // ---
    final static Key<Transform> PARENT_TO_WORLD = new SimpleKey<>("parentToWorld", Transform.class);
    final static Key<Transform> WORLD_TO_PARENT = new SimpleKey<>("worldToParent", Transform.class);
    final static Key<Transform> LOCAL_TO_WORLD = new SimpleKey<>("localToWorld", Transform.class);
    final static Key<Transform> WORLD_TO_LOCAL = new SimpleKey<>("worldToLocal", Transform.class);
    final static Key<Transform> LOCAL_TO_PARENT = new SimpleKey<>("localToParent", Transform.class);
    final static Key<Transform> PARENT_TO_LOCAL = new SimpleKey<>("parentToLocal", Transform.class);
    
    // ---
    // other constant declarations
    // ---
    final static Transform IDENTITY_TRANSFORM = new Translate(0,0);
}
