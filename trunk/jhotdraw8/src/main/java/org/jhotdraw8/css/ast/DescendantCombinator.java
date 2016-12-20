/* @(#)DescendantCombinator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.SelectorModel;

/**
 * A "descendant combinator" matches an element if its first selector matches on
 * an ancestor of the element and if its second selector matches on the element
 * itself.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DescendantCombinator extends Combinator {

    public DescendantCombinator(SimpleSelector simpleSelector, Selector selector) {
        super(simpleSelector, selector);
    }

    @Override
    public String toString() {
        return firstSelector + ".isAncestorOf(" + secondSelector + ")";
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        T result = secondSelector.match(model, element);
        T siblingElement = result == null ? null : result;
        while (siblingElement != null) {
            siblingElement = model.getParent(siblingElement);
            result = firstSelector.match(model, siblingElement);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public int getSpecificity() {
        return firstSelector.getSpecificity() + secondSelector.getSpecificity();
    }
}
