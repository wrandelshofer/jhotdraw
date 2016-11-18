/* @(#)PseudoClassSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css.ast;

/**
 * A "pseudo class selector" matches an element based on criteria which are
 * not directly encoded in the element.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class PseudoClassSelector extends SimpleSelector {
  @Override
  public final int getSpecificity() {
    return 10;
  }


}
