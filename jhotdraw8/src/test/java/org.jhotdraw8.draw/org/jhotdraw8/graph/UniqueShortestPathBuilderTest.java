/* @(#)AnyPathBuilderTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * UniqueShortestPathBuilderTest.
 *
 * @author Werner Randelshofer
 */
public class UniqueShortestPathBuilderTest {

    public UniqueShortestPathBuilderTest() {
    }

    private @NonNull DirectedGraph<Integer, Double> createGraph() {
        DirectedGraphBuilder<Integer, Double> builder = new DirectedGraphBuilder<>();

        // __|  1  |  2  |  3  |  4  |  5  |   6
        // 1 |       7.0   9.0  14.0         14.0
        // 2 | 7.0        10.0  15.0
        // 3 |                  11.0          2.0
        // 4 |                         6.0
        // 5 |                                9.0
        // 6 |14.0                     9.0
        //
        //

        builder.addVertex(1);
        builder.addVertex(2);
        builder.addVertex(3);
        builder.addVertex(4);
        builder.addVertex(5);
        builder.addVertex(6);
        builder.addBidiArrow(1, 2, 7.0);
        builder.addArrow(1, 3, 9.0);
        builder.addArrow(1, 4, 14.0);
        builder.addBidiArrow(1, 6, 14.0);
        builder.addArrow(2, 3, 10.0);
        builder.addArrow(2, 4, 15.0);
        builder.addArrow(3, 4, 11.0);
        builder.addArrow(3, 6, 2.0);
        builder.addArrow(4, 5, 6.0);
        builder.addBidiArrow(5, 6, 9.0);
        return builder;
    }

    private @NonNull DirectedGraph<Integer, Double> createDiamondGraph() {
        DirectedGraphBuilder<Integer, Double> builder = new DirectedGraphBuilder<>();

        // __|  1  |  2  |  3  |  4  |  5  |
        // 1 |       1.0   1.0
        // 2 |                   1.0
        // 3 |                   1.0
        // 4 |                         1.0
        //
        //

        builder.addVertex(1);
        builder.addVertex(2);
        builder.addVertex(3);
        builder.addVertex(4);
        builder.addVertex(5);
        builder.addArrow(1, 2, 1.0);
        builder.addArrow(1, 3, 1.0);
        builder.addArrow(2, 4, 1.0);
        builder.addArrow(3, 4, 1.0);
        builder.addArrow(4, 5, 1.0);
        return builder;
    }


    @Test
    public void testCreateGraph() {
        final DirectedGraph<Integer, Double> graph = createGraph();

        final String expected
                = "1 -> 2, 3, 4, 6.\n"
                + "2 -> 1, 3, 4.\n"
                + "3 -> 4, 6.\n"
                + "4 -> 5.\n"
                + "5 -> 6.\n"
                + "6 -> 1, 5.";

        final String actual = DumpGraphs.dumpAsAdjacencyList(graph);
        System.out.println(actual);

        assertEquals(expected, actual);
    }


    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFindShortestVertexPath() {
        DirectedGraph<Integer, Double> graph = createGraph();
        DirectedGraph<Integer, Double> diamondGraph = createDiamondGraph();
        return Arrays.asList(
                dynamicTest("graph.nonunique", () -> testFindShortestVertexPath(graph, 1, 5, null, 0.0)),
                dynamicTest("graph.2", () -> testFindShortestVertexPath(graph, 1, 4, VertexPath.of(1, 4), 14.0)),
                dynamicTest("graph.3", () -> testFindShortestVertexPath(graph, 2, 6, VertexPath.of(2, 3, 6), 12.0)),
                dynamicTest("graph.nopath", () -> testFindShortestVertexPath(graph, 2, 99, null, 0.0)),
                dynamicTest("diamond.1.nonunique", () -> testFindShortestVertexPath(diamondGraph, 1, 4, null, 0.0)),
                dynamicTest("diamond.2.nonunique", () -> testFindShortestVertexPath(diamondGraph, 1, 5, null, 0.0))
        );
    }

