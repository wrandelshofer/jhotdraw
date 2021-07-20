/*
 * @(#)IntersectLineRay.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

/**
 * The code of this class has been derived from intersection.js [1].
 * <p>
 * References:
 * <dl>
 *     <dt>[1] intersection.js</dt>
 *     <dd>intersection.js, Copyright (c) 2002 Kevin Lindsey, BSD 3-clause license.
 *     <a href="http://www.kevlindev.com/gui/math/intersection/Intersection.js">kevlindev.com</a></dd>
 * </dl>
 */
public class IntersectLineRay {
    private IntersectLineRay() {
    }

    public static @NonNull IntersectionResultEx intersectRayLineEx(
            double aox, double aoy, double adx, double ady, double amax,
            double b0x, double b0y, double b1x, double b1y, double epsilon) {
        return IntersectRayRay.intersectRayRayEx(
                aox, aoy, adx, ady, amax,
                b0x, b0y, b1x - b0x, b1y - b0y, 1 + epsilon, epsilon
        );
    }
}
