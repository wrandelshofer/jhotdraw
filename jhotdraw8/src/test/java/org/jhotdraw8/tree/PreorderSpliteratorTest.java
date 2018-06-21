/* @(#)PreorderSpliteratorTest.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.tree;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
/**
 * PreorderSpliteratorTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PreorderSpliteratorTest {

    private static SimpleTreeNode<String> createTree() {
        //       F
        //     ↙︎  ↘︎
        //    B    G
        //  ↙︎  ↘︎    ↘
        // A    D     I
        //    ↙︎  ↘︎    ↘
        //   C    E     H

        SimpleTreeNode<String> a=new SimpleTreeNode<String>("A");
        SimpleTreeNode<String> b=new SimpleTreeNode<String>("B");
        SimpleTreeNode<String> c=new SimpleTreeNode<String>("C");
        SimpleTreeNode<String> d=new SimpleTreeNode<String>("D");
        SimpleTreeNode<String> e=new SimpleTreeNode<String>("E");
        SimpleTreeNode<String> f=new SimpleTreeNode<String>("F");
        SimpleTreeNode<String> g=new SimpleTreeNode<String>("G");
        SimpleTreeNode<String> h=new SimpleTreeNode<String>("H");
        SimpleTreeNode<String> i=new SimpleTreeNode<String>("I");

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
    public void test() throws Exception {
        SimpleTreeNode<String> root=createTree();
        PreorderSpliterator<SimpleTreeNode<String>> instance = new PreorderSpliterator<>(root, SimpleTreeNode::getChildren);

        StringBuilder buf=new StringBuilder();
        instance.forEachRemaining(node->buf.append(node.getValue()));

        String expected="FBADCEGIH";
        String actual=buf.toString();

        assertEquals(expected,actual);
    }
}
