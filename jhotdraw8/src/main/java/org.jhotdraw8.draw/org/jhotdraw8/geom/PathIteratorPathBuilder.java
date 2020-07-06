/*
 * @(#)PathIteratorPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.PathIterator;

public class PathIteratorPathBuilder extends AbstractPathBuilder {
    private int numCommands;
    private int numCoords;
    @NonNull
    private byte[] commands = new byte[10];
    @NonNull
    private double[] coords = new double[60];


    private void needRoom() {
        if (numCommands >= commands.length) {
            int newSize = commands.length + 10;
            if (newSize < commands.length) {
                throw new ArrayIndexOutOfBoundsException("Can't expand commands array.");
            }
            byte[] temp = commands;
            commands = new byte[newSize];
            System.arraycopy(temp, 0, commands, 0, temp.length);
        }
        if (numCoords + 6 >= coords.length) {
            int newSize = coords.length + 60;
            if (newSize < commands.length) {
                throw new ArrayIndexOutOfBoundsException("Can't expand coords array.");
            }
            double[] temp = coords;
            coords = new double[newSize];
            System.arraycopy(temp, 0, coords, 0, temp.length);
        }
    }

    @Override
    protected void doClosePath() {
        needRoom();
        commands[numCommands++] = PathIterator.SEG_CLOSE;
    }

    @Override
    protected void doPathDone() {
        // empty
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        if (numCommands == 0) {
            throw new IllegalStateException("Missing initial moveto in path definition.");
        }
        needRoom();
        commands[numCommands++] = PathIterator.SEG_CUBICTO;
        coords[numCoords++] = x1;
        coords[numCoords++] = y1;
        coords[numCoords++] = x2;
        coords[numCoords++] = y2;
        coords[numCoords++] = x;
        coords[numCoords++] = y;
    }

    @Override
    protected void doLineTo(double x, double y) {
        if (numCommands == 0) {
            throw new IllegalStateException("Missing initial moveto in path definition.");
        }
        needRoom();
        commands[numCommands++] = PathIterator.SEG_LINETO;
        coords[numCoords++] = x;
        coords[numCoords++] = y;
    }

    @Override
    protected void doMoveTo(double x, double y) {
        needRoom();
        commands[numCommands++] = PathIterator.SEG_MOVETO;
        coords[numCoords++] = x;
        coords[numCoords++] = y;
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x, double y) {
        if (numCommands == 0) {
            throw new IllegalStateException("Missing initial moveto in path definition.");
        }
        needRoom();
        commands[numCommands++] = PathIterator.SEG_QUADTO;
        coords[numCoords++] = x1;
        coords[numCoords++] = y1;
        coords[numCoords++] = x;
        coords[numCoords++] = y;
    }

    @NonNull
    public PathIterator build() {
        return new MyPathIterator(numCommands, numCoords, commands, coords);
    }

    private static class MyPathIterator implements PathIterator {
        private int commandIndex = 0;
        private int coordsIndex = 0;
        private final int numCommands;
        private final int numCoords;
        private final byte[] commands;
        private final double[] coords;
        private static final int[] curvecoords = {2, 2, 4, 6, 0};

        public MyPathIterator(int numCommands, int numCoords, byte[] commands, double[] coords) {
            this.numCommands = numCommands;
            this.numCoords = numCoords;
            this.commands = commands;
            this.coords = coords;
        }

        @Override
        public int getWindingRule() {
            return PathIterator.WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return commandIndex >= numCommands;
        }

        @Override
        public void next() {
            if (!isDone()) {
                int type = commands[commandIndex++];
                coordsIndex += curvecoords[type];
            }
        }

        @Override
        public int currentSegment(float[] coords) {
            int type = commands[commandIndex];
            for (int i = 0, n = curvecoords[type]; i < n; i++) {
                coords[i] = (float) this.coords[i + coordsIndex];
            }
            return type;
        }

        @Override
        public int currentSegment(double[] coords) {
            int type = commands[commandIndex];
            for (int i = 0, n = curvecoords[type]; i < n; i++) {
                coords[i] = this.coords[i + coordsIndex];
            }
            return type;
        }
    }

}
