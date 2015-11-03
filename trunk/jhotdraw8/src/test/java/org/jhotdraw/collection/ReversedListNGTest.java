/* @(#)ReversedListNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author werni
 */
public class ReversedListNGTest {
    
    @Test(dataProvider = "addData")
    public void testAdd(String initialList, int index, Character value, String expectedList, String expectedChanges) throws Exception {
        ReversedList<Character> list = new ReversedList<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.add(index, value);

        /*
        System.out.println("initial :"+asList(initialList));
        System.out.println("actual  :"+list);
        System.out.println("expected:"+ asList(expectedList));
        */
        assertEquals(list, asList(expectedList));
        assertEquals(list.getSource(),asReversedList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @DataProvider
    public Object[][] addData() {
        return new Object[][]{
            {"", 0, 'a', "a", "add(0,[a])"},
            {"a", 0, 'b', "ba", "add(0,[b])"},
            {"a", 1, 'b', "ab", "add(1,[b])"},
            {"ab", 0, 'c', "cba", "add(0,[c])"},
            {"ab", 1, 'c', "bca", "add(1,[c])"},
            {"ab", 2, 'c', "bac", "add(2,[c])"},
        };
    }
    @Test(dataProvider = "setData")
    public void testSet(String initialList, int index, Character value, String expectedList, String expectedChanges) throws Exception {
        ReversedList<Character> list = new ReversedList<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.set(index, value);

        /*
        System.out.println("initial :"+asList(initialList));
        System.out.println("actual  :"+list);
        System.out.println("expected:"+ asList(expectedList));
                */
        assertEquals(list, asList(expectedList));
        assertEquals(list.getSource(),asReversedList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @DataProvider
    public Object[][] setData() {
        return new Object[][]{
            {"a", 0, 'b', "b", "rep([a]->[b])"},
            {"ab", 0, 'c', "ca", "rep([b]->[c])"},
            {"ab", 1, 'c', "bc", "rep([a]->[c])"},
        };
    }
    @Test(dataProvider = "removeData")
    public void testRemove(String initialList, int index, Character value, String expectedList, String expectedChanges) throws Exception {
        ReversedList<Character> list = new ReversedList<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        if (index==-1) {
        list.remove(value);
        }else{
        list.remove(index);
        }

        System.out.println("initial :"+asList(initialList));
        System.out.println("actual  :"+list);
        System.out.println("expected:"+ asList(expectedList));
        assertEquals(list, asList(expectedList));
        assertEquals(list.getSource(),asReversedList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @DataProvider
    public Object[][] removeData() {
        return new Object[][]{
            {"a", 0, 'x', "", "rem(0,[a])"},
            {"a", -1, 'a', "", "rem(0,[a])"},
            {"ab", 0, 'x', "a", "rem(0,[b])"},
            {"ab", -1, 'a', "b", "rem(1,[a])"},
            {"abc", 0, 'x', "ba", "rem(0,[c])"},
            {"abc", 1, 'x', "ca", "rem(1,[b])"},
            {"abc", 2, 'x', "cb", "rem(2,[a])"},
            {"abc", -1, 'a', "cb", "rem(2,[a])"},
            {"abc", -1, 'b', "ca", "rem(1,[b])"},
            {"abc", -1, 'c', "ba", "rem(0,[c])"},
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

    private static ObservableList<Character> asList(String str) {
        ArrayList<Character> l = new ArrayList<>();
        for (char ch : str.toCharArray()) {
            l.add(ch);
        }
        return FXCollections.observableList(l);
    }
    private static ObservableList<Character> asReversedList(String str) {
        LinkedList<Character> l = new LinkedList<>();
        for (char ch : str.toCharArray()) {
            l.addFirst(ch);
        }
        return FXCollections.observableList(l);
    }
}
