/* @(#)ChildCombinator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "child combinator" matches an element if its first selector matches on the
 * parent of the element and if its second selector matches on the element
 * itself.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ChildCombinator extends Combinator {

    public ChildCombinator(SimpleSelector simpleSelector, Selector selector) {
        super(simpleSelector, selector);
    }

    @Override
    public String toString() {
        return "(" + firstSelector + " > " + secondSelector + ")";
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        T matchingElement = secondSelector.match(model, element);
        if (matchingElement != null) {
            return firstSelector.match(model, model.getParent(matchingElement));
        }
        return null;
    }
}
