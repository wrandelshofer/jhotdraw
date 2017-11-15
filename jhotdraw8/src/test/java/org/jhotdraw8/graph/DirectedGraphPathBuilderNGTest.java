/* @(#)DirectedGraphPathBuilderNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import java.util.Collection;
import java.util.function.ToDoubleFunction;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DirectedGraphPathBuilderNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DirectedGraphPathBuilderNGTest {

    public DirectedGraphPathBuilderNGTest() {
    }
    
    private DirectedGraph<Integer>createGraph() {
        DirectedGraphBuilder<Integer,Double> builder=new DirectedGraphBuilder<>();
        builder.addVertex(1);
        builder.addVertex(2);
        builder.addVertex(3);
        builder.addVertex(4);
        builder.addVertex(5);
        builder.addVertex(6);
        builder.addBidiArrow(1, 2, 7.0);
        builder.addBidiArrow(1, 3, 9.0);
        builder.addBidiArrow(1, 6, 14.0);
        builder.addBidiArrow(2, 3, 10.0);
        builder.addBidiArrow(2, 4, 15.0);
        builder.addBidiArrow(3, 4, 11.0);
        builder.addBidiArrow(3, 6, 2.0);
        builder.addBidiArrow(4, 5, 6.0);
        builder.addBidiArrow(5, 6, 9.0);
        return builder;
    }
    
    @DataProvider
    public Object[][] anyPathProvider() {
        return new Object[][] {
                {1,5, VertexPath.of(1,6,5)},
            {1,4,VertexPath.of(1,2,4)},
            {2,6,VertexPath.of(2,1,6)}
        } ;
    }
    

    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithArrows.
     */
    @Test(dataProvider = "anyPathProvider")
    public void testFindAnyPath_3args(Integer start, Integer goal, VertexPath<Integer> expResult ) throws Exception {
        System.out.println("findAnyPath");
        DirectedGraph<Integer> graph = createGraph();
        DirectedGraphPathBuilder<Integer> instance = new DirectedGraphPathBuilder<>();
        VertexPath<Integer> result = instance.findAnyVertexPath(graph, start, goal);
        assertEquals(result, expResult);
    }


}