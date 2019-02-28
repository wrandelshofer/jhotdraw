/* @(#)IntDirectedGraphPathBuilderTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * IntDirectedGraphPathBuilderTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntDirectedGraphPathBuilderTest {

    public IntDirectedGraphPathBuilderTest() {
    }

    private IntDirectedGraph createGraph() {
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
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

    public Object[][] anyPathProvider() {
        return new Object[][]{
                {0, 4, VertexPath.of(0, 5, 4)},
                {0, 3, VertexPath.of(0, 1, 3)},
                {1, 5, VertexPath.of(1, 0, 5)}
        };
    }

    @Test
    public void testCreateGraph() {
        final IntDirectedGraph graph = createGraph();

        final String expected
                = "0 -> 1, 2, 5.\n"
                + "1 -> 0, 2, 3.\n"
                + "2 -> 0, 1, 3, 5.\n"
                + "3 -> 1, 2, 4.\n"
                + "4 -> 3, 5.\n"
                + "5 -> 0, 2, 4.";

        final String actual = IntDirectedGraphs.dumpAsAdjacencyMap(graph);
        System.out.println(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindAnyVertexPath_3argsWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testFindAnyVertexPath_3args((Integer) args[0], (Integer) args[1], (VertexPath<Integer>) args[2]);
        }
    }

    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithArrows.
     */
    public void testFindAnyVertexPath_3args(Integer start, Integer goal, VertexPath<Integer> expResult) throws Exception {
        System.out.println("findAnyVertexPath start:" + start + " goal:" + goal + " expResult:" + expResult);
        IntDirectedGraph graph = createGraph();
        IntDirectedGraphPathBuilder instance = new IntDirectedGraphPathBuilder();
        VertexPath<Integer> result = instance.findAnyVertexPath(graph, start, goal);
        assertEquals(result, expResult);
    }

}
