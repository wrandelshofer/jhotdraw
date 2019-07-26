/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Spliterators;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * IntBreadthFirstSpliteratorTest.
 *
 * @author Werner Randelshofer
 */
public class IntBreadthFirstSpliteratorTest {

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
        IntDirectedGraph graph = createGraph();
        return new Object[][]{
                {graph, 0, 4, Arrays.asList(0, 1, 2, 5, 3, 4)},
                {graph, 0, 3, Arrays.asList(0, 1, 2, 5, 3)},
                {graph, 1, 5, Arrays.asList(1, 0, 2, 3, 5)}
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
            testIterate((IntDirectedGraph) args[0], (Integer) args[1], (Integer) args[2], (List<Integer>) args[3]);
        }
    }

    /**
     * Test of findAnyVertexPath method, of class
     * DirectedGraphPathBuilderWithArrows.
     */
    public void testIterate(IntDirectedGraph graph, Integer start, Integer goal, List<Integer> expResult) throws Exception {
        System.out.println("testIterate start:" + start + " goal:" + goal + " expResult:" + expResult);

        IntBreadthFirstSpliterator instance = new IntBreadthFirstSpliterator(graph::getNextVertices, start);
        List<Integer> result = new ArrayList<>();
        PrimitiveIterator.OfInt iter = Spliterators.iterator(instance);
        while (iter.hasNext()) {
            final int next = iter.nextInt();
            result.add(next);
            if (next == goal) {
                break;
            }
        }
        System.out.println("actual:" + result);
        assertEquals(expResult, result);
    }

}
