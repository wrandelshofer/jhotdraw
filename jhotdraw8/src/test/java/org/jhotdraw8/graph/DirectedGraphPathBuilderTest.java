/* @(#)DirectedGraphPathBuilderTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DirectedGraphPathBuilderTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DirectedGraphPathBuilderTest {

    public DirectedGraphPathBuilderTest() {
    }
    
    private DirectedGraph<Integer,Double>createGraph() {
        DirectedGraphBuilder<Integer,Double> builder=new DirectedGraphBuilder<>();
        builder.addVertex(1);
        builder.addVertex(2);
        builder.addVertex(3);
        builder.addVertex(4);
        builder.addVertex(5);
        builder.addVertex(6);
        builder.addBidiArrow(1, 2, 7.0);
        builder.addArrow(1, 3, 9.0);
        builder.addBidiArrow(1, 6, 14.0);
        builder.addArrow(2, 3, 10.0);
        builder.addArrow(2, 4, 15.0);
        builder.addArrow(3, 4, 11.0);
        builder.addArrow(3, 6, 2.0);
        builder.addArrow(4, 5, 6.0);
        builder.addBidiArrow(5, 6, 9.0);
        return builder;
    }
    
    public Object[][] anyPathProvider() {
        return new Object[][] {
                {1,5, VertexPath.of(1,6,5)},
            {1,4,VertexPath.of(1,2,4)},
            {2,6,VertexPath.of(2,1,6)}
        } ;
    }
    
    @Test
    public void testCreateGraph() {
        final DirectedGraph<Integer, Double> graph = createGraph();

        final String expected
                = "1 -> 2, 3, 6.\n"
                + "2 -> 1, 3, 4.\n"
                + "3 -> 4, 6.\n"
                + "4 -> 5.\n"
                + "5 -> 6.\n"
                + "6 -> 1, 5.";

        final String actual = DirectedGraphs.dumpAsAdjacencyList(graph);
        System.out.println(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindAnyPath_3argsWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testFindAnyPath_3args((Integer) args[0], (Integer) args[1], (VertexPath<Integer>) args[2]);
        }
    }

 
    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithArrows.
     */
    public void testFindAnyPath_3args(Integer start, Integer goal, VertexPath<Integer> expResult ) throws Exception {
        System.out.println("findAnyPath start:"+start+" goal:"+goal+" expResult:"+expResult);
        DirectedGraph<Integer,Double> graph = createGraph();
        DirectedGraphPathBuilder<Integer,Double> instance = new DirectedGraphPathBuilder<>();
        VertexPath<Integer> result = instance.findAnyVertexPath(graph, start, goal);
        assertEquals(result, expResult);
    }

    public Object[][] shortestVertexPathProvider() {
        return new Object[][] {
                {1,5, VertexPath.of(1,3,6,5)},
            {1,4,VertexPath.of(1,3,4)},
            {2,6,VertexPath.of(2,3,6)}
        } ;
    }
    public Object[][] shortestEdgePathProvider() {
        return new Object[][] {
                {1,5,EdgePath.of(9.0,2.0,9.0)},
            {1,4,EdgePath.of(9.0,11.0)},
            {2,6,EdgePath.of(10.0,2.0)}
        } ;
    }
    

    @Test
    public void testFindShortestVertexPathWithShortestVertexPathProvider() throws Exception {
        for (Object[] args : shortestVertexPathProvider()) {
            testFindShortestVertexPath((Integer) args[0], (Integer) args[1], (VertexPath<Integer>) args[2]);
        }
    }
    /**
     * Test of findAnyPath method, of class DirectedGraphWithArrowsPathBuilder.
     */
    public void testFindShortestVertexPath(Integer start, Integer goal, VertexPath<Integer> expResult ) throws Exception {
        System.out.println("findShortestVertexPath start:"+start+" goal:"+goal+" expResult:"+expResult);
        DirectedGraph<Integer, Double> graph = createGraph();
        ToDoubleFunction<Double> costf = arg->arg;
        DirectedGraphPathBuilder<Integer,Double> instance = new DirectedGraphPathBuilder<>();
        VertexPath<Integer> result = instance.findShortestVertexPath(graph, start, goal, costf);
        assertEquals(result, expResult);
    }

    @Test
    public void testFindShortestEdgePathWithShortestEdgePathProvider() throws Exception {
        for (Object[] args : shortestEdgePathProvider()) {
            testFindShortestEdgePath((Integer) args[0], (Integer) args[1], (EdgePath<Double>) args[2]);
        }
    }
    /**
     * Test of findAnyPath method, of class DirectedGraphWithArrowsPathBuilder.
     */
    public void testFindShortestEdgePath(Integer start, Integer goal, EdgePath<Double> expResult ) throws Exception {
        System.out.println("findShortestEdgePath start:"+start+" goal:"+goal+" expResult:"+expResult);
        DirectedGraph<Integer, Double> graph = createGraph();
        ToDoubleFunction<Double> costf = arg->arg;
        DirectedGraphPathBuilder<Integer,Double> instance = new DirectedGraphPathBuilder<>();
        EdgePath<Double> result = instance.findShortestEdgePath(graph, start, goal, costf);
        assertEquals(result, expResult);
    }

    public Object[][] anyVertexPathProvider() {
        return new Object[][] {
                {1,5, VertexPath.of(1,6,5)},
            {1,4,VertexPath.of(1,2,4)},
            {2,6,VertexPath.of(2,1,6)}
        } ;
    }
    


    @Test
    public void testFindAnyVertexPath_3argsWithAnyVertexPathProvider() throws Exception {
        for (Object[] args : anyVertexPathProvider()) {
            testFindAnyVertexPath_3args((Integer) args[0], (Integer) args[1], (VertexPath<Integer>) args[2]);
        }
    }
    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithArrows.
     */
    public void testFindAnyVertexPath_3args(Integer start, Integer goal, VertexPath<Integer> expResult ) throws Exception {
        System.out.println("findAnyVertexPath start:"+start+" goal:"+goal+" expResult:"+expResult);
        DirectedGraph<Integer,Double> graph = createGraph();
        DirectedGraphPathBuilder<Integer,Double> instance = new DirectedGraphPathBuilder<>();
        VertexPath<Integer> result = instance.findAnyVertexPath(graph, start, goal);
        assertEquals(result, expResult);
    }

    public Object[][] anyEdgePathProvider() {
        return new Object[][] {
                {1,5, EdgePath.of(14.0,9.0)},
            {1,4,EdgePath.of(7.0,15.0)},
            {2,6,EdgePath.of(7.0,14.0)}
        } ;
    }
    

    @Test
    public void testFindAnyEdgePath_3argsWithAnyEdgePathProvider() throws Exception {
        for (Object[] args : anyEdgePathProvider()) {
            testFindAnyEdgePath_3args((Integer) args[0], (Integer) args[1], (EdgePath<Double>) args[2]);
        }
    }
    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithArrows.
     */
    public void testFindAnyEdgePath_3args(Integer start, Integer goal, EdgePath<Double> expResult ) throws Exception {
        System.out.println("findAnyVertexPath start:"+start+" goal:"+goal+" expResult:"+expResult);
        DirectedGraph<Integer,Double> graph = createGraph();
        DirectedGraphPathBuilder<Integer,Double> instance = new DirectedGraphPathBuilder<>();
        EdgePath<Double> result = instance.findAnyEdgePath(graph, start, goal);
        assertEquals(result, expResult);
    }

}