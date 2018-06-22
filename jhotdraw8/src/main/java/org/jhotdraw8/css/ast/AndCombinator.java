/* @(#)AndCombinator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.css.SelectorModel;

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

    @NonNull
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
}
