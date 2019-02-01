/* @(#)SharedKeysMapTest
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import org.junit.jupiter.api.Test;

import javax.xml.validation.SchemaFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        SharedKeysMap<Object,Object> instance = new SharedKeysMap<>();
        
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


    @Test
    public void testConcurrentModification() {
        // GIVEN
        SharedKeysMap<String,Integer> instance = new SharedKeysMap<>();
        instance.put("one",1);
        instance.put("two",2);

        // WHEN create iterator
        Iterator<Map.Entry<String,Integer>> iter = instance.entrySet().iterator();

        // WHEN iterate a bit
        System.out.println(iter.next());

        // WHEN perform concurrent modification
        instance.put("three",3);

        // THEN iterator must not throw ConcurrentModificationException
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    @Test
    public void testSharing() {
        // GIVEN
        HashMap<String,Integer> keyMap = new HashMap<>();
        SharedKeysMap<String,Integer> instance1 = new SharedKeysMap<>(keyMap);
        SharedKeysMap<String,Integer> instance2 = new SharedKeysMap<>(keyMap);

        // WHEN
        instance1.put("one",1);
        instance1.put("two",2);

        // THEN instance 1 must contain two entries
        assertEquals(2, instance1.keySet().size());
       assertEquals( ImmutableSet.of("one","two").asSet(),instance1.keySet());

        // THEN instance 2 must contain zero entries
        assertEquals(0, instance2.keySet().size());
        assertEquals( ImmutableSet.of().asSet(),instance2.keySet());

        // WHEN
        instance2.put("two",22);
        instance2.put("three",33);

        // THEN instance 1 must contain two entries
        assertEquals(2, instance1.keySet().size());
        assertEquals( ImmutableSet.of("one","two").asSet(),instance1.keySet());

        // THEN instance 2 must contain two entries
        assertEquals(2, instance2.keySet().size());
        assertEquals( ImmutableSet.of("two","three").asSet(),instance2.keySet());

    }

    @Test
    public void testMapChangeListener() {
        // GIVEN
        HashMap<String,Integer> keyMap = new HashMap<>();
        SharedKeysMap<String,Integer> instance = new SharedKeysMap<>(keyMap);

        List<String> changes = new ArrayList<>();
        MapChangeListener<String,Integer> listener= change -> {
            changes.add(change.getKey()
                    +(change.wasAdded()?",added:"+change.getValueAdded():"")
                    +(change.wasRemoved()?",removed:"+change.getValueRemoved():""));

        };
        instance.addListener(listener);

        // WHEN
        instance.put("one",1);
        instance.put("two",2);
        instance.put("three",3);
        instance.remove("two");
        instance.put("three",33);

        assertEquals(Arrays.asList(
                "one,added:1",
                "two,added:2",
                "three,added:3",
                "two,removed:2",
                "three,added:33,removed:3"),
                changes);
    }

    @Test
    public void testInvalidationListener() {
        // GIVEN
        HashMap<String,Integer> keyMap = new HashMap<>();
        SharedKeysMap<String,Integer> instance = new SharedKeysMap<>(keyMap);

        int[] count={0};
        InvalidationListener listener= o -> {
            if (o==instance)count[0]++;
        };
        instance.addListener(listener);

        // WHEN
        instance.put("one",1);
        instance.put("two",2);
        instance.put("three",3);
        instance.remove("two");
        instance.put("three",33);

        assertEquals(5,count[0]);
    }
}
