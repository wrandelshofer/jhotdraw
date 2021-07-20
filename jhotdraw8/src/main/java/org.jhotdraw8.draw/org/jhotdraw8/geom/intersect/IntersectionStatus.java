/*
 * @(#)IntersectionStatus.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

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
public enum IntersectionStatus {
    /**
     * Shape 1 intersects with shape 2.
     */
    INTERSECTION,
    /**
     * Shape 1 does not intersect with shape 2.
     * <p>
     * Note that the {@link IntersectionResultEx} may contain
     * {@link IntersectionPointEx}s indicating "false" intersections
     * of shape 1 with shape 2.
     */
    NO_INTERSECTION,
    /**
     * Shape 1 does not intersect with shape 2, and shape 1 is inside of shape 2.
     */
    NO_INTERSECTION_INSIDE,
    /**
     * Shape 1 does not intersect with shape 2, and shape 1 is outside of shape 2.
     */
    NO_INTERSECTION_OUTSIDE,
    /**
     * Shape 1 does not intersect with shape 2, and shape 1 is tangent to shape 2.
     */
    NO_INTERSECTION_TANGENT,
    /**
     * Shape 1 does not intersect with shape 2, and shape 1 is coincident with shape 2.
     */
    NO_INTERSECTION_COINCIDENT,
    /**
     * Shape 1 does not intersect with shape 2, and shape 1 is parallel to shape 2.
     */
    NO_INTERSECTION_PARALLEL
}
