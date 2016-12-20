/* @(#)AdjacentSiblingCombinator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.SelectorModel;

/**
 * An "adjacent sibling combinator" matches an element if its first selector
 * matches on the adjacent sibling of the element and if its second selector
 * matches the element.
 *
 * @author Werner Randelshofer
 * @version $Id: AdjacentSiblingCombinator.java 1149 2016-11-18 11:00:10Z
 * rawcoder $
 */
public class AdjacentSiblingCombinator extends Combinator {

    public AdjacentSiblingCombinator(SimpleSelector firstSelector, Selector secondSelector) {
        super(firstSelector, secondSelector);
    }

    @Override
    public String toString() {
        return firstSelector + " + " + secondSelector;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        T result = secondSelector.match(model, element);
        if (result != null) {
            result = firstSelector.match(model, model.getPreviousSibling(result));
        }
        return result;
    }

    @Override
    public int getSpecificity() {
        return firstSelector.getSpecificity() + secondSelector.getSpecificity();
    }
}
