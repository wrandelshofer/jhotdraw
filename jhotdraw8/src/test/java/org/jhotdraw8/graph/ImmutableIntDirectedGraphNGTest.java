/* @(#)ImmutableIntDirectedGraphNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * ImmutableIntDirectedGraphNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ImmutableIntDirectedGraphNGTest {

    public ImmutableIntDirectedGraphNGTest() {
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
     IntDirectedGraphBuilder builder=new   IntDirectedGraphBuilder();
     builder.setVertexCount(3);
        
        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 0, "edge count");
        
        builder.addArrow(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
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
     IntDirectedGraphBuilder builder=new   IntDirectedGraphBuilder();
     builder.setVertexCount(3);
        
        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 0, "edge count");
        
        builder.addArrow(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
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
     IntDirectedGraphBuilder builder=new   IntDirectedGraphBuilder();
     builder.setVertexCount(3);
        
        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 0, "edge count");
        
        builder.addArrow(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
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
     IntDirectedGraphBuilder builder=new   IntDirectedGraphBuilder();
     builder.setVertexCount(3);
        
        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 0, "edge count");
        assertEquals(instance.getNextCount(a),0,"edge count of "+a);
        assertEquals(instance.getNextCount(b),0,"edge count of "+b);
        assertEquals(instance.getNextCount(c),0,"edge count of "+c);
        
        builder.addArrow(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
        assertEquals(instance.getNextCount(b),0,"edge count of "+b);
        assertEquals(instance.getNextCount(c),0,"edge count of "+c);
        
        builder.addArrow(b,c);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 2, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
        assertEquals(instance.getNextCount(b),1,"edge count of "+b);
        assertEquals(instance.getNext(b,0),c,"next edge of "+b);
        assertEquals(instance.getNextCount(c),0,"edge count of "+c);
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
     IntDirectedGraphBuilder builder=new   IntDirectedGraphBuilder();
     builder.setVertexCount(3);
        
        ImmutableIntDirectedGraph instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 0, "edge count");
        
        builder.addArrow(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getArrowCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
    }

}