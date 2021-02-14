/*
 * @(#)IntersectLineRay.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

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
