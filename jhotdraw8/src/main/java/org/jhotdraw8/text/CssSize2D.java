/* @(#)CssSize2D.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import java.util.Objects;

/**
 * CssSize2D.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CssSize2D {
private final CssSize x;
private final CssSize y;

  public CssSize2D(CssSize x, CssSize y) {
    this.x = x;
    this.y = y;
  }

  public CssSize getX() {
    return x;
  }

  public CssSize getY() {
    return y;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 11 * hash + Objects.hashCode(this.x);
    hash = 11 * hash + Objects.hashCode(this.y);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CssSize2D other = (CssSize2D) obj;
    if (!Objects.equals(this.x, other.x)) {
      return false;
    }
    if (!Objects.equals(this.y, other.y)) {
      return false;
    }
    return true;
  }
  
  
}
