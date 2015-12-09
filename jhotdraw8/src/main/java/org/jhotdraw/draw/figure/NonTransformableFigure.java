/* @(#)NonTransformableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Provides default implementations for figures which can not be transformed.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface NonTransformableFigure extends Figure {
    /**
     * Computes the transformation from local coordinates into parent
     * coordinates.
     *
     * @return the transformation
     */
    @Override
    default Transform computeLocalToParent() {
        return new Translate(0,0);
    }

    /**
     * Computes the transformation from parent coordinates into local
     * coordinates.
     *
     * @return the transformation
     */
    @Override
    default Transform computeParentToLocal() {
        return new Translate(0,0);
    }

}
