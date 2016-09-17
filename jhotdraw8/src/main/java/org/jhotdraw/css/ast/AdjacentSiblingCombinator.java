/* @(#)AdjacentSiblingCombinator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * An "adjacent sibling combinator" matches an element if its first selector
 * matches on the adjacent sibling of the element and if its second selector
 * matches the element.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
    public <T> MatchResult<T> match(SelectorModel<T> model, T element) {
        MatchResult<T> result = secondSelector.match(model, element);
        if (result != null) {
            result = firstSelector.match(model, model.getPreviousSibling(result.getElement()));
        }
        return result == null ? null : new MatchResult<T>(result.getElement(), this);
    }

  @Override
  public int getSpecificity() {
    return firstSelector.getSpecificity()+secondSelector.getSpecificity();
  }
}
