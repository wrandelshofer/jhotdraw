/* @(#)IntArrayDequeTest
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author werni
 */
public class IntArrayDequeTest {
    
    public IntArrayDequeTest() {
    }

    /**
     * Test of addFirst method, of class IntArrayDeque.
     */
    @Test
    public void testAddFirst() {
        System.out.println("addFirst");
        int e = 1;
        IntArrayDeque instance = new IntArrayDeque();
        instance.addFirst(e);
        assertFalse(instance.isEmpty());
        
        assertEquals(1,instance.getFirst());
        
        instance.addFirst(2);
        assertEquals(2,instance.getFirst());
        assertEquals(2,instance.size());
    }

    /**
     * Test of addLast method, of class IntArrayDeque.
     */
    @Test
    public void testAddLast() {
        System.out.println("addLast");
        int e = 1;
        IntArrayDeque instance = new IntArrayDeque();
        instance.addLast(e);
        assertFalse(instance.isEmpty());
        
        assertEquals(1,instance.getLast());
        
        instance.addLast(2);
        assertEquals(2,instance.getLast());
        assertEquals(2,instance.size());
    }

    
}
