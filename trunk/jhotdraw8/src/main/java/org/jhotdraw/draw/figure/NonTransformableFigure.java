/* @(#)NonTransformableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import javafx.scene.transform.Transform;

/**
 * Provides default implementations for figures which can not be transformed.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface NonTransformableFigure extends TransformCachingFigure {

    @Override
    default Transform getLocalToParent() {
        return FigureImplementationDetails.IDENTITY_TRANSFORM;
    }

    @Override
    default Transform getParentToLocal() {
        return FigureImplementationDetails.IDENTITY_TRANSFORM;
    }


}
