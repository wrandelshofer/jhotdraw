/* @(#)DirectedGraphWithEdgesPathBuilderNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import java.util.function.ToDoubleFunction;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DirectedGraphWithEdgesPathBuilderNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DirectedGraphWithEdgesPathBuilderNGTest {

    public DirectedGraphWithEdgesPathBuilderNGTest() {
    }

    private DirectedGraphWithEdges<Integer,Double>createGraph() {
        DirectedGraphWithEdgesBuilder<Integer,Double> builder=new DirectedGraphWithEdgesBuilder<>();
        builder.addVertex(1);
        builder.addVertex(2);
        builder.addVertex(3);
        builder.addVertex(4);
        builder.addVertex(5);
        builder.addVertex(6);
        builder.addBidiEdge(1, 2, 7.0);
        builder.addBidiEdge(1, 3, 9.0);
        builder.addBidiEdge(1, 6, 14.0);
        builder.addBidiEdge(2, 3, 10.0);
        builder.addBidiEdge(2, 4, 15.0);
        builder.addBidiEdge(3, 4, 11.0);
        builder.addBidiEdge(3, 6, 2.0);
        builder.addBidiEdge(4, 5, 6.0);
        builder.addBidiEdge(5, 6, 9.0);
        return builder;
    }
    
    @DataProvider
    public Object[][] shortestVertexPathProvider() {
        return new Object[][] {
                {1,5, VertexPath.of(1,3,6,5)},
            {1,4,VertexPath.of(1,3,4)},
            {2,6,VertexPath.of(2,3,6)}
        } ;
    }
    @DataProvider
    public Object[][] shortestEdgePathProvider() {
        return new Object[][] {
                {1,5,EdgePath.of(9.0,2.0,9.0)},
            {1,4,EdgePath.of(9.0,11.0)},
            {2,6,EdgePath.of(10.0,2.0)}
        } ;
    }
    
    /**
     * Test of findAnyPath method, of class DirectedGraphWithEdgesPathBuilder.
     */
    @Test(dataProvider = "shortestVertexPathProvider")
    public void testFindShortestVertexPath(Integer start, Integer goal, VertexPath<Integer> expResult ) throws Exception {
        System.out.println("findShortestPath");
        DirectedGraphWithEdges<Integer, Double> graph = createGraph();
        ToDoubleFunction<Double> costf = arg->arg;
        DirectedGraphWithEdgesPathBuilder<Integer,Double> instance = new DirectedGraphWithEdgesPathBuilder<>();
        VertexPath<Integer> result = instance.findShortestVertexPath(graph, start, goal, costf);
        assertEquals(result, expResult);
    }
    /**
     * Test of findAnyPath method, of class DirectedGraphWithEdgesPathBuilder.
     */
    @Test(dataProvider = "shortestEdgePathProvider")
    public void testFindShortestEdgePath(Integer start, Integer goal, EdgePath<Double> expResult ) throws Exception {
        System.out.println("findShortestPath");
        DirectedGraphWithEdges<Integer, Double> graph = createGraph();
        ToDoubleFunction<Double> costf = arg->arg;
        DirectedGraphWithEdgesPathBuilder<Integer,Double> instance = new DirectedGraphWithEdgesPathBuilder<>();
        EdgePath<Double> result = instance.findShortestEdgePath(graph, start, goal, costf);
        assertEquals(result, expResult);
    }
    @DataProvider
    public Object[][] anyVertexPathProvider() {
        return new Object[][] {
                {1,5, VertexPath.of(1,6,5)},
            {1,4,VertexPath.of(1,2,4)},
            {2,6,VertexPath.of(2,1,6)}
        } ;
    }
    

    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithEdges.
     */
    @Test(dataProvider = "anyVertexPathProvider")
    public void testFindAnyVertexPath_3args(Integer start, Integer goal, VertexPath<Integer> expResult ) throws Exception {
        System.out.println("findAnyVertexPath");
        DirectedGraphWithEdges<Integer,Double> graph = createGraph();
        DirectedGraphWithEdgesPathBuilder<Integer,Double> instance = new DirectedGraphWithEdgesPathBuilder<>();
        VertexPath<Integer> result = instance.findAnyVertexPath(graph, start, goal);
        assertEquals(result, expResult);
    }
    @DataProvider
    public Object[][] anyEdgePathProvider() {
        return new Object[][] {
                {1,5, EdgePath.of(14.0,9.0)},
            {1,4,EdgePath.of(7.0,15.0)},
            {2,6,EdgePath.of(7.0,14.0)}
        } ;
    }
    

    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithEdges.
     */
    @Test(dataProvider = "anyEdgePathProvider")
    public void testFindAnyEdgePath_3args(Integer start, Integer goal, EdgePath<Double> expResult ) throws Exception {
        System.out.println("findAnyVertexPath");
        DirectedGraphWithEdges<Integer,Double> graph = createGraph();
        DirectedGraphWithEdgesPathBuilder<Integer,Double> instance = new DirectedGraphWithEdgesPathBuilder<>();
        EdgePath<Double> result = instance.findAnyEdgePath(graph, start, goal);
        assertEquals(result, expResult);
    }


}