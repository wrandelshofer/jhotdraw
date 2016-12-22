/* @(#)CssSize.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.io.DefaultUnitConverter;

/**
 * CssSize.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CssSize {

  private final String units;
  private final double value;

  public CssSize(double value, String units) {
    this.value = value;
    this.units = units;
  }

  public double getDefaultConvertedValue() {
    return DefaultUnitConverter.getInstance().convert(this, null);
  }

  public String getUnits() {
    return units;
  }

  public double getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    final double v = getDefaultConvertedValue();
    hash = 17 * hash + (int) (Double.doubleToLongBits(v) ^ (Double.doubleToLongBits(this.value) >>> 32));
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
    final CssSize other = (CssSize) obj;
    double otherValue=DefaultUnitConverter.getInstance().convert(other,this.units);
    if (Math.abs(otherValue-this.value)>1e-4) {
      return false;
    }
    return true;
  }
  
  
}
