/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * IntBreadthFirstVertexIteratorTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntBreadthFirstVertexIteratorTest {

    private IntDirectedGraph createGraph() {
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.addVertex();
        builder.addVertex();
        builder.addVertex();
        builder.addVertex();
        builder.addVertex();
        builder.addVertex();

        builder.addBidiArrow(0, 1);
        builder.addArrow(0, 2);
        builder.addBidiArrow(0, 5);
        builder.addArrow(1, 2);
        builder.addArrow(1, 3);
        builder.addArrow(2, 3);
        builder.addArrow(2, 5);
        builder.addArrow(3, 4);
        builder.addBidiArrow(4, 5);
        return builder;
    }

    public Object[][] anyPathProvider() {
        return new Object[][]{
            {1, 5, Arrays.asList(1, 2, 3, 6, 4, 5)},
            {1, 4, Arrays.asList(1, 2, 3, 6, 4)},
            {2, 6, Arrays.asList(2, 1, 3, 4, 6)}
        };
    }

    @Test
    public void testCreateGraph() {
        final IntDirectedGraph graph = createGraph();

        final String expected
                = "0 -> 1, 2, 5.\n"
                + "1 -> 0, 2, 3.\n"
                + "2 -> 3, 5.\n"
                + "3 -> 4.\n"
                + "4 -> 5.\n"
                + "5 -> 0, 4.";

        final String actual = IntDirectedGraphs.dumpAsAdjacencyMap(graph);
        System.out.println(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void testIterateWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testIterate((Integer) args[0], (Integer) args[1], (List<Integer>) args[2]);
        }
    }

    /**
     * Test of findAnyVertexPath method, of class
     * DirectedGraphPathBuilderWithArrows.
     */
    public void testIterate(Integer start, Integer goal, List<Integer> expResult) throws Exception {
        System.out.println("testIterate start:" + start + " goal:" + goal + " expResult:" + expResult);
        IntDirectedGraph graph = createGraph();
        IntBreadthFirstVertexIterator instance = new IntBreadthFirstVertexIterator(graph, start);
        List<Integer> result = new ArrayList<>();
        while (instance.hasNext()) {
            final Integer next = instance.next();
            result.add(next);
            if (next == goal) {
                break;
            }
        }
        System.out.println("actual:" + result);
        assertEquals(expResult, result);
    }

}
