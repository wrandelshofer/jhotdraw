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
     * Test of buildAddEdge method, of class ImmutableDirectedGraphInt.
     */
    @Test
    public void testBuildAddEdge() {
        System.out.println("buildAddEdge");
        int a = 0;
        int b = 1;
        int c = 1;
     DirectedGraphBuilderInt builder=new   DirectedGraphBuilderInt();
     builder.setVertexCount(3);
        
        ImmutableDirectedGraphInt instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 0, "edge count");
        
        builder.addEdge(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
    }

    /**
     * Test of getEdgeCount method, of class ImmutableDirectedGraphInt.
     */
    @Test
    public void testGetEdgeCount() {
        System.out.println("getEdgeCount");
        int a = 0;
        int b = 1;
        int c = 1;
     DirectedGraphBuilderInt builder=new   DirectedGraphBuilderInt();
     builder.setVertexCount(3);
        
        ImmutableDirectedGraphInt instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 0, "edge count");
        
        builder.addEdge(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
    }

    /**
     * Test of getNext method, of class ImmutableDirectedGraphInt.
     */
    @Test
    public void testGetNext() {
        System.out.println("getNext");
        int a = 0;
        int b = 1;
        int c = 1;
     DirectedGraphBuilderInt builder=new   DirectedGraphBuilderInt();
     builder.setVertexCount(3);
        
        ImmutableDirectedGraphInt instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 0, "edge count");
        
        builder.addEdge(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
    }

    /**
     * Test of getNextCount method, of class ImmutableDirectedGraphInt.
     */
    @Test
    public void testGetNextCount() {
        System.out.println("getNextCount");
        int a = 0;
        int b = 1;
        int c = 2;
     DirectedGraphBuilderInt builder=new   DirectedGraphBuilderInt();
     builder.setVertexCount(3);
        
        ImmutableDirectedGraphInt instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 0, "edge count");
        assertEquals(instance.getNextCount(a),0,"edge count of "+a);
        assertEquals(instance.getNextCount(b),0,"edge count of "+b);
        assertEquals(instance.getNextCount(c),0,"edge count of "+c);
        
        builder.addEdge(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
        assertEquals(instance.getNextCount(b),0,"edge count of "+b);
        assertEquals(instance.getNextCount(c),0,"edge count of "+c);
        
        builder.addEdge(b,c);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 2, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
        assertEquals(instance.getNextCount(b),1,"edge count of "+b);
        assertEquals(instance.getNext(b,0),c,"next edge of "+b);
        assertEquals(instance.getNextCount(c),0,"edge count of "+c);
    }

    /**
     * Test of getVertexCount method, of class ImmutableDirectedGraphInt.
     */
    @Test
    public void testGetVertexCount() {
        System.out.println("getVertexCount");
        int a = 0;
        int b = 1;
        int c = 1;
     DirectedGraphBuilderInt builder=new   DirectedGraphBuilderInt();
     builder.setVertexCount(3);
        
        ImmutableDirectedGraphInt instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 0, "edge count");
        
        builder.addEdge(a,b);
         instance = builder.build();
        assertEquals(instance.getVertexCount(), 3, "vertex count");
        assertEquals(instance.getEdgeCount(), 1, "edge count");
        assertEquals(instance.getNextCount(a),1,"edge count of "+a);
        assertEquals(instance.getNext(a,0),b,"next edge of "+a);
    }

}