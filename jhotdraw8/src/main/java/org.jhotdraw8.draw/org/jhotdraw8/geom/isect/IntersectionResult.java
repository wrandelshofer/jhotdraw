package org.jhotdraw8.geom.isect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableArrayList;

import java.util.Collection;

/**
 * A container object that contains the result of an intersection test.
 */
public class IntersectionResult extends ImmutableArrayList<IntersectionPoint> {
    private final IntersectionStatus status;

    public IntersectionResult(@NonNull IntersectionStatus status, @NonNull Collection<? extends IntersectionPoint> copyItems) {
        super(copyItems);
        this.status = status;
    }

    public IntersectionStatus getStatus() {
        return status;
    }
}
