/* @(#)ImmutableIntDirectedGraphTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * ImmutableIntDirectedGraphTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ImmutableIntDirectedGraphTest {

    public ImmutableIntDirectedGraphTest() {
    }

    /**
     * Test of buildAddArrow method, of class ImmutableIntDirectedGraph.
     */
    @Test
    public void testBuildAddArrow() {
        System.out.println("buildAddArrow");
        int a = 0;
        int b = 1;
        int c = 1;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 1, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
    }

    /**
     * Test of getArrowCount method, of class ImmutableIntDirectedGraph.
     */
    @Test
    public void testGetArrowCount() {
        System.out.println("getArrowCount");
        int a = 0;
        int b = 1;
        int c = 1;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 1, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
    }

    /**
     * Test of getNext method, of class ImmutableIntDirectedGraph.
     */
    @Test
    public void testGetNext() {
        System.out.println("getNext");
        int a = 0;
        int b = 1;
        int c = 1;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 1, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
    }

    /**
     * Test of getNextCount method, of class ImmutableIntDirectedGraph.
     */
    @Test
    public void testGetNextCount() {
        System.out.println("getNextCount");
        int a = 0;
        int b = 1;
        int c = 2;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());
        assertEquals("edge count of " + a, 0, instance.getNextCount(a));
        assertEquals("edge count of " + b, 0, instance.getNextCount(b));
        assertEquals("edge count of " + c, 0, instance.getNextCount(c));

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 1, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
        assertEquals("edge count of " + b, 0, instance.getNextCount(b));
        assertEquals("edge count of " + c, 0, instance.getNextCount(c));

        builder.addArrow(b, c);
        instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 2, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
        assertEquals("edge count of " + b, 1, instance.getNextCount(b));
        assertEquals("next edge of " + b, c, instance.getNext(b, 0));
        assertEquals("edge count of " + c, 0, instance.getNextCount(c));
    }

    /**
     * Test of getVertexCount method, of class ImmutableIntDirectedGraph.
     */
    @Test
    public void testGetVertexCount() {
        System.out.println("getVertexCount");
        int a = 0;
        int b = 1;
        int c = 1;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 1, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
    }

}
