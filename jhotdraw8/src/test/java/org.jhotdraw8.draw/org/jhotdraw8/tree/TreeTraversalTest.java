/* @(#)TreeTraversalTest.java
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.tree;

import org.jhotdraw8.graph.BreadthFirstSpliterator;
import org.jhotdraw8.graph.DepthFirstSpliterator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TreeTraversalTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class TreeTraversalTest {

    private static SimpleTreeNode<String> createTree() {
        //       F
        //     ↙︎  ↘︎
        //    B     G
        //  ↙︎  ↘︎      ↘
        // A    D       I
        //    ↙︎  ↘︎     ↙
        //   C    E   H

        SimpleTreeNode<String> a = new SimpleTreeNode<String>("A");
        SimpleTreeNode<String> b = new SimpleTreeNode<String>("B");
        SimpleTreeNode<String> c = new SimpleTreeNode<String>("C");
        SimpleTreeNode<String> d = new SimpleTreeNode<String>("D");
        SimpleTreeNode<String> e = new SimpleTreeNode<String>("E");
        SimpleTreeNode<String> f = new SimpleTreeNode<String>("F");
        SimpleTreeNode<String> g = new SimpleTreeNode<String>("G");
        SimpleTreeNode<String> h = new SimpleTreeNode<String>("H");
        SimpleTreeNode<String> i = new SimpleTreeNode<String>("I");

        b.addChild(a);
        b.addChild(d);
        d.addChild(c);
        d.addChild(e);
        f.addChild(b);
        f.addChild(g);
        g.addChild(i);
        i.addChild(h);

        return f;
    }

    @Test
    public void testPreorderTraversal() throws Exception {
        SimpleTreeNode<String> root = createTree();
        PreorderSpliterator<SimpleTreeNode<String>> instance = new PreorderSpliterator<>(SimpleTreeNode::getChildren, root);

        StringBuilder buf = new StringBuilder();
        instance.forEachRemaining(node -> buf.append(node.getValue()));

        String expected = "FBADCEGIH";
        String actual = buf.toString();
        System.out.println("Preorder:");
        System.out.println("  expected: " + expected);
        System.out.println("  actual  : " + actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testPostorderTraversal() throws Exception {
        SimpleTreeNode<String> root = createTree();
        PostorderSpliterator<SimpleTreeNode<String>> instance = new PostorderSpliterator<>(SimpleTreeNode::getChildren, root);

        StringBuilder buf = new StringBuilder();
        instance.forEachRemaining(node -> buf.append(node.getValue()));

        String expected = "ACEDBHIGF";
        String actual = buf.toString();
        System.out.println("Postorder:");
        System.out.println("  expected: " + expected);
        System.out.println("  actual  : " + actual);
        assertEquals(expected, actual);
    }


    @Test
    public void testBreadthFirstTraversal() throws Exception {
        SimpleTreeNode<String> root = createTree();
        BreadthFirstSpliterator<SimpleTreeNode<String>> instance = new BreadthFirstSpliterator<>(SimpleTreeNode::getChildren, root, n -> true);

        StringBuilder buf = new StringBuilder();
        instance.forEachRemaining(node -> buf.append(node.getValue()));

        String expected = "FBGADICEH";
        String actual = buf.toString();
        System.out.println("BFS:");
        System.out.println("  expected: " + expected);
        System.out.println("  actual  : " + actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testDepthFirstTraversal() throws Exception {
        SimpleTreeNode<String> root = createTree();
        DepthFirstSpliterator<SimpleTreeNode<String>> instance = new DepthFirstSpliterator<>(SimpleTreeNode::getChildren, root, n -> true);

        StringBuilder buf = new StringBuilder();
        instance.forEachRemaining(node -> buf.append(node.getValue()));

        String expected = "FGIHBDECA";
        String actual = buf.toString();
        System.out.println("DFS:");
        System.out.println("  expected: " + expected);
        System.out.println("  actual  : " + actual);
        assertEquals(expected, actual);
    }

}
