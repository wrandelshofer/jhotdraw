/*
 * @(#)NegationPseudoClassSelector.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * Implements the negation pseudo-class selector.
 * <p>
 * The negation pseudo-class, {@code :not(X)}, is a functional notation taking a
 * simple selector (excluding the negation pseudo-class itself) as an argument.
 * It represents an element that is not represented by its argument.
 * <p>
 * Negations may not be nested; {@code :not(:not(...))} is invalid.
 * Note also that since pseudo-elements are not simple selectors,
 * they are not a valid argument to {@code :not()}.
 * <p>
 * See <a href="https://www.w3.org/TR/selectors-3/#negation">negation pseudo-class</a>.
 */
public class NegationPseudoClassSelector extends FunctionPseudoClassSelector {

    private final SimpleSelector selector;

    public NegationPseudoClassSelector(String functionIdentifier, @NonNull SimpleSelector selector) {
        super(functionIdentifier);
        this.selector = selector;
    }

    @NonNull
    @Override
    public String toString() {
        return "FunctionPseudoClass:" + getFunctionIdentifier() + "(" + ")";
    }

    @Nullable
    @Override
    public <T> T match(@NonNull SelectorModel<T> model, @Nullable T element) {
        final T match = selector.match(model, element);
        return match == null ? element : null;
    }
}
