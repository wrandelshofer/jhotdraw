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
 * @version $Id$
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
    public <T> MatchResult<T> match(SelectorModel<T> model, T element) {
        MatchResult<T> result = secondSelector.match(model, element);
        if (result != null) {
            result= firstSelector.match(model, model.getParent(result.getElement()));
        }
        return result ==null?null:new MatchResult<>(result.getElement(),this);
    }
    
  @Override
  public int getSpecificity() {
    return firstSelector.getSpecificity()+secondSelector.getSpecificity();
  }
    
}
