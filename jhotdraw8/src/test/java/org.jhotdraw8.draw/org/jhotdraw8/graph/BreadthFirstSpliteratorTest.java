/* @(#)BreadthFirstSpliteratorTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * BreadthFirstSpliteratorTest.
 *
 * @author Werner Randelshofer
 */
public class BreadthFirstSpliteratorTest {

    private @NonNull DirectedGraph<Integer, Double> createGraph() {
        DirectedGraphBuilder<Integer, Double> builder = new DirectedGraphBuilder<>();
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

    public @NonNull Object[][] anyPathProvider() {
        final DirectedGraph<Integer, Double> graph = createGraph();

        return new Object[][]{
                {graph, 1, 5, Arrays.asList(1, 2, 3, 6, 4, 5)},
                {graph, 1, 4, Arrays.asList(1, 2, 3, 6, 4)},
                {graph, 2, 6, Arrays.asList(2, 1, 3, 4, 6)}
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

        final String actual = DumpGraphs.dumpAsAdjacencyList(graph);
        System.out.println(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void testIterateWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testIterate((DirectedGraph<Integer, Double>) args[0], (Integer) args[1], (Integer) args[2], (List<Integer>) args[3]);
        }
    }

    static void testIterate(@NonNull DirectedGraph<Integer, Double> graph, @NonNull Integer start, Integer goal, List<Integer> expResult) throws Exception {
        System.out.println("testIterate start:" + start + " goal:" + goal + " expResult:" + expResult);
        BreadthFirstSpliterator<Integer> instance = new BreadthFirstSpliterator<>(graph::getNextVertices, start);
        List<Integer> result = new ArrayList<>();
        Iterator<Integer> iter = Spliterators.iterator(instance);
        while (iter.hasNext()) {
            final Integer next = iter.next();
            result.add(next);
            if (next.equals(goal)) {
                break;
            }
        }
        System.out.println("actual:" + result);
        assertEquals(expResult, result);
    }

    @Test
    public void testForEachRemainingWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testTryAdvance((DirectedGraph<Integer, Double>) args[0], (Integer) args[1], (Integer) args[2], (List<Integer>) args[3]);
        }
    }

    public void testTryAdvance(@NonNull DirectedGraph<Integer, Double> graph, @NonNull Integer start, Integer goal, List<Integer> expResult) throws Exception {
        System.out.println("testForEachRemaining start:" + start + " goal:" + goal + " expResult:" + expResult);
        BreadthFirstSpliterator<Integer> instance = new BreadthFirstSpliterator<>(graph::getNextVertices, start);
        List<Integer> result = new ArrayList<>();
        while (instance.tryAdvance(result::add)) {
            if (result.get(result.size() - 1).equals(goal)) {
                break;
            }
        }

        System.out.println("actual:" + result);
        assertEquals(expResult, result);
    }

    @Test
    public void testTrySplitWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testTrySplit((DirectedGraph<Integer, Double>) args[0], (Integer) args[1], (Integer) args[2], (List<Integer>) args[3]);
        }
    }

    public void testTrySplit(@NonNull DirectedGraph<Integer, Double> graph, @NonNull Integer start, Integer goal, @NonNull List<Integer> expResult) throws Exception {

        System.out.println("testTrySplit start:" + start + " goal:" + goal + " expResult:" + expResult);

        Queue<Spliterator<Integer>> splits = new ArrayDeque<>();
        splits.add(new BreadthFirstSpliterator<>(graph::getNextVertices, start));
        List<Integer> result = new ArrayList<>();
        while (!splits.isEmpty()) {
            Spliterator<Integer> instance = splits.remove();
            while (instance.tryAdvance(result::add)) {
                if (result.get(result.size() - 1).equals(goal)) {
                    break;
                }
                final Spliterator<Integer> splitOff = instance.trySplit();
                if (splitOff != null) {
                    splits.add(splitOff);
                }
            }
        }
        System.out.println("actual:    " + result);

        // Splitting changes the order of the iterator. We don't care.
        expResult.sort(Comparator.naturalOrder());
        result.sort(Comparator.naturalOrder());
        assertEquals(expResult, result);
    }

    @Test
    public void testTrySplitParallelWithAnyPathProvider() throws Exception {
        for (Object[] args : anyPathProvider()) {
            testTrySplitParallel((DirectedGraph<Integer, Double>) args[0], (Integer) args[1], (Integer) args[2], (List<Integer>) args[3]);
        }
    }

    public void testTrySplitParallel(@NonNull DirectedGraph<Integer, Double> graph, @NonNull Integer start, Integer goal, @NonNull List<Integer> expResult) throws Exception {

        System.out.println("testTrySplit start:" + start + " goal:" + goal + " expResult:" + expResult);

        Queue<Spliterator<Integer>> splits = new ArrayDeque<>();
        final BreadthFirstSpliterator<Integer> instance = new BreadthFirstSpliterator<>(graph::getNextVertices, start);
        List<Integer> result = new ArrayList<>();
        instance.tryAdvance(result::add);// we can never split at start vertex, because it is the only vertex in the que
        result.addAll(StreamSupport.stream(instance, true).collect(Collectors.toList()));
        System.out.println("actual:    " + result);

        // Splitting changes the order of the iterator. We don't care.
        expResult.sort(Comparator.naturalOrder());
        result.sort(Comparator.naturalOrder());
        // assertEquals(expResult, result);
    }


}
