/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DepthFirstIteratorNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DepthFirstIteratorNGTest {

    /**
     * <pre>
     * 1 ←→ 2
     * 1 → 3
     * 1 ←→ 6
     * 2 → 3
     * 2 → 4
     * 3 → 4
     * 3 → 6
     * 4 → 5
     * 5 ←→ 6
     * </pre>
     *
     * @return
     */
    private DirectedGraph<Integer, Double> createGraph() {
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

    @DataProvider
    public Object[][] anyPathProvider() {
        return new Object[][]{
            {1, 5, Arrays.asList(1, 6, 5)},
            {1, 4, Arrays.asList(1, 6, 5, 3, 4)},
            {2, 6, Arrays.asList(2, 4, 5, 6)}
        };
    }

    /**
     * Test of findAnyVertexPath method, of class DirectedGraphPathBuilderWithArrows.
     */
    @Test(dataProvider = "anyPathProvider")
    public void testIterate(Integer start, Integer goal, List<Integer> expResult) throws Exception {
        System.out.println("testIterate");
        DirectedGraph<Integer, Double> graph = createGraph();
        DepthFirstVertexIterator<Integer> instance = new DepthFirstVertexIterator<>(graph, start);
        List<Integer> result = new ArrayList<>();
        while (instance.hasNext()) {
            final Integer next = instance.next();
            result.add(next);
            if (next == goal) {
                break;
            }
        }
        System.out.println("actual:" + result);
        assertEquals(result, expResult);
    }

}
