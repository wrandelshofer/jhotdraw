/*
 * @(#)ListEnumeratorTest.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListEnumeratorSpliteratorTest {
    @Test
    public void testEnumeratorAsSpliterator() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        List<Integer> actual = StreamSupport.stream(new ListEnumeratorSpliterator<>(list), false).collect(Collectors.toList());
        assertEquals(list, actual);
    }

    @Test
    public void testEnumerator() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        List<Integer> actual = new ArrayList<>();
        for (ListEnumeratorSpliterator<Integer> i = new ListEnumeratorSpliterator<>(list); i.moveNext(); ) {
            actual.add(i.current());
        }
        assertEquals(list, actual);
    }

    @Test
    public void testSplit() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        List<Integer> expected = iterativelySplitList(list.spliterator());
        List<Integer> actual = iterativelySplitList(new ListEnumeratorSpliterator<>(list));
        assertEquals(expected, actual);
    }

    private @NonNull List<Integer> iterativelySplitList(@NonNull Spliterator<Integer> i) {
        List<Spliterator<Integer>> splits = new ArrayList<>();
        for (Spliterator<Integer> split = i.trySplit(); split != null; split = i.trySplit()) {
            splits.add(split);
        }
        List<Integer> actual = new ArrayList<>();
        for (Spliterator<Integer> split : splits) {
            split.forEachRemaining(actual::add);
            actual.add(-1); // we use -1 to mark where we did a split
        }
        actual.add(-1); // we use -1 to mark where we did a split
        i.forEachRemaining(actual::add);
        return actual;
    }
}