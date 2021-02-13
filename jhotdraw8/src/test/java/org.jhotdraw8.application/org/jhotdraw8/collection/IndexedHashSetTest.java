/* @(#)IndexedSetTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests {@link IndexedHashSet}.
 *
 * @author Werner Randelshofer
 */

public class IndexedHashSetTest  extends AbstractIndexedArraySetTest {

    @Override
    protected AbstractIndexedArraySet<Character> newInstance(Collection<Character> col) {
        return new IndexedHashSet<>(col);
    }

}