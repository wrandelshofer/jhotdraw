/* @(#)UniversalSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "universal selector" matches an element if the element exists.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class UniversalSelector extends SimpleSelector {

    @Override
    public String toString() {
        return "Universal:*";
    }

    @Override
    public <T> MatchResult<T> match(SelectorModel<T> model, T element) {
        return new MatchResult<>(element,this);
    }

  @Override
  public int getSpecificity() {
    return 0;
  }
}
