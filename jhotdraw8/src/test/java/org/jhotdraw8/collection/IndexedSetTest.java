/* @(#)IndexedSetTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 *
 * @author werni
 */
public class IndexedSetTest {

    public IndexedSetTest() {
    }

    public static void testAdd(String initialList, int index, Character value, String expectedListStr, String expectedChanges) throws Exception {
        IndexedSet<Character> list = new IndexedSet<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.add(index, value);

        List<Character> expectedList = asList(expectedListStr);
        assertEquals(expectedList,list);
        assertEquals( expectedChanges,recorder.getChanges());
        assertTrue(list.containsAll(expectedList));

        List<Character> invertedList=asList("abcd");
        invertedList.removeAll(expectedList);
        for (Character c:invertedList) {
            assertFalse(list.contains(invertedList));
        }
    }

    @TestFactory
    public  List<DynamicTest> addDataFactory() {
        return Arrays.asList(
                dynamicTest("0", () ->testAdd("", 0, 'a', "a", "add(0,[a])")),
            dynamicTest("1", () ->testAdd("a", 0, 'a', "a", "")),
            dynamicTest("2", () ->testAdd("a", 1, 'a', "a", "")),
            dynamicTest("3", () ->testAdd("a", 0, 'b', "ba", "add(0,[b])")),
            dynamicTest("4", () ->testAdd("a", 1, 'b', "ab", "add(1,[b])")),
            dynamicTest("5", () ->testAdd("ab", 0, 'a', "ab", "")),
            dynamicTest("6", () ->testAdd("ab", 1, 'a', "ab", "")),
            dynamicTest("7", () ->testAdd("ab", 2, 'a', "ba", "rem(0,[a])add(1,[a])")),
            dynamicTest("8", () ->testAdd("ab", 0, 'b', "ba", "add(0,[b])rem(2,[b])")),
            dynamicTest("9", () ->testAdd("ab", 1, 'b', "ab", "")),
            dynamicTest("10", () ->testAdd("ab", 2, 'b', "ab", "")),
            dynamicTest("11", () ->testAdd("abc", 0, 'a', "abc", "")),
            dynamicTest("12", () ->testAdd("abc", 1, 'a', "abc", "")),
            dynamicTest("13", () ->testAdd("abc", 2, 'a', "bac", "rem(0,[a])add(1,[a])")),
            dynamicTest("14", () ->testAdd("abc", 3, 'a', "bca", "rem(0,[a])add(2,[a])")),
            dynamicTest("15", () ->testAdd("abc", 0, 'b', "bac", "add(0,[b])rem(2,[b])")),
            dynamicTest("16", () ->testAdd("abc", 1, 'b', "abc", "")),
            dynamicTest("17", () ->testAdd("abc", 2, 'b', "abc", "")),
            dynamicTest("18", () ->testAdd("abc", 3, 'b', "acb", "rem(1,[b])add(2,[b])")),
            dynamicTest("19", () ->testAdd("abc", 0, 'c', "cab", "add(0,[c])rem(3,[c])")),
            dynamicTest("20", () ->testAdd("abc", 1, 'c', "acb", "add(1,[c])rem(3,[c])")),
            dynamicTest("21", () ->testAdd("abc", 2, 'c', "abc", "")),
            dynamicTest("22", () ->testAdd("abc", 3, 'c', "abc", ""))//
        );

    }

    public static void testSet(String initialList, int index, Character value, String expectedListStr, String expectedChanges) throws Exception {
        IndexedSet<Character> list = new IndexedSet<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.set(index, value);

        List<Character> expectedList = asList(expectedListStr);
        assertEquals(expectedList,list);
        assertEquals(recorder.getChanges(), expectedChanges);
        assertTrue(list.containsAll(expectedList));

        List<Character> invertedList=asList("abcd");
        invertedList.removeAll(expectedList);
        for (Character c:invertedList) {
            assertFalse(list.contains(invertedList));
        }
    }

    @TestFactory
    public  List<DynamicTest> setDataFactory() {
        return Arrays.asList(
                dynamicTest("1", () ->testSet("a", 0, 'a', "a", "")),
            dynamicTest("1", () ->testSet("a", 0, 'b', "b", "rep([a]->[b])")),
            dynamicTest("2", () ->testSet("ab", 0, 'a', "ab", "")),
            dynamicTest("3", () ->testSet("ab", 1, 'a', "a", "rem(1,[b])")),
            dynamicTest("4", () ->testSet("ab", 0, 'b', "b", "rem(0,[a])")),
            dynamicTest("5", () ->testSet("ab", 1, 'b', "ab", "")),
            dynamicTest("6", () ->testSet("abc", 0, 'a', "abc", "")),
            dynamicTest("7", () ->testSet("abc", 1, 'a', "ac", "rem(1,[b])")),
            dynamicTest("8", () ->testSet("abc", 2, 'a', "ba", "perm(0->1,1->0)rem(2,[c])")),
            dynamicTest("9", () ->testSet("abc", 0, 'b', "bc", "rem(0,[a])")),
            dynamicTest("10", () ->testSet("abc", 1, 'b', "abc", "")),
            dynamicTest("11", () ->testSet("abc", 2, 'b', "ab", "rem(2,[c])")),
            dynamicTest("12", () ->testSet("abc", 0, 'c', "cb", "perm(1->2,2->1)rem(1,[a])")),
            dynamicTest("13", () ->testSet("abc", 1, 'c', "ac", "rem(1,[b])")),
            dynamicTest("14", () ->testSet("abc", 2, 'c', "abc", "")) //
        //
        );

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
