/* @(#)IntDoubleVertexGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

/**
 * A facade for a double vertex graph where the vertices
 * are integers from  {@code 0} to {@code vertexCount - 1}.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface IntDoubleVertexGraph {
    /** Returns the number of vertices {@code V}.
     * @return vertex count
     */
    int getVertexCount();
    
    /**
     * Returns the twin vertex of v.
     * @param v a vertex
     * @return the twin vertex of v
     */
    int getTwin(int v);
    
    /** Returns the number of adjacent vertices of v.
     * 
     * @param v a vertex
     * @return the number of adjacent vertices of v.
     */
    int getAdjacentCount(int v);
    
    /** Returns the i-th adjacent vertex of v.
     * 
     * @param v a vertex
     * @param i the index
     * @return the i-th adjacent vertex of v
     */
     int getAdjacent(int v, int i);
    
    /**
     * Gets the number of next vertices using the traversal rule: twin-adjacent.
     * @param v a vertex
     * @return number of next vertices
     */
    default int getNextCount(int v) {
        return getAdjacentCount(getTwin(v));
    }
    
    /**
     * Gets the i-th next vertex using the traversal rule: twin-adjacent.
     * @param v a vertex
     * @param i the index of the desired next vertex
     * @return the i-th next vertex
     */
    default int getNext(int v, int i) {
        return getAdjacent(getTwin(v), i);
    }
    
    /**
     * Gets the number of previous vertices using the traversal rule: twin-adjacent.
     * @param v a vertex
     * @return number of previous vertices
     */
    default int getPrevCount(int v) {
        return getAdjacentCount(v);
    }
    
    /**
     * Gets the i-th previous vertex using the traversal rule: twin-adjacent.
     * @param v a vertex
     * @param i the index of the desired previous vertex
     * @return the i-th previous vertex
     */
    default int getPrev(int v, int i) {
        return getTwin(getAdjacent(v, i));
    }
}
