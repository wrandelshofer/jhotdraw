/*
 * @(#)AbstractDirectedGraphBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.Arrays;

/**
 * AbstractDirectedGraphBuilder.
 * <p>
 * <b>Implementation:</b>
 * <p>
 * Example graph:
 * <pre>
 *     0 ──→ 1 ──→ 2
 *     │     │
 *     ↓     ↓
 *     3 ←── 4
 * </pre>
 * If the graph is inserted in the following sequence
 * into the builder:
 * <pre>
 *     buildAddVertex();
 *     buildAddVertex();
 *     buildAddVertex();
 *     buildAddVertex();
 *     buildAddVertex();
 *     buildAddVertex();
 *     build.addArrow(0, 1);
 *     build.addArrow(0, 3);
 *     build.addArrow(1, 2);
 *     build.addArrow(1, 4);
 *     build.addArrow(4, 3);
 * </pre>
 * Then the internal representation is as follows:
 * <pre>
 *     vertexCount: 5
 *
 *  vertex#    lastArrow             arrow#    arrowHeads
 *           pointer,count                    vertex, next
 *    0     [  1  ][  2  ] ─────┐       0    [  1  ][ -1  ] ←┐
 *    1     [  2  ][  2  ] ───┐ └─────→ 1    [  3  ][  0  ] ─┘
 *    2     [  0  ][  0  ] X  │         2    [  2  ][ -1  ] ←┐
 *    3     [  0  ][  0  ] X  └───────→ 3    [  4  ][  2  ] ─┘
 *    4     [  4  ][  1  ] ───────────→ 4    [  3  ][ -1  ] X
 * </pre>
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractDirectedGraphBuilder {

    protected final static int ARROWS_NEXT_FIELD = 1;
    protected final static int ARROWS_NUM_FIELDS = 2;
    protected final static int ARROWS_VERTEX_FIELD = 0;
    protected final static int LASTARROW_COUNT_FIELD = 0;
    protected final static int LASTARROW_NUM_FIELDS = 2;
    protected final static int LASTARROW_POINTER_FIELD = 1;
    protected final static int SENTINEL = -1;

    protected int arrowCount;
    /**
     * Table of arrow heads.
     * <p>
     * {@code arrows[i * ARROWS_NUM_FIELDS+ARROWS_VERTEX_FIELD} contains the
     * index of the vertex of the i-th arrow.
     * <p>
     * {@code arrows[i * ARROWS_NUM_FIELDS+ARROWS_NEXT_FIELD} contains the index
     * of the next arrow.
     */
    private int[] nextArrowHeads;

    /**
     * Table of last arrows.
     * <p>
     * {@code lastArrow[i * ARROWS_NUM_FIELDS+LASTARROW_POINTER_FIELD} contains
     * the index of the last arrow of the i-th vertex in table {@link #nextArrowHeads}.
     * <p>
     * {@code lastArrow[i * ARROWS_NUM_FIELDS+LASTARROW_COUNT_FIELD} contains
     * the number of arrows of the i-th vertex.
     */
    private int[] nextLastArrow;

    /**
     * The vertex count.
     */
    private int vertexCount;

    public AbstractDirectedGraphBuilder() {
        this(16, 16);
    }

    public AbstractDirectedGraphBuilder(int vertexCapacity, int arrowCapacity) {
        if (vertexCapacity < 0) {
            throw new IllegalArgumentException("vertexCapacity: " + vertexCapacity);
        }
        if (arrowCapacity < 0) {
            throw new IllegalArgumentException("arrowCapacity: " + arrowCapacity);
        }
        this.nextArrowHeads = new int[arrowCapacity * ARROWS_NUM_FIELDS];
        this.nextLastArrow = new int[vertexCapacity * LASTARROW_NUM_FIELDS];
    }

    /**
     * Builder-method: adds a directed arrow from 'a' to 'b'.
     *
     * @param a vertex a
     * @param b vertex b
     */
    protected void buildAddArrow(int a, int b) {
        if (nextArrowHeads.length <= arrowCount * ARROWS_NUM_FIELDS) {
            int[] tmpArrowHeads = nextArrowHeads;
            nextArrowHeads = new int[nextArrowHeads.length * ARROWS_NUM_FIELDS];
            System.arraycopy(tmpArrowHeads, 0, nextArrowHeads, 0, tmpArrowHeads.length);
        }

        doAddArrow(a, b, nextArrowHeads, nextLastArrow);

        arrowCount++;
    }

    /**
     * Builder-method: adds a directed arrow from 'a' to 'b'.
     *
     * @param a          vertex a
     * @param b          vertex b
     * @param lastArrow  the array of last arrows
     * @param arrowHeads the array of arrow heads
     */
    protected void doAddArrow(int a, int b, int[] arrowHeads, int[] lastArrow) {
        int arrowCountOfA = lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD];
        int lastArrowIdOfA = arrowCountOfA == 0 ? SENTINEL : lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD];

        int newLastArrowIdOfA = arrowCount;
        arrowHeads[newLastArrowIdOfA * ARROWS_NUM_FIELDS + ARROWS_VERTEX_FIELD] = b;
        arrowHeads[newLastArrowIdOfA * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD] = lastArrowIdOfA;

        lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD] = arrowCountOfA + 1;
        lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD] = newLastArrowIdOfA;
    }

    /**
     * Builder-method: adds a vertex.
     */
    protected void buildAddVertex() {
        vertexCount++;
        if (nextLastArrow.length < vertexCount * LASTARROW_NUM_FIELDS) {
            int[] tmp = nextLastArrow;
            nextLastArrow = new int[nextLastArrow.length * 2 * LASTARROW_NUM_FIELDS];
            System.arraycopy(tmp, 0, nextLastArrow, 0, tmp.length);
        }
    }

    public int getArrowCount() {
        return arrowCount;
    }

    /**
     * Builder-method: sets the vertex count.
     *
     * @param newValue the new vertex count, must be larger or equal the current
     *                 vertex count.
     */
    protected void buildSetVertexCount(int newValue) {
        if (newValue < vertexCount) {
            throw new IllegalArgumentException("can only add vertices:" + newValue);
        }
        vertexCount = newValue;
        if (nextLastArrow.length < vertexCount * LASTARROW_NUM_FIELDS) {
            int[] tmp = nextLastArrow;
            nextLastArrow = new int[nextLastArrow.length * LASTARROW_NUM_FIELDS];
            System.arraycopy(tmp, 0, nextLastArrow, 0, tmp.length);
        }

    }

    /**
     * Removes the i-th arrow of vertex vi.
     *
     * @param a a vertex
     * @param i the i-th arrow of vertex vi
     */
    protected void buildRemoveArrow(int a, int i) {
        buildRemoveArrow(a, i, nextLastArrow, nextArrowHeads, arrowCount);
        arrowCount--;
    }

    /**
     * Removes the i-th arrow of vertex v.
     *
     * @param vidx       the index of the vertex v
     * @param i          the i-th arrow of vertex v
     * @param lastArrow  the array of last arrows
     * @param arrowHeads the array of arrow heads
     * @param arrowCount the number of arrows
     */
    protected void buildRemoveArrow(int vidx, int i, int[] lastArrow, int[] arrowHeads, int arrowCount) {
        if (vidx < 0 || vidx >= getVertexCount()) {
            throw new IllegalArgumentException("a:" + i);
        }
        if (i < 0 || i >= getNextCount(vidx)) {
            throw new IllegalArgumentException("i:" + i);
        }

        // find the arrowId and the previous arrowId
        int prevArrowId = SENTINEL;
        int arrowId = lastArrow[vidx * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD];
        for (int j = i - 1; j >= 0; j--) {
            prevArrowId = arrowId;
            arrowId = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        }

        if (prevArrowId == SENTINEL) {
            // if there is no previous arrowId => make the point from lastArrow point to the arrow after arrowId.
            lastArrow[vidx * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD] = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        } else {
            // if there is a previous arrowId => make the pointer from prevArrowId point to the arrow after arrowId.
            arrowHeads[prevArrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD] = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        }
        // Decrease number of arrows for vertex vi
        lastArrow[vidx * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD]--;

        // Decrease total number of arrows
        arrowCount--;

        int moveArrowId = arrowCount;
        if (arrowId != moveArrowId) {
            // move moveArrowId to arrowId
            arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_VERTEX_FIELD] = arrowHeads[moveArrowId * ARROWS_NUM_FIELDS + ARROWS_VERTEX_FIELD];
            arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD] = arrowHeads[moveArrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
            // if there is a pointer in lastArrows to moveArrowId, make it point to arrowId. 
            boolean fixed = false;
            for (int v = 0; v < vertexCount; v++) {
                if (lastArrow[v * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD] > 0
                        && lastArrow[v * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD] == moveArrowId) {
                    lastArrow[v * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD] = arrowId;
                    fixed = true;
                    break;
                }
            }
            // if there is a pointer in arrows to moveArrowId, make it point to arrowId. 
            if (!fixed) {
                for (int e = 0; e < arrowCount; e++) {
                    if (arrowHeads[e * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD] == moveArrowId) {
                        arrowHeads[e * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD] = arrowId;
                        fixed = true;
                        break;
                    }
                }
            }
        }
    }

    protected int getNextArrowIndex(int vi, int i) {
        return getArrowIndex(vi, i, nextLastArrow, nextArrowHeads);
    }

    protected int getArrowIndex(int vi, int i, int[] lastArrow, int[] arrowHeads) {
        int arrowId = lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD];
        int nextCount = lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD];
        for (int j = nextCount - 1; j > i; j--) {
            arrowId = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        }
        return arrowId;
    }

    public int getNext(int vi, int i) {
        int arrowId = getNextArrowIndex(vi, i);
        return nextArrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_VERTEX_FIELD];
    }

    public int getNextCount(int vi) {
        return nextLastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD];
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void clear() {
        arrowCount = 0;
        vertexCount = 0;
        Arrays.fill(nextArrowHeads, 0);
        Arrays.fill(nextLastArrow, 0);
    }
}
