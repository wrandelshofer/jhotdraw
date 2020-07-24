/*
 * @(#)Intersection.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.isect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.DoubleArrayList;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A container object that contains the result of an intersection test.
 */
public class IntersectionResult {


    @NonNull
    private final List<IntersectionPoint> ipoints;
    private final IntersectionStatus status;

    public IntersectionResult(@NonNull List<IntersectionPoint> intersections) {
        this(intersections.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION, intersections);
    }

    public IntersectionResult(IntersectionStatus status) {
        this(status, Collections.emptyList());
    }

    public IntersectionResult(IntersectionStatus status, @NonNull List<IntersectionPoint> intersections) {
        this.ipoints = Collections.unmodifiableList(intersections);
        this.status = status;
    }

    @NonNull
    public List<IntersectionPoint> getIntersections() {
        return ipoints;
    }

    @NonNull
    public IntersectionPoint getFirst() {
        return ipoints.get(0);
    }

    public Point2D.Double getLastPoint() {
        return ipoints.get(ipoints.size() - 1).getPoint();
    }

    public double getLastParameterA() {
        return ipoints.get(ipoints.size() - 1).getParameterA();
    }

    @Nullable
    public IntersectionPoint getLastIntersectionPoint() {
        return ipoints.isEmpty() ? null : ipoints.get(ipoints.size() - 1);
    }

    public double getFirstParameterA() {
        return ipoints.get(0).getParameterA();
    }

    public double getFirstParameterB() {
        return ipoints.get(0).getParameterB();
    }

    public List<Point2D.Double> getPoints() {
        return ipoints.stream().map(IntersectionPoint::getPoint).collect(Collectors.toList());
    }

    public Point2D.Double getFirstPoint() {
        return ipoints.get(0).getPoint();
    }

    public IntersectionStatus getStatus() {
        return status;
    }

    public DoubleArrayList getAllParametersB() {
        return ipoints.stream()
                .mapToDouble(IntersectionPoint::getParameterB)
                .collect(DoubleArrayList::new, DoubleArrayList::add, DoubleArrayList::addAll);
    }

    public DoubleArrayList getAllParametersA() {
        return ipoints.stream()
                .mapToDouble(IntersectionPoint::getParameterA)
                .collect(DoubleArrayList::new, DoubleArrayList::add, DoubleArrayList::addAll);
    }

    public boolean isEmpty() {
        return ipoints.isEmpty();
    }

    public int size() {
        return ipoints.size();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Intersection{").append(status).append(", points=");
        b.append(ipoints);
        b.append('}');
        return b.toString();
    }

    public IntersectionPoint get(int i) {
        return ipoints.get(i);
    }
}
