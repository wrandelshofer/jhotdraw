/* @(#)UniversalSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "select nothing selector" matches nothing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectNothingSelector extends SimpleSelector {

    @Override
    public String toString() {
        return "SelectNothing";
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return null;
    }

  @Override
  public int getSpecificity() {
    return 0;
  }
}
