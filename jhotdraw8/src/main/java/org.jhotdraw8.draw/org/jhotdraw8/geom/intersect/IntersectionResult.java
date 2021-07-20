/*
 * @(#)IntersectionResult.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.jhotdraw8.collection.ImmutableArrayList;
import org.jhotdraw8.collection.ImmutableCollection;

import java.util.Collection;

/**
 * A container object that contains the result of an intersection test.
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
public class IntersectionResult extends ImmutableArrayList<IntersectionPoint> {
    private final IntersectionStatus status;

    public IntersectionResult(@NonNull IntersectionStatus status, @NonNull Collection<? extends IntersectionPoint> copyItems) {
        super(copyItems);
        this.status = status;
    }

    public IntersectionResult(@NonNull IntersectionStatus status, @NonNull ImmutableCollection<? extends IntersectionPoint> copyItems) {
        super(copyItems);
        this.status = status;
    }

    public IntersectionResult(@NonNull Collection<? extends IntersectionPoint> copyItems) {
        this(copyItems.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                copyItems);
    }

    public IntersectionStatus getStatus() {
        return status;
    }

    public DoubleArrayList getAllArgumentsA() {
        return stream()
                .mapToDouble(IntersectionPoint::getArgumentA)
                .collect(DoubleArrayList::new, DoubleArrayList::add, DoubleArrayList::addAll);
    }


}
