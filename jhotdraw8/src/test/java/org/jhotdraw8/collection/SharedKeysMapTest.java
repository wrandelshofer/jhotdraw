/* @(#)SharedKeysMapTest
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author werni
 */
public class SharedKeysMapTest {
    
    public SharedKeysMapTest() {
    }

  
    /**
     * Test of getOrDefault method, of class SharedKeysMap.
     */
    @Test
    public void testGetOrDefault() {
        System.out.println("getOrDefault");

        // GIVEN
        SharedKeysMap instance = new SharedKeysMap();
        
        // WHEN getOrDefault and no value has been put
        Object key = "dummyKey";
        Object defaultValue = "defaultValue";
        Object result = instance.getOrDefault(key, defaultValue);
        
         // THEN must return default value
        assertEquals(defaultValue, result);
        
        // WHEN getOrDefault and a value has been put
        Object putValue="putValue";
        instance.put(key, putValue );
         result = instance.getOrDefault(key, defaultValue);
         
         // THEN must return put value
        assertEquals(putValue, result);
        
        // WHEN key is removed
        instance.remove(key);
         result = instance.getOrDefault(key, defaultValue);
         // THEN must return default value
        assertEquals(defaultValue, result);
    }


}
