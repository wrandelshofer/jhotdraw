/*
 * @(#)AndCombinator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.SelectorModel;

import java.util.function.Consumer;

/**
 * An "and combinator" matches an element if both its first selector and its
 * second selector match the element.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AndCombinator extends Combinator {

    public AndCombinator(SimpleSelector simpleSelector, Selector selector) {
        super(simpleSelector, selector);
    }

    @Nonnull
    @Override
    public String toString() {
        return "(" + firstSelector + " && " + secondSelector + ")";
    }

    @Nullable
    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        T firstResult = firstSelector.match(model, element);
        return (firstResult != null && secondSelector.match(model, element) != null) ? firstResult : null;
    }

    @Override
    public int getSpecificity() {
        return firstSelector.getSpecificity() + secondSelector.getSpecificity();
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        firstSelector.produceTokens(consumer);
        secondSelector.produceTokens(consumer);
    }
}
