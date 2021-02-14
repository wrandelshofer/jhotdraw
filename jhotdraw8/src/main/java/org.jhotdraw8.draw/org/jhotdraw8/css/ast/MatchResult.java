/*
 * @(#)MatchResult.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;

/**
 * Describes the result of a successful match from a
 * {@link Selector} to an element.
 *
 * @param <E> the element type
 */
public class MatchResult<E> {
    private final @NonNull Selector selector;
    private final @NonNull E element;

    public MatchResult(@NonNull Selector selector, @NonNull E element) {
        this.selector = selector;
        this.element = element;
    }

    public @NonNull Selector getSelector() {
        return selector;
    }

    public @NonNull E getElement() {
        return element;
    }
}
