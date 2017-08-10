/* @(#)IntDoubleVertexGraphModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

/**
 * A facade for a directed graph where the vertices
 * are integers from  {@code 0} to {@code vertexCount - 1}.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface IntDirectedGraph {
    /** Returns the number of vertices {@code V}.
     * @return vertex count
     */
    int getVertexCount();
   
    
    /** Returns the number of next vertices of v.
     * 
     * @param v a vertex
     * @return the number of next vertices of v.
     */
    int getNextCount(int v);
    
    /** Returns the i-th next vertex of v.
     * 
     * @param v a vertex
     * @param i the index of the desired next vertex
     * @return i the index
     */
     int getNext(int v, int i);
    
}
