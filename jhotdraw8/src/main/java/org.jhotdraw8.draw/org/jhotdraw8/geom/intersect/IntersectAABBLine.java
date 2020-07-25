package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectAABBLine {
    private IntersectAABBLine() {
    }

    @NonNull
    public static IntersectionResultEx intersectLineAABBEx(double a0x, double a0y, double a1x, double a1y,
                                                           double rminx, double rminy, double rmaxx, double rmaxy) {
        return intersectLineAABBEx(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), rminx, rminy, rmaxx, rmaxy);
    }

    @NonNull
    public static IntersectionResultEx intersectLineAABBEx(@NonNull Point2D a0, @NonNull Point2D a1,
                                                           double rminx, double rminy, double rmaxx, double rmaxy) {

        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = new Point2D.Double(rminx, rminy);
        bottomRight = new Point2D.Double(rmaxx, rmaxy);
        topRight = new Point2D.Double(rmaxx, rminy);
        bottomLeft = new Point2D.Double(rminx, rmaxy);

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = IntersectLineLine.intersectLineLineEx(a0, a1, topLeft, topRight);
        inter2 = IntersectLineLine.intersectLineLineEx(a0, a1, topRight, bottomRight);
        inter3 = IntersectLineLine.intersectLineLineEx(a0, a1, bottomRight, bottomLeft);
        inter4 = IntersectLineLine.intersectLineLineEx(a0, a1, bottomLeft, topLeft);

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
    }

    @NonNull
    public static IntersectionResultEx intersectAABBLineEx(
            double rminx, double rminy, double rmaxx, double rmaxy,
            @NonNull Point2D a0, @NonNull Point2D a1) {

        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = new Point2D.Double(rminx, rminy);
        bottomRight = new Point2D.Double(rmaxx, rmaxy);
        topRight = new Point2D.Double(rmaxx, rminy);
        bottomLeft = new Point2D.Double(rminx, rmaxy);

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = IntersectLineLine.intersectLineLineEx(topRight, topLeft, a0, a1);
        inter2 = IntersectLineLine.intersectLineLineEx(bottomRight, topRight, a0, a1);
        inter3 = IntersectLineLine.intersectLineLineEx(bottomLeft, bottomRight, a0, a1);
        inter4 = IntersectLineLine.intersectLineLineEx(topLeft, bottomLeft, a0, a1);

        List<IntersectionPointEx> result = new ArrayList<>();
        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());
        return new IntersectionResultEx(result);
    }
}
