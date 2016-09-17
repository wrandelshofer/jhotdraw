/* @(#)MatchResult.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.css.ast;

/**
 * MatchResult.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class MatchResult<T> {
  private final T element;
  private final Selector selector;

  public MatchResult(T element, Selector selector) {
    this.element = element;
    this.selector = selector;
  }

  public T getElement() {
    return element;
  }

  public Selector getSelector() {
    return selector;
  }
  
  
}
