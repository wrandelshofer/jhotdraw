/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <A> arrow type
 */
public abstract class AbstractDirectedGraphBuilder<A> implements IntDirectedGraph<A> {

    private final static int ARROWS_NEXT_FIELD = 1;
    private final static int ARROWS_NUM_FIELDS = 2;
    private final static int ARROWS_VERTEX_FIELD = 0;
    private final static int LASTARROW_COUNT_FIELD = 0;
    private final static int LASTARROW_NUM_FIELDS = 2;
    private final static int LASTARROW_POINTER_FIELD = 1;
    private final static int SENTINEL = -1;

    private int arrowCount;
    /**
     * Table of arrow heads.
     * <p>
     * {@code arrows[i * ARROWS_NUM_FIELDS+ARROWS_VERTEX_FIELD} contains the index
     * of the vertex of the i-th arrow.
     * <p>
     * {@code arrows[i * ARROWS_NUM_FIELDS+ARROWS_NEXT_FIELD} contains the index of
     * the next arrow.
     */
    private int[] arrowHeads;

    /**
     * Table of last arrows.
     * <p>
     * {@code lastArrow[i * ARROWS_NUM_FIELDS+LASTARROW_POINTER_FIELD} contains
     * the index of the last arrow of the i-th vertex.
     * <p>
     * {@code lastArrow[i * ARROWS_NUM_FIELDS+LASTARROW_COUNT_FIELD} contains
     * the number of arrows of the i-th vertex.
     */
    private int[] lastArrow;
    
    
    private Object[] arrows;
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
        this.arrowHeads = new int[arrowCapacity * ARROWS_NUM_FIELDS];
        this.lastArrow = new int[vertexCapacity * LASTARROW_NUM_FIELDS];
        this.arrows = new Object[arrowCapacity];
    }

    /**
     * Builder-method: adds a directed arrow from 'a' to 'b'.
     *
     * @param a vertex a
     * @param b vertex b
     * @param arrow the arrow from 'a' to 'b'.
     */
    protected void buildAddArrow(int a, int b, A arrow) {
        if (arrowHeads.length <= arrowCount * ARROWS_NUM_FIELDS) {
            int[] tmpArrowHeads = arrowHeads;
            arrowHeads = new int[arrowHeads.length * ARROWS_NUM_FIELDS];
            System.arraycopy(tmpArrowHeads, 0, arrowHeads, 0, tmpArrowHeads.length);
            
            Object[] tmpArrows = arrows;
            arrows = new Object[arrows.length * ARROWS_NUM_FIELDS];
            System.arraycopy(tmpArrows, 0, arrows, 0, tmpArrows.length);
        }

        int arrowCountOfA = lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD];
        int lastArrowIdOfA = arrowCountOfA == 0 ? SENTINEL : lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD];

        int newLastArrowIdOfA = arrowCount;
        arrowHeads[newLastArrowIdOfA * ARROWS_NUM_FIELDS + ARROWS_VERTEX_FIELD] = b;
        arrowHeads[newLastArrowIdOfA * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD] = lastArrowIdOfA;

        lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD] = arrowCountOfA + 1;
        lastArrow[a * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD] = newLastArrowIdOfA;

        arrows[newLastArrowIdOfA]=arrow;
        
        arrowCount++;
    }

    /**
     * Builder-method: adds a vertex.
     */
    protected void buildAddVertex() {
        vertexCount++;
        if (lastArrow.length < vertexCount * LASTARROW_NUM_FIELDS) {
            int[] tmp = lastArrow;
            lastArrow = new int[lastArrow.length * 2 * LASTARROW_NUM_FIELDS];
            System.arraycopy(tmp, 0, lastArrow, 0, tmp.length);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public A getArrow(int index) {
        return (A)arrows[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public A getArrow(int vi, int i) {
        int arrowId = getArrowIndex(vi, i);
        return (A)arrows[arrowId];
    }

    @Override
    public int getArrowCount() {
        return arrowCount;
    }

    protected void buildSetVertexCount(int newValue) {
        if (newValue < vertexCount) {
            throw new IllegalArgumentException("can only add vertices:" + newValue);
        }
        vertexCount = newValue;
        if (lastArrow.length < vertexCount * LASTARROW_NUM_FIELDS) {
            int[] tmp = lastArrow;
            lastArrow = new int[lastArrow.length * LASTARROW_NUM_FIELDS];
            System.arraycopy(tmp, 0, lastArrow, 0, tmp.length);
        }
        
    }
    
    /**
     * Removes the i-th arrow of vertex vi.
     *
     * @param vi a vertex
     * @param i the i-th arrow of vertex vi
     */
    protected void buildRemoveArrow(int vi, int i) {
        if (vi < 0 || vi >= getVertexCount()) {
            throw new IllegalArgumentException("vi:" + i);
        }
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i:" + i);
        }

        // find the arrowId and the previous arrowId
        int prevArrowId = SENTINEL;
        int arrowId = lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD];
        for (int j = i - 1; j >= 0; j--) {
            prevArrowId = arrowId;
            arrowId = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        }

        if (prevArrowId == SENTINEL) {
            // if there is no previous arrowId => make the point from lastArrow point to the arrow after arrowId.
            lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD] = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        } else {
            // if there is a previous arrowId => make the pointer from prevArrowId point to the arrow after arrowId.
            arrowHeads[prevArrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD] = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        }
        // Decrease number of arrows for vertex vi
        lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD]--;

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

    protected int getArrowIndex(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("0 <= i(" + i + ") <= " + getNextCount(vi));
        }
        int arrowId = lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_POINTER_FIELD];
        int nextCount = lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD];
        for (int j = nextCount - 1; j > i; j--) {
            arrowId = arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_NEXT_FIELD];
        }
        return arrowId;
    }

    @Override
    public int getNext(int vi, int i) {
        int arrowId = getArrowIndex(vi, i);
        return arrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_VERTEX_FIELD];
    }

    @Override
    public int getNextCount(int vi) {
        return lastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD];
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

}
