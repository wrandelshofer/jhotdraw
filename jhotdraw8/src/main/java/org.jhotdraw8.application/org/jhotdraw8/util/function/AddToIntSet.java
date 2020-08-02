/*
 * @(#)AddToSet.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util.function;

/**
 * Represents a function that adds an element to a set if not already present.
 */
@FunctionalInterface
public interface AddToIntSet {
    /**
     * Adds the specified element to the set if it is not already present.
     *
     * @param e element to be added to the set
     * @return {@code true} if this set did not already contain the specified
     * element
     */
    boolean add(int e);
}
