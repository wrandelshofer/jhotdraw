/* @(#)IntDirectedGraphPathBuilderNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import java.util.Collection;
import java.util.function.ToDoubleFunction;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * IntDirectedGraphPathBuilderNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntDirectedGraphPathBuilderNGTest {

    public IntDirectedGraphPathBuilderNGTest() {
    }
    
    private IntDirectedGraph createGraph() {
        IntDirectedGraphBuilder builder=new IntDirectedGraphBuilder();
        builder.setVertexCount(6);
        builder.addBidiArrow(0, 1);
        builder.addBidiArrow(0, 2);
        builder.addBidiArrow(0, 5);
        builder.addBidiArrow(1, 2);
        builder.addBidiArrow(1, 3);
        builder.addBidiArrow(2, 3);
        builder.addBidiArrow(2, 5);
        builder.addBidiArrow(3, 4);
        builder.addBidiArrow(4, 5);
        return builder;
    }
    
    @DataProvider
    public Object[][] anyPathProvider() {
        return new Object[][] {
                {0,4, VertexPath.of(0,5,4)},
            {0,3,VertexPath.of(0,1,3)},
            {1,5,VertexPath.of(1,0,5)}
        } ;
    }
    

    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithArrows.
     */
    @Test(dataProvider = "anyPathProvider")
    public void testFindAnyPath_3args(Integer start, Integer goal, VertexPath<Integer> expResult ) throws Exception {
        System.out.println("findShortestPath");
        IntDirectedGraph graph = createGraph();
        IntDirectedGraphPathBuilder instance = new IntDirectedGraphPathBuilder();
        VertexPath<Integer> result = instance.findAnyVertexPath(graph, start, goal);
        assertEquals(result, expResult);
    }


}