/* @(#)IndexedSetNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.collection.IndexedSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.collections.ListChangeListener;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author werni
 */
public class IndexedSetNGTest {

    public IndexedSetNGTest() {
    }

    @Test(dataProvider = "addData")
    public void testAdd(String initialList, int index, Character value, String expectedList, String expectedChanges) throws Exception {
        IndexedSet<Character> list = new IndexedSet<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.add(index, value);

        assertEquals(list, asList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @DataProvider
    public Object[][] addData() {
        return new Object[][]{
            {"", 0, 'a', "a", "add(0,[a])"},
            {"a", 0, 'a', "a", ""},
            {"a", 1, 'a', "a", ""},
            {"a", 0, 'b', "ba", "add(0,[b])"},
            {"a", 1, 'b', "ab", "add(1,[b])"},
            {"ab", 0, 'a', "ab", ""},
            {"ab", 1, 'a', "ab", ""},
            {"ab", 2, 'a', "ba", "perm(0->1,1->0)"},
            {"ab", 0, 'b', "ba", "perm(0->1,1->0)"},
            {"ab", 1, 'b', "ab", ""},
            {"ab", 2, 'b', "ab", ""},
            {"abc", 0, 'a', "abc", ""},
            {"abc", 1, 'a', "abc", ""},
            {"abc", 2, 'a', "bac", "perm(0->1,1->0)"},
            {"abc", 3, 'a', "bca", "perm(0->2,2->0)"},
            {"abc", 0, 'b', "bac", "perm(0->1,1->0)"},
            {"abc", 1, 'b', "abc", ""},
            {"abc", 2, 'b', "abc", ""},
            {"abc", 3, 'b', "acb", "perm(1->2,2->1)"},
            {"abc", 0, 'c', "cab", "perm(0->2,2->0)"},
            {"abc", 1, 'c', "acb", "perm(1->2,2->1)"},
            {"abc", 2, 'c', "abc", ""},
            {"abc", 3, 'c', "abc", ""},//
        };

    }

    @Test(dataProvider = "setData")
    public void testSet(String initialList, int index, Character value, String expectedList, String expectedChanges) throws Exception {
        IndexedSet<Character> list = new IndexedSet<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.set(index, value);

        assertEquals(list, asList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @DataProvider
    public Object[][] setData() {
        return new Object[][]{
            {"a", 0, 'a', "a", ""},
            {"a", 0, 'b', "b", "rep([a]->[b])"},
            {"ab", 0, 'a', "ab", ""},
            {"ab", 1, 'a', "a", "ren(1,[b])"},
            {"ab", 0, 'b', "b", "ren(0,[a])"},
            {"ab", 1, 'b', "ab", ""},
            {"abc", 0, 'a', "abc", ""},
            {"abc", 1, 'a', "ac", "ren(1,[b])"},
            {"abc", 2, 'a', "ba", "perm(0->1,1->0)ren(2,[c])"},
            {"abc", 0, 'b', "bc", "ren(0,[a])"},
            {"abc", 1, 'b', "abc", ""},
            {"abc", 2, 'b', "ab", "ren(2,[c])"},
            {"abc", 0, 'c', "cb", "perm(1->2,2->1)ren(1,[a])"},
            {"abc", 1, 'c', "ac", "ren(1,[b])"},
            {"abc", 2, 'c', "abc", ""}, //
        //
        };

    }

    private static class ChangeRecorder implements ListChangeListener<Character> {

        private StringBuilder buf = new StringBuilder();

        @Override
        public void onChanged(ListChangeListener.Change<? extends Character> c) {
            while (c.next()) {
                if (c.wasPermutated()) {
                    buf.append("perm(");
                    boolean first = true;
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        if (i != c.getPermutation(i)) {
                            if (first) {
                                first = false;
                            } else {
                                buf.append(',');
                            }
                            buf.append(i).append("->").append(c.getPermutation(i));
                        }
                    }
                    buf.append(")");
                } else if (c.wasReplaced()) {
                    buf.append("rep(").append(c.getRemoved()).append("->").append(c.getAddedSubList()).append(")");
                } else {
                    if (c.wasAdded()) {
                        buf.append("add(").append(c.getFrom()).append(',').append(c.getAddedSubList()).append(')');
                    }
                    if (c.wasRemoved()) {
                        buf.append("rem(").append(c.getFrom()).append(',').append(c.getRemoved()).append(')');
                    }
                }
            }
        }

        private String getChanges() {
            return buf.toString();
        }
    }

    private static List<Character> asList(String str) {
        ArrayList<Character> l = new ArrayList<>();
        for (char ch : str.toCharArray()) {
            l.add(ch);
        }
        return l;
    }

}
