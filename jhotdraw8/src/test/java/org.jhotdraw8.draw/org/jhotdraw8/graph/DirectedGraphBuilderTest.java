/* @(#)DirectedGraphBuilderTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DirectedGraphBuilderTest.
 *
 * @author Werner Randelshofer
 */
public class DirectedGraphBuilderTest {

    public DirectedGraphBuilderTest() {
    }

    /**
     * Test of buildAddArrow method, of class DirectedGraphBuilder.
     */
    @Test
    public void testBuildAddArrow() {
        System.out.println("buildAddArrow");
        int a = 0;
        int b = 1;
        int c = 1;
        DirectedGraphBuilder<Integer, Double> instance = new DirectedGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "next count");

        instance.addArrow(a, b, 1.0);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "next count");
        assertEquals(1, instance.getNextCount(a), "next count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
    }

    /**
     * Test of getArrowCount method, of class DirectedGraphBuilder.
     */
    @Test
    public void testGetArrowCount() {
        System.out.println("getArrowCount");
        int a = 0;
        int b = 1;
        int c = 1;
        DirectedGraphBuilder<Integer, Double> instance = new DirectedGraphBuilder<Integer, Double>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "next count");

        instance.addArrow(a, b, 1.0);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "next count");
        assertEquals(1, instance.getNextCount(a), "next count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
    }

    /**
     * Test of getNext method, of class DirectedGraphBuilder.
     */
    @Test
    public void testGetNext() {
        System.out.println("getNext");
        int a = 0;
        int b = 1;
        int c = 1;
        DirectedGraphBuilder<Integer, Double> instance = new DirectedGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "arrow count");

        instance.addArrow(a, b, 1.0);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "arrow count");
        assertEquals(1, instance.getNextCount(a), "next count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
    }

    /**
     * Test of getNextCount method, of class DirectedGraphBuilder.
     */
    @Test
    public void testGetNextCount() {
        System.out.println("getNextCount");
        int a = 0;
        int b = 1;
        int c = 2;
        DirectedGraphBuilder<Integer, Double> instance = new DirectedGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "arrow count");
        assertEquals(0, instance.getNextCount(a), "next count of " + a);
        assertEquals(0, instance.getNextCount(b), "next count of " + b);
        assertEquals(0, instance.getNextCount(c), "next count of " + c);

        instance.addArrow(a, b, 1.0);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "arrow count");
        assertEquals(1, instance.getNextCount(a), "next count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
        assertEquals(0, instance.getNextCount(b), "next count of " + b);
        assertEquals(0, instance.getNextCount(c), "next count of " + c);

        instance.addArrow(b, c, 2.0);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(2, instance.getArrowCount(), "next count");
        assertEquals(1, instance.getNextCount(a), "next count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
        assertEquals(1, instance.getNextCount(b), "next count of " + b);
        assertEquals(c, instance.getNext(b, 0), "next edge of " + b);
        assertEquals(0, instance.getNextCount(c), "next count of " + c);
    }

    /**
     * Test of getVertexCount method, of class DirectedGraphBuilder.
     */
    @Test
    public void testGetVertexCount() {
        System.out.println("getVertexCount");
        int a = 0;
        int b = 1;
        int c = 1;
        DirectedGraphBuilder<Integer, Double> instance = new DirectedGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(0, instance.getArrowCount(), "arrow count");

        instance.addArrow(a, b, 1.0);
        assertEquals(3, instance.getVertexCount(), "vertex count");
        assertEquals(1, instance.getArrowCount(), "next count");
        assertEquals(1, instance.getNextCount(a), "next count of " + a);
        assertEquals(b, instance.getNext(a, 0), "next edge of " + a);
    }

}
