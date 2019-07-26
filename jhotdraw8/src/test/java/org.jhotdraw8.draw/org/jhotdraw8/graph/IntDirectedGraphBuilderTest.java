/* @(#)IntDirectedGraphBuilderTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * IntDirectedGraphBuilderTest.
 *
 * @author Werner Randelshofer
 */
public class IntDirectedGraphBuilderTest {

    public IntDirectedGraphBuilderTest() {
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
        IntDirectedGraphBuilder instance = new IntDirectedGraphBuilder();
        instance.setVertexCount(3);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "edge count");

        instance.addArrow(a, b);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "edge count");
        assertEquals(1, instance.getNextCount(a), "edge count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
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
        IntDirectedGraphBuilder instance = new IntDirectedGraphBuilder();
        instance.setVertexCount(3);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "edge count");

        instance.addArrow(a, b);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "edge count");
        assertEquals(1, instance.getNextCount(a), "edge count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
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
        IntDirectedGraphBuilder instance = new IntDirectedGraphBuilder();
        instance.setVertexCount(3);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "edge count");

        instance.addArrow(a, b);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "edge count");
        assertEquals(1, instance.getNextCount(a), "edge count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
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
        IntDirectedGraphBuilder instance = new IntDirectedGraphBuilder();
        instance.setVertexCount(3);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "edge count");
        assertEquals(0, instance.getNextCount(a), "edge count of " + a);
        assertEquals(0, instance.getNextCount(b), "edge count of " + b);
        assertEquals(0, instance.getNextCount(c), "edge count of " + c);

        instance.addArrow(a, b);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "edge count");
        assertEquals(1, instance.getNextCount(a), "edge count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
        assertEquals(0, instance.getNextCount(b), "edge count of " + b);
        assertEquals(0, instance.getNextCount(c), "edge count of " + c);

        instance.addArrow(b, c);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(2, instance.getArrowCount(), "edge count");
        assertEquals(1, instance.getNextCount(a), "edge count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
        assertEquals(1, instance.getNextCount(b), "edge count of " + b);
        assertEquals(c, instance.getNext(b, 0), "next edge of " + b);
        assertEquals(0, instance.getNextCount(c), "edge count of " + c);
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
        IntDirectedGraphBuilder instance = new IntDirectedGraphBuilder();
        instance.setVertexCount(3);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "edge count");

        instance.addArrow(a, b);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "edge count");
        assertEquals(1, instance.getNextCount(a), "edge count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
    }

}
