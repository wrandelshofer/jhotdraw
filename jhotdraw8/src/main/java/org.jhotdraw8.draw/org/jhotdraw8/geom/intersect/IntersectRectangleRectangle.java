package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectRectangleRectangle {
    private IntersectRectangleRectangle() {
    }

    @NonNull
    public static IntersectionResultEx intersectRectangleRectangleEx(double ax, double ay, double aw, double ah,
                                                                     double bx, double by, double bw, double bh) {
        return intersectRectangleRectangleEx(
                new Point2D.Double(ax, ay), new Point2D.Double(ax + aw, ay + ah),
                new Point2D.Double(bx, by), new Point2D.Double(bx + bw, by + bh));

    }

    /**
     * Computes the intersection between two rectangles 'a' and 'b'.
     *
     * @param a0 corner point 0 of rectangle 'a'
     * @param a1 corner point 1 of rectangle 'a'
     * @param b0 corner point 0 of rectangle 'b'
     * @param b1 corner point 1 of rectangle 'b'
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectRectangleRectangleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = Intersections.topLeft(a0, a1);
        bottomRight = Intersections.bottomRight(a0, a1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = IntersectLineRectangle.intersectLineRectangleEx(topLeft, topRight, b0, b1);
        inter2 = IntersectLineRectangle.intersectLineRectangleEx(topRight, bottomRight, b0, b1);
        inter3 = IntersectLineRectangle.intersectLineRectangleEx(bottomRight, bottomLeft, b0, b1);
        inter4 = IntersectLineRectangle.intersectLineRectangleEx(bottomLeft, topLeft, b0, b1);

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;

        if (inter1.getStatus() == IntersectionStatus.INTERSECTION) {
            result.addAll(inter1.asList());
        }
        if (inter2.getStatus() == IntersectionStatus.INTERSECTION) {
            result.addAll(inter2.asList());
        }
        if (inter3.getStatus() == IntersectionStatus.INTERSECTION) {
            result.addAll(inter3.asList());
        }
        if (inter4.getStatus() == IntersectionStatus.INTERSECTION) {
            result.addAll(inter4.asList());
        }

        return new IntersectionResultEx(result);
    }
}
