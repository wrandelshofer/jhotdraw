package org.jhotdraw8.graph;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class GraphSearchTest {
    private DirectedGraph<String, Integer> createDisjointGraph() {
        DirectedGraphBuilder<String, Integer> builder = new DirectedGraphBuilder<>();
        builder.addVertex("a");
        builder.addVertex("b");
        builder.addVertex("c");
        builder.addVertex("d");
        builder.addVertex("A");
        builder.addVertex("B");
        builder.addVertex("C");
        builder.addVertex("D");

        builder.addBidiArrow("a", "b", 1);
        builder.addArrow("b", "c", 1);
        builder.addBidiArrow("c", "d", 1);
        builder.addBidiArrow("A", "B", 1);
        builder.addArrow("B", "C", 1);
        builder.addBidiArrow("C", "D", 1);
        return builder;
    }

    private DirectedGraph<String, Integer> createLoopGraph() {
        DirectedGraphBuilder<String, Integer> builder = new DirectedGraphBuilder<>();
        builder.addVertex("a");
        builder.addVertex("b");
        builder.addVertex("c");
        builder.addVertex("d");

        builder.addArrow("a", "b", 1);
        builder.addArrow("b", "c", 1);
        builder.addBidiArrow("c", "d", 1);
        builder.addBidiArrow("d", "a", 1);
        return builder;
    }

    private DirectedGraph<String, Integer> createNonMSTGraph() {
        // Graph with more edges than the minimal spanning tree:
        // A--1--B     C
        // |   / |   / |
        // 5  3  2  4  5
        // |/    |/    |
        // D--4--E--7--F

        DirectedGraphBuilder<String, Integer> builder = new DirectedGraphBuilder<>();
        builder.addVertex("A");
        builder.addVertex("B");
        builder.addVertex("C");
        builder.addVertex("D");
        builder.addVertex("E");
        builder.addVertex("F");

        builder.addBidiArrow("A", "B", 1);
        builder.addBidiArrow("A", "D", 5);
        builder.addBidiArrow("B", "D", 3);
        builder.addBidiArrow("B", "E", 2);
        builder.addBidiArrow("C", "E", 4);
        builder.addBidiArrow("C", "F", 5);
        builder.addBidiArrow("D", "E", 4);
        builder.addBidiArrow("E", "F", 7);
        return builder;
    }

    private DirectedGraph<String, Integer> createMSTGraph() {
        // Graph with only the edges for minimal spanning tree:
        // A--1--B     C
        //     / |   / |
        //    3  2  4  5
        //  /    |/    |
        // D     E     F

        DirectedGraphBuilder<String, Integer> builder = new DirectedGraphBuilder<>();
        builder.addVertex("A");
        builder.addVertex("B");
        builder.addVertex("C");
        builder.addVertex("D");
        builder.addVertex("E");
        builder.addVertex("F");

        builder.addBidiArrow("A", "B", 1);
        builder.addBidiArrow("B", "E", 2);
        builder.addBidiArrow("B", "D", 3);
        builder.addBidiArrow("C", "E", 4);
        builder.addBidiArrow("C", "F", 5);
        return builder;
    }

    @TestFactory
    public List<DynamicTest> testFindDisjointSets() {
        return Arrays.asList(
                dynamicTest("1", () -> doFindDisjointSets(createDisjointGraph(), 2)),
                dynamicTest("2", () -> doFindDisjointSets(createLoopGraph(), 1))
        );
    }

    void doFindDisjointSets(DirectedGraph<String, Integer> graph, int expectedSetCount) {
        System.out.println("find disjoint sets");
        System.out.println("graph:");
        System.out.println(DumpGraphs.dumpAsAdjacencyList(graph));

        List<Set<String>> actualSets = GraphSearch.findDisjointSets(graph);
        System.out.println("disjoint sets:");
        System.out.println(actualSets);

        assertEquals(expectedSetCount, actualSets.size());
    }


    @Test
    void findMinimumSpanningTree() {
        DirectedGraph<String, Integer> nonMst = createNonMSTGraph();
        DirectedGraph<String, Integer> expectedMst = createMSTGraph();
        DirectedGraphBuilder<String, Integer> actualMst = GraphSearch.findMinimumSpanningTreeGraph(nonMst, Integer::doubleValue);
        System.out.println(DumpGraphs.dumpAsAdjacencyList(nonMst));
        System.out.println(DumpGraphs.dumpAsAdjacencyList(expectedMst));
        System.out.println(DumpGraphs.dumpAsAdjacencyList(actualMst));
        assertEquals(DumpGraphs.dumpAsAdjacencyList(expectedMst), DumpGraphs.dumpAsAdjacencyList(actualMst));
    }


    @Test
    void findMinimumSpanningTreeGraph() {
    }

    @Test
    void sortTopologically() {
    }

    @Test
    void sortTopologicallyInt() {
    }

    @TestFactory
    public List<DynamicTest> testSearchStronglyConnectedComponents() {
        return Arrays.asList(
                dynamicTest("1", () -> doSearchStronglyConnectedComponents(createDisjointGraph(), 4)),
                dynamicTest("2", () -> doSearchStronglyConnectedComponents(createLoopGraph(), 1))
        );
    }

    void doSearchStronglyConnectedComponents(DirectedGraph<String, Integer> graph, int expectedSetCount) {
        System.out.println("find strongly connected components");
        System.out.println("graph:");
        System.out.println(DumpGraphs.dumpAsAdjacencyList(graph));

        List<List<String>> actualSets = GraphSearch.findStronglyConnectedComponents(graph);
        System.out.println("strongly connected components");
        System.out.println(actualSets);

        assertEquals(expectedSetCount, actualSets.size());
    }
}