    /**
     * Test of findAnyPath method, of class UniqueShortestPathBuilder.
     */
    public void testFindShortestVertexPath(@NonNull DirectedGraph<Integer, Double> graph, @NonNull Integer start, @NonNull Integer goal, VertexPath<Integer> expPath, double expCost) throws Exception {
        System.out.println("doFindShortestVertexPath start:" + start + " goal:" + goal + " expResult:" + expPath + " expCost: " + expCost);

        ToDoubleFunction<Double> costf = arg -> arg;
        UniqueDoubleShortestPathBuilder<Integer, Double> instance = new UniqueDoubleShortestPathBuilder<>(graph::getNextArcs, costf);
        Map.Entry<VertexPath<Integer>, Double> result = instance.findVertexPath(start, goal::equals);
        if (result == null) {
            assertNull(expPath);
        } else {
            assertEquals(expPath, result.getKey());
            assertEquals(expCost, result.getValue().doubleValue());
        }
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFindShortestEdgeMultiGoalPath() throws Exception {
        DirectedGraph<Integer, Double> graph = createGraph();
        DirectedGraph<Integer, Double> diamondGraph = createDiamondGraph();
        return Arrays.asList(
                dynamicTest("graph.1.nonunique", () -> testFindShortestEdgeMultiGoalPath(graph, 1, Arrays.asList(5, 6), null)),
                dynamicTest("graph.2.nonunique", () -> testFindShortestEdgeMultiGoalPath(graph, 1, Arrays.asList(4, 5), null)),
                dynamicTest("graph.3", () -> testFindShortestEdgeMultiGoalPath(graph, 2, Arrays.asList(3, 6), org.jhotdraw8.graph.ArrowPath.of(10.0))),
                dynamicTest("graph.4.nonunique", () -> testFindShortestEdgeMultiGoalPath(graph, 1, Arrays.asList(6, 5), null)),
                dynamicTest("graph.5.nonunique", () -> testFindShortestEdgeMultiGoalPath(graph, 1, Arrays.asList(5, 4), null)),
                dynamicTest("graph.6", () -> testFindShortestEdgeMultiGoalPath(graph, 2, Arrays.asList(6, 3), ArrowPath.of(10.0))),
                dynamicTest("graph.7.unreachable", () -> testFindShortestEdgeMultiGoalPath(graph, 2, Arrays.asList(600, 300), null)),
                dynamicTest("diamond.1.nonunique", () -> testFindShortestEdgeMultiGoalPath(diamondGraph, 1, Arrays.asList(2, 3), null))
        );
    }

    /**
     * Test of findAnyPath method, of class UniqueShortestPathBuilder.
     */
    public void testFindShortestEdgeMultiGoalPath(@NonNull DirectedGraph<Integer, Double> graph, @NonNull Integer start, @NonNull List<Integer> multiGoal, ArrowPath<Double> expResult) throws Exception {
        System.out.println("doFindShortestEdgeMultiGoalPath start:" + start + " goal:" + multiGoal + " expResult:" + expResult);
        ToDoubleFunction<Double> costf = arg -> arg;
        UniqueDoubleShortestPathBuilder<Integer, Double> instance = new UniqueDoubleShortestPathBuilder<>(graph::getNextArcs, costf);

        // Find shortest path to any of the goals
        Map.Entry<ArrowPath<Double>, Double> actualShortestPath = instance.findArrowPath(start, multiGoal::contains);
        double actualLength = actualShortestPath == null ? 0.0 : actualShortestPath.getValue();

        // Find a path for each individual goal, and remember the shortest path
        List<ArrowPath<Double>> individualShortestPaths = new ArrayList<>();
        double individualShortestLength = Double.POSITIVE_INFINITY;
        for (Integer goal : multiGoal) {
            Map.Entry<ArrowPath<Double>, Double> resultEntry = instance.findArrowPath(start, goal::equals);
            if (resultEntry == null) {
                assertNull(expResult);
                return;
            } else {
                ArrowPath<Double> result = resultEntry.getKey();
                double resultLength = result.getArrows().stream().mapToDouble(Double::doubleValue).sum();
                if (resultLength < individualShortestLength) {
                    individualShortestLength = resultLength;
                    individualShortestPaths.clear();
                    individualShortestPaths.add(result);
                } else if (resultLength == individualShortestLength) {
                    individualShortestPaths.add(result);
                }
            }
        }

        System.out.println("  individual shortest paths: " + individualShortestPaths);
        System.out.println("  actual shortest path: " + actualShortestPath);

        assertEquals(expResult, actualShortestPath == null ? null : actualShortestPath.getKey());
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFindShortestEdgePath() throws Exception {
        return Arrays.asList(
                dynamicTest("1.nonunique", () -> testFindShortestEdgePath(1, 5, null)),
                dynamicTest("2", () -> testFindShortestEdgePath(1, 4, ArrowPath.of(14.0))),
                dynamicTest("3", () -> testFindShortestEdgePath(2, 6, ArrowPath.of(10.0, 2.0)))
        );
    }

    /**
     * Test of findAnyPath method, of class UniqueShortestPathBuilder.
     */
    private void testFindShortestEdgePath(@NonNull Integer start, @NonNull Integer goal, ArrowPath<Double> expResult) throws Exception {
        System.out.println("doFindShortestEdgePath start:" + start + " goal:" + goal + " expResult:" + expResult);
        DirectedGraph<Integer, Double> graph = createGraph();
        ToDoubleFunction<Double> costf = arg -> arg;
        UniqueDoubleShortestPathBuilder<Integer, Double> instance = new UniqueDoubleShortestPathBuilder<>(graph::getNextArcs, costf);
        Map.Entry<ArrowPath<Double>, Double> result = instance.findArrowPath(start, goal::equals);
        assertEquals(expResult, result == null ? null : result.getKey());
    }

    private @NonNull DirectedGraph<Integer, Double> createGraph2() {
        DirectedGraphBuilder<Integer, Double> b = new DirectedGraphBuilder<>();
        b.addVertex(1);
        b.addVertex(2);
        b.addVertex(3);
        b.addVertex(4);
        b.addVertex(5);

        b.addArrow(1, 2, 1.0);
        b.addArrow(1, 3, 1.0);
        b.addArrow(2, 3, 1.0);
        b.addArrow(3, 4, 1.0);
        b.addArrow(3, 5, 1.0);
        b.addArrow(4, 5, 1.0);
        return b;
    }


    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFindShortestVertexPathOverWaypoints() throws Exception {
        return Arrays.asList(
                dynamicTest("1", () -> testFindShortestVertexPathOverWaypoints(Arrays.asList(1, 3, 5), VertexPath.of(1, 3, 6, 5), 20.0)),
                dynamicTest("2", () -> testFindShortestVertexPathOverWaypoints(Arrays.asList(1, 4), VertexPath.of(1, 4), 14.0)),
                dynamicTest("3", () -> testFindShortestVertexPathOverWaypoints(Arrays.asList(2, 6), VertexPath.of(2, 3, 6), 12.0)),
                dynamicTest("4", () -> testFindShortestVertexPathOverWaypoints(Arrays.asList(1, 6, 5), VertexPath.of(1, 3, 6, 5), 20.0))
        );
    }

    /**
     * Test of findAnyVertexPath method, of class AnyPathBuilder.
     */
    private void testFindShortestVertexPathOverWaypoints(@NonNull List<Integer> waypoints, VertexPath<Integer> expResult, double expCost) throws Exception {
        System.out.println("doFindVertexPathOverWaypoints waypoints:" + waypoints + " expResult:" + expResult + " expCost:" + expCost);
        ToDoubleFunction<Double> costf = arg -> arg;
        DirectedGraph<Integer, Double> graph = createGraph();
        UniqueDoubleShortestPathBuilder<Integer, Double> instance = new UniqueDoubleShortestPathBuilder<>(graph::getNextArcs, costf);
        Map.Entry<VertexPath<Integer>, Double> actual = instance.findVertexPathOverWaypoints(waypoints);
        if (actual == null) {
            assertNull(expResult);
        } else {
            assertEquals(expResult, actual.getKey());
            assertEquals(expCost, actual.getValue().doubleValue());
        }
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFindEdgePathOverWaypoints() {
        return Arrays.asList(
                dynamicTest("1.nonunique", () -> testFindEdgePathOverWaypoints(Arrays.asList(1, 5), null, 0.0)),
                dynamicTest("2", () -> testFindEdgePathOverWaypoints(Arrays.asList(1, 4), ArrowPath.of(14.0), 14.0)),
                dynamicTest("3", () -> testFindEdgePathOverWaypoints(Arrays.asList(2, 6), ArrowPath.of(10.0, 2.0), 12.0)),
                dynamicTest("4", () -> testFindEdgePathOverWaypoints(Arrays.asList(1, 6, 5), ArrowPath.of(9.0, 2.0, 9.0), 20.0))
        );
    }

    /**
     * Test of findAnyVertexPath method, of class AnyPathBuilder.
     */
    private void testFindEdgePathOverWaypoints(@NonNull List<Integer> waypoints, ArrowPath<Double> expResult, double expCost) throws Exception {
        System.out.println("doFindVertexPathOverWaypoints waypoints:" + waypoints + " expResult:" + expResult);
        ToDoubleFunction<Double> costf = arg -> arg;
        DirectedGraph<Integer, Double> graph = createGraph();
        UniqueDoubleShortestPathBuilder<Integer, Double> instance = new UniqueDoubleShortestPathBuilder<>(graph::getNextArcs, costf);
        Map.Entry<ArrowPath<Double>, Double> actual = instance.findArrowPathOverWaypoints(waypoints, Integer.MAX_VALUE);
        if (actual == null) {
            assertNull(expResult);
        } else {
            assertEquals(expResult, actual.getKey());
            assertEquals(expCost, actual.getValue().doubleValue());
        }
    }
}