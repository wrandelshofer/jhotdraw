/* @(#)BidiGraphBuilderTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;


import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BidiGraphBuilderTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BidiGraphBuilderTest {

    public BidiGraphBuilderTest() {
    }

    /**
     * Test of buildAddArrow method, of class BidiGraphBuilder.
     */
    @Test
    public void testBuildAddArrow() {
        System.out.println("buildAddArrow");
        Integer a = 0;
        Integer b = 1;
        Integer c = 1;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");

        instance.addArrow(a, b, 1.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }

    /**
     * Test of getArrowCount method, of class ImmutableIntDirectedGraph.
     */
    @Test
    public void testGetArrowCount() {
        System.out.println("getArrowCount");
        Integer a = 0;
        Integer b = 1;
        Integer c = 1;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");

        instance.addArrow(a, b, 1.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }

    /**
     * Test of getNext method, of class BidiGraphBuilder.
     */
    @Test
    public void testGetNext() {
        System.out.println("getNext");
        Integer a = 0;
        Integer b = 1;
        Integer c = 1;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"edge count");

        instance.addArrow(a, b, 1.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"edge count");
        assertEquals( 1, instance.getNextCount(a),"edge count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }

    /**
     * Test of getPrev method, of class BidiGraphBuilder.
     */
    @Test
    public void testGetPrev() {
        System.out.println("getPrev");
        Integer a = 0;
        Integer b = 1;
        Integer c = 1;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"arrow count");

        instance.addArrow(a, b, 1.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"arrow count");
        assertEquals( 1, instance.getPrevCount(b),"prev count of " + b);
        assertEquals( a, instance.getPrev(b, 0),"prev edge of " + b);
    }

    /**
     * Test of getNextCount method, of class BidiGraphBuilder.
     */
    @Test
    public void testGetNextCount() {
        System.out.println("getNextCount");
        Integer a = 0;
        Integer b = 1;
        Integer c = 2;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"arrow count");
        assertEquals( 0, instance.getNextCount(a),"next count of " + a);
        assertEquals( 0, instance.getNextCount(b),"next count of " + b);
        assertEquals( 0, instance.getNextCount(c),"next count of " + c);

        instance.addArrow(a, b, 1.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"arrow count");
        assertEquals( 1, instance.getNextCount(a),"next count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
        assertEquals( 0, instance.getNextCount(b),"next count of " + b);
        assertEquals( 0, instance.getNextCount(c),"next count of " + c);

        instance.addArrow(b, c, 2.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 2, instance.getArrowCount(),"arrow count");
        assertEquals( 1, instance.getNextCount(a),"next count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
        assertEquals( 1, instance.getNextCount(b),"next count of " + b);
        assertEquals( c, instance.getNext(b, 0),"next edge of " + b);
        assertEquals( 0, instance.getNextCount(c),"next count of " + c);
    }

    /**
     * Test of getNextCount method, of class BidiGraphBuilder.
     */
    @Test
    public void testGetPrevCount() {
        System.out.println("getPrevCount");
        Integer a = 0;
        Integer b = 1;
        Integer c = 2;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(a);
        instance.addVertex(b);
        instance.addVertex(c);

        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"arrow count");
        assertEquals( 0, instance.getPrevCount(a),"prev count of " + a);
        assertEquals( 0, instance.getPrevCount(b),"prev count of " + b);
        assertEquals( 0, instance.getPrevCount(c),"prev count of " + c);

        instance.addArrow(a, b, 1.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"arrow count");
        assertEquals( 0, instance.getPrevCount(a),"prev count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
        assertEquals( 1, instance.getPrevCount(b),"prev count of " + b);
        assertEquals( 0, instance.getPrevCount(c),"prev count of " + c);

        instance.addArrow(b, c, 2.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 2, instance.getArrowCount(),"arrow count");
        assertEquals( 0, instance.getPrevCount(a),"prev count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
        assertEquals( 1, instance.getPrevCount(b),"prev count of " + b);
        assertEquals( c, instance.getNext(b, 0),"next edge of " + b);
        assertEquals( 1, instance.getPrevCount(c),"prev count of " + c);
    }

    /**
     * Test of getVertexCount method, of class BidiGraphBuilder.
     */
    @Test
    public void testGetVertexCount() {
        System.out.println("getVertexCount");
        Integer a = 0;
        Integer b = 1;
        Integer c = 1;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(a);
        instance.addVertex(b);
        instance.addVertex(c);

        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 0, instance.getArrowCount(),"arrow count");

        instance.addArrow(a, b, 1.0);
        assertEquals( 3, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"arrow count");
        assertEquals( 1, instance.getNextCount(a),"next count of " + a);
        assertEquals( b, instance.getNext(a, 0),"next edge of " + a);
    }
    
    @Test
    public void testRemoveVertex() {
        System.out.println("removeVertex");
        String a = "a";
        String b = "b";
        String c = "c";
        String d = "d";
        String e = "e";
        BidiGraphBuilder<String, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(a);
        instance.addVertex(b);
        instance.addVertex(c);
        instance.addVertex(d);
        instance.addVertex(e);
        instance.addArrow(a,c, 1.0);
        instance.addArrow(a,e, 1.0);
        instance.addArrow(b,c, 1.0);
        instance.addArrow(c,d, 1.0);
        instance.addArrow(c,e, 1.0);

        assertEquals( 5, instance.getVertexCount(),"vertex count");
        assertEquals( 5, instance.getArrowCount(),"arrow count");

        instance.removeVertex(c);
        assertEquals( 4, instance.getVertexCount(),"vertex count");
        assertEquals( 1, instance.getArrowCount(),"arrow count");
    }

    @Test
    public void testBreadthFirstSearch() {
        System.out.println("BreadthFirstSearch");
        String a = "a";
        String b = "b";
        String c = "c";
        String d = "d";
        String e = "e";
        BidiGraphBuilder<String, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(a);
        instance.addVertex(b);
        instance.addVertex(c);
        instance.addVertex(d);
        instance.addVertex(e);
        instance.addArrow(a,c, 1.0);
        instance.addArrow(a,e, 1.0);
        instance.addArrow(b,c, 1.0);
        instance.addArrow(c,d, 1.0);
        instance.addArrow(c,e, 1.0);

        assertEquals("aced", instance.breadthFirstSearch(a).collect(Collectors.joining("")));
    }

    @Test
    public void testBreadthFirstBackwardSearch() {
        System.out.println("BreadthFirstBackwardSearch");
        String a = "a";
        String b = "b";
        String c = "c";
        String d = "d";
        String e = "e";
        BidiGraphBuilder<String, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(a);
        instance.addVertex(b);
        instance.addVertex(c);
        instance.addVertex(d);
        instance.addVertex(e);
        instance.addArrow(a,c, 1.0);
        instance.addArrow(a,e, 1.0);
        instance.addArrow(b,c, 1.0);
        instance.addArrow(c,d, 1.0);
        instance.addArrow(c,e, 1.0);

        assertEquals("eacb", instance.breadthFirstSearchBackwards(e).collect(Collectors.joining("")));
    }
}
