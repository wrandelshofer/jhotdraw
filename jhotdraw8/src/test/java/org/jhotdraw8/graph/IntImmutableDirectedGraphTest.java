/* @(#)IntImmutableDirectedGraphTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * IntImmutableDirectedGraphTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntImmutableDirectedGraphTest {

    public IntImmutableDirectedGraphTest() {
    }

    /**
     * Test of buildAddArrow method, of class IntImmutableDirectedGraph.
     */
    @Test
    public void testBuildAddArrow() {
        System.out.println("buildAddArrow");
        int a = 0;
        int b = 1;
        int c = 2;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        IntImmutableDirectedGraph instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }

    /**
     * Test of getArrowCount method, of class IntImmutableDirectedGraph.
     */
    @Test
    public void testGetArrowCount() {
        System.out.println("getArrowCount");
        int a = 0;
        int b = 1;
        int c = 2;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        IntImmutableDirectedGraph instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }

    /**
     * Test of getNext method, of class IntImmutableDirectedGraph.
     */
    @Test
    public void testGetNext() {
        System.out.println("getNext");
        int a = 0;
        int b = 1;
        int c = 2;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        IntImmutableDirectedGraph instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }

    /**
     * Test of getNextCount method, of class IntImmutableDirectedGraph.
     */
    @Test
    public void testGetNextCount() {
        System.out.println("getNextCount");
        int a = 0;
        int b = 1;
        int c = 2;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        IntImmutableDirectedGraph instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");
        assertEquals( 0, instance.getNextCount(a),"edge count of " + a);
        assertEquals( 0, instance.getNextCount(b),"edge count of " + b);
        assertEquals( 0, instance.getNextCount(c),"edge count of " + c);

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
        assertEquals( 0, instance.getNextCount(b),"edge count of " + b);
        assertEquals( 0, instance.getNextCount(c),"edge count of " + c);

        builder.addArrow(b, c);
        instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 2, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
        assertEquals( 1, instance.getNextCount(b),"edge count of " + b);
        assertEquals( c, instance.getNext(b, 0),"next edge of " + b);
        assertEquals( 0, instance.getNextCount(c),"edge count of " + c);
    }

    /**
     * Test of getVertexCount method, of class IntImmutableDirectedGraph.
     */
    @Test
    public void testGetVertexCount() {
        System.out.println("getVertexCount");
        int a = 0;
        int b = 1;
        int c = 1;
        IntDirectedGraphBuilder builder = new IntDirectedGraphBuilder();
        builder.setVertexCount(3);

        IntImmutableDirectedGraph instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");

        builder.addArrow(a, b);
        instance = builder.build();
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }

}
