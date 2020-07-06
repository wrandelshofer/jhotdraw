package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class DepthFirstArcSpliteratorTest {
    @NonNull
    protected DirectedGraph<Integer, Integer> createDoubleVertexGraph() {
        final DirectedGraphBuilder<Integer, Integer> builder = new DirectedGraphBuilder<>();

        //  (1)->(2)------------->(3)------->(4)----
        //      ↗︎                         ↗︎         \
        //     /                         /           ↘
        //  (5)------->(6)------->(7)----            (9)
        //                \                         ↗︎
        //                  ↘                     /
        //                   (8)-----------------


        for (int i = 1; i < 10; i++) {
            builder.addVertex(i);
            builder.addVertex(i * 10);
        }
        builder.addArrow(1, 2, 100);
        builder.addArrow(2, 3, 100);
        builder.addArrow(3, 4, 100);
        builder.addArrow(4, 9, 100);
        builder.addArrow(5, 2, 100);
        builder.addArrow(5, 6, 100);
        builder.addArrow(6, 7, 100);
        builder.addArrow(6, 8, 100);
        builder.addArrow(7, 4, 100);
        builder.addArrow(8, 9, 100);
        return builder.build();
    }

    @TestFactory
    public List<DynamicTest> testIterator() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestIterator(createDoubleVertexGraph(), Arrays.asList(5, 9),
                        "5->6:100,6->8:100,8->9:100,6->7:100,7->4:100,4->9:100,5->2:100,2->3:100,3->4:100"))
        );
    }

    private void doTestIterator(DirectedGraph<Integer, Integer> graph, List<Integer> waypoints, String expected) {
        Set<Integer> goals = new HashSet<>(waypoints);
        StringBuilder buf = new StringBuilder();
        for (Integer root : waypoints) {
            DepthFirstArcSpliterator<Integer, Integer> itr = new DepthFirstArcSpliterator<>(graph::getNextArcs, root);
            while (itr.moveNext()) {
                Arc<Integer, Integer> current = itr.current();
                if (buf.length() > 0) {
                    buf.append(",");
                }
                buf.append(current.getStart() + "->" + current.getEnd() + ":" + current.getData());
            }
        }
        String actual = buf.toString();
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> testPathBuilding() {
        return Arrays.asList(
                dynamicTest("5->", () -> doTestPathBuilding(createDoubleVertexGraph(), Arrays.asList(5),
                        "[VertexPath{[5, 6, 8, 9]}, VertexPath{[6, 7, 4, 9]}, VertexPath{[5, 2, 3, 4]}]")),
                dynamicTest("1->", () -> doTestPathBuilding(createDoubleVertexGraph(), Arrays.asList(1),
                        "[VertexPath{[1, 2, 3, 4, 9]}]"))
        );
    }

    private void doTestPathBuilding(DirectedGraph<Integer, Integer> graph, List<Integer> waypoints,
                                    String expected) {
        List<VertexPath<Integer>> paths = new ArrayList<>();
        List<Integer> path = null;
        for (Integer root : waypoints) {
            DepthFirstArcSpliterator<Integer, Integer> itr = new DepthFirstArcSpliterator<>(graph::getNextArcs, root);
            while (itr.moveNext()) {
                Arc<Integer, Integer> current = itr.current();
                if (path == null) {
                    path = new ArrayList<>();
                    path.add(current.getStart());
                    path.add(current.getEnd());
                } else if (path.get(path.size() - 1).equals(current.getStart())) {
                    path.add(current.getEnd());
                } else {
                    paths.add(new VertexPath<>(path));
                    path = new ArrayList<>();
                    path.add(current.getStart());
                    path.add(current.getEnd());
                }
            }
        }
        if (path != null) {
            paths.add(new VertexPath<Integer>(path));
        }

        String actual = paths.toString();
        assertEquals(expected, actual);
    }
}
