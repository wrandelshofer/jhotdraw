package org.jhotdraw8.geom;

import java.awt.geom.Point2D;
import java.util.DoubleSummaryStatistics;

public class Points2D {
    public static Point2D.Double add(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() + b.getX(),a.getY() + b.getY());
    }
     public static Point2D.Double add(Point2D a,  double bx,double by) {
        return new Point2D.Double(a.getX() + bx,a.getY() + by);
    }
    public static Point2D.Double sum(Point2D a, Point2D b, Point2D... more) {
        DoubleSummaryStatistics x=new DoubleSummaryStatistics();
        DoubleSummaryStatistics y=new DoubleSummaryStatistics();
        x.accept(a.getX());
        x.accept(b.getX());
        y.accept(a.getY());
        y.accept(b.getY());
        for (Point2D p:more) {
            x.accept(p.getX());
            y.accept(p.getY());
        }
        return new Point2D.Double(x.getSum(),y.getSum());
    }
    public static Point2D.Double subtract(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() - b.getX(),a.getY() - b.getY());
    }
    public static Point2D.Double subtract(double ax,double ay, double bx,double by) {
        return new Point2D.Double(ax - bx,ay -by);
    }
    public static Point2D.Double add(double ax,double ay, double bx,double by) {
        return new Point2D.Double(ax + bx,ay +by);
    }
    public static Point2D.Double multiply(Point2D a, double v) {
        return new Point2D.Double(a.getX() *v,a.getY() *v);
    }
    public static Point2D.Double divide(Point2D a, double v) {
        return new Point2D.Double(a.getX() /v,a.getY() /v);
    }
    public static double magnitude(Point2D a) {
        final double x = a.getX();
        final double y = a.getY();
        return Math.sqrt(x * x + y * y);
    }
    public static double magnitudeSq(Point2D a) {
        final double x = a.getX();
        final double y = a.getY();
        return x * x + y * y;
    }

    public static Point2D.Double normalize(Point2D a) {
        final double mag = magnitude(a);

        if (mag == 0.0) {
            return new Point2D.Double(0.0, 0.0);
        }

        return new Point2D.Double(
                a.getX() / mag,
                a.getY() / mag);
    }

    public static double dotProduct(Point2D a, Point2D b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }
}
