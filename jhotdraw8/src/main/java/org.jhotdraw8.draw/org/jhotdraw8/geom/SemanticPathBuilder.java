/*
 * @(#)SemanticPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import java.awt.geom.Path2D;
import java.util.Arrays;

public class SemanticPathBuilder<T> extends AbstractPathBuilder {
    private final Path2D.Double path;
    public final static int CLOSE_PATH = 1;
    public final static int CURVE_TO = 2;
    public final static int LINE_TO = 3;
    public final static int MOVE_TO = 4;
    public final static int QUAD_TO = 5;
    private double[] coords = new double[0];
    private int[] ops = new int[0];
    private int[] offsets = new int[0];
    private int size;
    private int lastOffset;
    @SuppressWarnings("unchecked")
    private T[] data = (T[]) new Object[0];

    public SemanticPathBuilder() {
        this.path = new Path2D.Double();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    protected void doClosePath() {
        grow(CLOSE_PATH);
        path.closePath();
    }

    private void grow(int opcode, double... coords) {
        //grow by 50 % if capacity is not sufficient
        if (this.coords.length <= lastOffset + coords.length) {
            double[] temp = this.coords;
            this.coords = new double[Math.max(16, (lastOffset + coords.length) + (lastOffset + coords.length) / 2)];
            System.arraycopy(temp, 0, this.coords, 0, temp.length);
        }
        if (ops.length <= size) {
            int[] temp = ops;
            ops = new int[Math.max(16, size + size / 2)];
            System.arraycopy(temp, 0, ops, 0, temp.length);
            temp = offsets;
            offsets = new int[Math.max(16, size + size / 2)];
            System.arraycopy(temp, 0, offsets, 0, temp.length);
            Object[] tmp = data;
            @SuppressWarnings("unchecked")
            Object[] suppress = data = (T[]) new Object[Math.max(16, size + size / 2)];
            System.arraycopy(tmp, 0, data, 0, tmp.length);
        }
        ops[size] = opcode;
        System.arraycopy(coords, 0, this.coords, lastOffset, coords.length);
        offsets[size] = lastOffset;
        lastOffset += coords.length;
        size++;
    }

    @Override
    protected void doPathDone() {


    }

    public void setData(T data) {
        this.data[size - 1] = data;
    }

    public int size() {
        return size;
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        grow(CURVE_TO, x1, y1, x2, y2, x, y);
        path.curveTo(x1, y1, x2, y2, x, y);
    }

    @Override
    protected void doLineTo(double x, double y) {
        grow(LINE_TO, x, y);
        path.lineTo(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        grow(MOVE_TO, x, y);
        path.moveTo(x, y);
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x, double y) {
        grow(QUAD_TO, x1, y1, x, y);
        path.quadTo(x1, y1, x, y);
    }

    public Path2D.Double build() {
        pathDone();
        return path;
    }

    public void clear() {
        path.reset();
        size = lastOffset = 0;
        Arrays.fill(data, null);
        Arrays.fill(coords, 0.0);
        Arrays.fill(ops, 0);
        Arrays.fill(offsets, 0);
    }
}
