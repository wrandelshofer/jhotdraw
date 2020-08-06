package org.jhotdraw8.collection;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImmutableArrayListTest {

    @Test
    void testSubList() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        ImmutableArrayList<Integer> instance = new ImmutableArrayList<>(list);

        // cut off: 1
        assertEquals(list.subList(1, 4), instance.readOnlySubList(1, 4).asList());

        // cut off: 4
        assertEquals(list.subList(0, 3), instance.readOnlySubList(0, 3).asList());

        // cut out: 2
        assertEquals(list.subList(1, 2), instance.readOnlySubList(1, 2).asList());

        // empty list at: 3
        assertEquals(list.subList(2, 2), instance.readOnlySubList(2, 2).asList());
    }
}