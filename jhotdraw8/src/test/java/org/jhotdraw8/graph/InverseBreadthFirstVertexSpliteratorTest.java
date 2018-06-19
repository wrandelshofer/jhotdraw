/* @(#)InverseBreadthFirstVertexSpliteratorTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
InverseBreadthFirstVertexSpliteratorTest *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class InverseBreadthFirstVertexSpliteratorTest {

    private BidiGraph<Integer, Double> createGraph() {
        BidiGraphBuilder<Integer, Double> builder = new BidiGraphBuilder<>();
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
        return new Object[][]{
            {1, 5, Arrays.asList(1, 2, 3, 6, 4, 5)},
            {1, 4, Arrays.asList(1, 2, 3, 6, 4)},
            {2, 6, Arrays.asList(2, 1, 3, 4, 6)}
        };
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
    public void testIterateWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testIterate((Integer) args[0], (Integer) args[1], (List<Integer>) args[2]);
        }
    }

    public void testIterate(Integer start, Integer goal, List<Integer> expResult) throws Exception {
        System.out.println("testIterate start:" + start + " goal:" + goal + " expResult:" + expResult);
        DirectedGraph<Integer, Double> graph = createGraph();
        BreadthFirstSpliterator<Integer> instance = new BreadthFirstSpliterator<>(graph, start);
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
    @Test
    public void testTryAdvanceWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testTryAdvance((Integer) args[0], (Integer) args[1], (List<Integer>) args[2]);
        }
    }

    public void testTryAdvance(Integer start, Integer goal, List<Integer> expResult) throws Exception {
        System.out.println("testForEachRemaining start:" + start + " goal:" + goal + " expResult:" + expResult);
        DirectedGraph<Integer, Double> graph = createGraph();
        BreadthFirstSpliterator<Integer> instance = new BreadthFirstSpliterator<>(graph, start);
        List<Integer> result = new ArrayList<>();
        while (instance.tryAdvance(result::add)) {
            if (result.get(result.size()-1).equals(goal))break;
        }
        System.out.println("actual:" + result);
        assertEquals(expResult, result);
    }

}
