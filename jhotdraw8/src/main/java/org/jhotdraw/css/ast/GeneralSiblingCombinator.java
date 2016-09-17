/* @(#)GeneralSiblingCombinator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * An "generarl sibling combinator" matches an element if its first selector
 * matches on a previous sibling of the element and if its second selector
 * matches the element.
 *
 * @author Werner Randelshofer
 * @version $Id: GeneralSiblingCombinator.java 1120 2016-01-15 17:37:49Z
 * rawcoder $
 */
public class GeneralSiblingCombinator extends Combinator {

  public GeneralSiblingCombinator(SimpleSelector simpleSelector, Selector selector) {
    super(simpleSelector, selector);
  }

  @Override
  public String toString() {
    return firstSelector + " ~ " + secondSelector;
  }

  @Override
  public <T> MatchResult<T> match(SelectorModel<T> model, T element) {
   MatchResult< T> matchingElement = secondSelector.match(model, element);
    T siblingElement = matchingElement==null?null:matchingElement.getElement();
    while (siblingElement != null) {
      siblingElement = model.getPreviousSibling(siblingElement);
      matchingElement = firstSelector.match(model, siblingElement);
      if (matchingElement != null) {
        break;
      }
    }
    return matchingElement == null ? null : new MatchResult<>(matchingElement.getElement(),this);
  }

  @Override
  public final int getSpecificity() {
    return firstSelector.getSpecificity() + secondSelector.getSpecificity();
  }
}
