/* @(#)AbstractBidiGraphBuilder.java
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

/**
 * AbstractBidiGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AbstractBidiGraphBuilder extends AbstractDirectedGraphBuilder implements IntBidiGraph {

    /**
     * Table of arrow heads.
     * <p>
     * {@code arrows[i * ARROWS_NUM_FIELDS+ARROWS_VERTEX_FIELD} contains the
     * index of the vertex of the i-th arrow.
     * <p>
     * {@code arrows[i * ARROWS_NUM_FIELDS+ARROWS_NEXT_FIELD} contains the index
     * of the next arrow.
     */
    private int[] prevArrowHeads;

    /**
     * Table of last arrows.
     * <p>
     * {@code lastArrow[i * ARROWS_NUM_FIELDS+LASTARROW_POINTER_FIELD} contains
     * the index of the last arrow of the i-th vertex.
     * <p>
     * {@code lastArrow[i * ARROWS_NUM_FIELDS+LASTARROW_COUNT_FIELD} contains
     * the number of arrows of the i-th vertex.
     */
    private int[] prevLastArrow;

    public AbstractBidiGraphBuilder() {
        this(16, 16);
    }

    public AbstractBidiGraphBuilder(int vertexCapacity, int arrowCapacity) {
        if (vertexCapacity < 0) {
            throw new IllegalArgumentException("vertexCapacity: " + vertexCapacity);
        }
        if (arrowCapacity < 0) {
            throw new IllegalArgumentException("arrowCapacity: " + arrowCapacity);
        }
        this.prevArrowHeads = new int[arrowCapacity * ARROWS_NUM_FIELDS];
        this.prevLastArrow = new int[vertexCapacity * LASTARROW_NUM_FIELDS];
    }

    protected int getPrevArrowIndex(int vi, int i) {
        return getArrowIndex(vi, i, prevLastArrow, prevArrowHeads);
    }

    @Override
    public int getPrev(int vi, int i) {
        int arrowId = getPrevArrowIndex(vi, i);
        return prevArrowHeads[arrowId * ARROWS_NUM_FIELDS + ARROWS_VERTEX_FIELD];
    }

    @Override
    public int getPrevCount(int vi) {
        return prevLastArrow[vi * LASTARROW_NUM_FIELDS + LASTARROW_COUNT_FIELD];
    }

    /**
     * Builder-method: adds a directed arrow from 'a' to 'b'.
     *
     * @param vidxa vertex a
     * @param vidxb vertex b
     */
    @Override
    protected void buildAddArrow(int vidxa, int vidxb) {
        if (prevArrowHeads.length <= arrowCount * ARROWS_NUM_FIELDS) {
            int[] tmpArrowHeads = prevArrowHeads;
            prevArrowHeads = new int[prevArrowHeads.length * ARROWS_NUM_FIELDS];
            System.arraycopy(tmpArrowHeads, 0, prevArrowHeads, 0, tmpArrowHeads.length);
        }

        doAddArrow(vidxb, vidxa, prevArrowHeads, prevLastArrow);

        super.buildAddArrow(vidxa, vidxb);
    }

    /**
     * Removes the i-th arrow of vertex vi.
     *
     * @param vidx a vertex
     * @param i    the i-th arrow of the vertex
     */
    @Override
    protected void buildRemoveArrow(int vidx, int i) {
        int x = getNext(vidx, i);
        int xi = -1;
        for (int j = getPrevCount(x) - 1; j >= 0; j--) {
            if (getPrev(x, j) == vidx) {
                xi = j;
                break;
            }
        }
        if (xi == -1) {
            throw new RuntimeException("programming error");
        }

        buildRemoveArrow(x, xi, prevLastArrow, prevArrowHeads, arrowCount);
        super.buildRemoveArrow(vidx, i);
    }
}
