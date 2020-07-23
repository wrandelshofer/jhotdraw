/*
 * @(#)Intersection.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.isect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A container object that contains the result of an intersection test.
 */
public class IntersectionResult {


    @NonNull
    private final List<IntersectionPoint> intersections;
    private final Status status;

    public IntersectionResult(@NonNull List<IntersectionPoint> intersections) {
        this(intersections.isEmpty() ? Status.NO_INTERSECTION : Status.INTERSECTION, intersections);
    }

    public IntersectionResult(Status status) {
        this(status, Collections.emptyList());
    }

    public IntersectionResult(Status status, @NonNull List<IntersectionPoint> intersections) {
        if (status == Status.INTERSECTION && intersections.isEmpty()
                || status != Status.INTERSECTION && !intersections.isEmpty()) {
            throw new IllegalArgumentException("status=" + status + " intersections=" + intersections);
        }
        //intersections.sort(Comparator.comparingDouble(IntersectionPoint::getT1));

        this.intersections = Collections.unmodifiableList(intersections);
        this.status = status;
    }

    @NonNull
    public List<IntersectionPoint> getIntersections() {
        return intersections;
    }

    @NonNull
    public IntersectionPoint getFirst() {
        return intersections.get(0);
    }

    public Point2D.Double getLastPoint() {
        return intersections.get(intersections.size() - 1).getPoint();
    }

    public double getLastT() {
        return intersections.get(intersections.size() - 1).getT1();
    }

    @Nullable
    public IntersectionPoint getLastIntersectionPoint() {
        return intersections.isEmpty() ? null : intersections.get(intersections.size() - 1);
    }

    public double getFirstT() {
        return intersections.get(0).getT1();
    }

    public List<Point2D.Double> getPoints() {
        return intersections.stream().map(IntersectionPoint::getPoint).collect(Collectors.toList());
    }

    public Point2D.Double getFirstPoint() {
        return intersections.get(0).getPoint();
    }

    public Status getStatus() {
        return status;
    }

    public List<Double> getTs() {
        return intersections.stream().map(IntersectionPoint::getT1).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return intersections.isEmpty();
    }

    public int size() {
        return intersections.size();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Intersection{").append(status).append(", points=");
        boolean first = true;
        for (Point2D.Double p : getPoints()) {
            if (first) {
                first = false;
            } else {
                b.append(' ');
            }
            b.append(p.getX()).append(',').append(p.getY());
        }
        b.append(", ts=").append(getTs()).append('}');
        return b.toString();
    }

    public enum Status {
        /**
         * Shape 1 intersects with shape 2.
         */
        INTERSECTION,
        /**
         * Shape 1 does not intersect with shape 2.
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
        /** Shape 1 does not intersect with shape 2, and shape 1 is parallel to shape 2. */
        NO_INTERSECTION_PARALLEL
    }
}
