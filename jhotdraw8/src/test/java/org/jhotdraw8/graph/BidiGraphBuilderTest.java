/* @(#)BidiGraphBuilderTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

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

        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());

        instance.addArrow(a, b, 1.0);
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
        Integer a = 0;
        Integer b = 1;
        Integer c = 1;
        BidiGraphBuilder<Integer, Double> instance = new BidiGraphBuilder<>();
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());

        instance.addArrow(a, b, 1.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 1, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
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

        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 0, instance.getArrowCount());

        instance.addArrow(a, b, 1.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("edge count", 1, instance.getArrowCount());
        assertEquals("edge count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
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

        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 0, instance.getArrowCount());

        instance.addArrow(a, b, 1.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 1, instance.getArrowCount());
        assertEquals("prev count of " + b, 1, instance.getPrevCount(b));
        assertEquals("prev edge of " + b, a, instance.getPrev(b, 0));
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

        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 0, instance.getArrowCount());
        assertEquals("next count of " + a, 0, instance.getNextCount(a));
        assertEquals("next count of " + b, 0, instance.getNextCount(b));
        assertEquals("next count of " + c, 0, instance.getNextCount(c));

        instance.addArrow(a, b, 1.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 1, instance.getArrowCount());
        assertEquals("next count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
        assertEquals("next count of " + b, 0, instance.getNextCount(b));
        assertEquals("next count of " + c, 0, instance.getNextCount(c));

        instance.addArrow(b, c, 2.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 2, instance.getArrowCount());
        assertEquals("next count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
        assertEquals("next count of " + b, 1, instance.getNextCount(b));
        assertEquals("next edge of " + b, c, instance.getNext(b, 0));
        assertEquals("next count of " + c, 0, instance.getNextCount(c));
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
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 0, instance.getArrowCount());
        assertEquals("prev count of " + a, 0, instance.getPrevCount(a));
        assertEquals("prev count of " + b, 0, instance.getPrevCount(b));
        assertEquals("prev count of " + c, 0, instance.getPrevCount(c));

        instance.addArrow(a, b, 1.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 1, instance.getArrowCount());
        assertEquals("prev count of " + a, 0, instance.getPrevCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
        assertEquals("prev count of " + b, 1, instance.getPrevCount(b));
        assertEquals("prev count of " + c, 0, instance.getPrevCount(c));

        instance.addArrow(b, c, 2.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 2, instance.getArrowCount());
        assertEquals("prev count of " + a, 0, instance.getPrevCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
        assertEquals("prev count of " + b, 1, instance.getPrevCount(b));
        assertEquals("next edge of " + b, c, instance.getNext(b, 0));
        assertEquals("prev count of " + c, 1, instance.getPrevCount(c));
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
        instance.addVertex(0);
        instance.addVertex(1);
        instance.addVertex(2);

        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 0, instance.getArrowCount());

        instance.addArrow(a, b, 1.0);
        assertEquals("vertex count", 3, instance.getVertexCount());
        assertEquals("arrow count", 1, instance.getArrowCount());
        assertEquals("next count of " + a, 1, instance.getNextCount(a));
        assertEquals("next edge of " + a, b, instance.getNext(a, 0));
    }

}
