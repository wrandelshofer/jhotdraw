/*
 * @(#)IntersectionResultEx.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.jhotdraw8.collection.ImmutableArrayList;

import java.util.Collections;
import java.util.List;

/**
 * A container object that contains an elaborated result of an intersection test.
 * <p>
 * The code of this class has been derived from intersection.js [1].
 * <p>
 * References:
 * <dl>
 *     <dt>[1] intersection.js</dt>
 *     <dd>intersection.js, Copyright (c) 2002 Kevin Lindsey, BSD 3-clause license.
 *     <a href="http://www.kevlindev.com/gui/math/intersection/Intersection.js">kevlindev.com</a></dd>
 * </dl>
 */
public class IntersectionResultEx extends ImmutableArrayList<IntersectionPointEx> {

    private final IntersectionStatus status;

    public IntersectionResultEx(@NonNull List<IntersectionPointEx> intersections) {
        this(intersections.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION, intersections);
    }

    public IntersectionResultEx(IntersectionStatus status) {
        this(status, Collections.emptyList());
    }

    public IntersectionResultEx(IntersectionStatus status, @NonNull List<IntersectionPointEx> intersections) {
        super(intersections);
        this.status = status;
    }


    public IntersectionStatus getStatus() {
        return status;
    }

    public DoubleArrayList getAllArgumentsB() {
        return stream()
                .mapToDouble(IntersectionPointEx::getArgumentB)
                .collect(DoubleArrayList::new, DoubleArrayList::add, DoubleArrayList::addAll);
    }

    public DoubleArrayList getAllArgumentsA() {
        return stream()
                .mapToDouble(IntersectionPointEx::getArgumentA)
                .collect(DoubleArrayList::new, DoubleArrayList::add, DoubleArrayList::addAll);
    }


    @Override
    public @NonNull String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Intersection{").append(status).append(", points=");
        b.append(super.toString());
        b.append('}');
        return b.toString();
    }

}
