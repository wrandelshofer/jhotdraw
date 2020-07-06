package org.jhotdraw8.geom.offsetline;

public class MinMax {
    public final double first;
    public final double second;

    public MinMax(double a, double b) {
        if (a < b) {
            this.first = a;
            this.second = b;
        } else {
            this.first = b;
            this.second = a;
        }
    }

    public double first() {
        return first;
    }

    public double second() {
        return second;
    }
}
