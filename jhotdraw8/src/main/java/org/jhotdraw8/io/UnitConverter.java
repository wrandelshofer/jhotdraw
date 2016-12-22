/* @(#)UnitConverter.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.io;

import java.util.Objects;
import org.jhotdraw8.text.CssSize;

/**
 * UnitConverter.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface UnitConverter {
       /**
     * Gets the resolution in dots per inch.
     *
     * @return dpi
     */
    default double getDpi() {
        return 90;
    }
    default double getFactor(String unit) {
      double factor = 1.0;
      if (unit != null) {
        switch (unit) {
          case "%":
            factor = 100;
            break;
          case "px":
            factor = 1.0;
            break;
          case "cm":
            factor = 2.54 / getDpi();
            break;
          case "mm":
            factor = 25.4 / getDpi();
            break;
          case "in":
            factor = 1.0 / getDpi();
            break;
          case "pt":
            factor = 72 / getDpi();
            break;
          case "pc":
            factor = 72 * 12.0 / getDpi();
            break;
          case "em":
            factor = 1.0 / getFontSize();
            break;
          case "ex":
            factor = 1.0 / getFontXHeight();
            break;
        }
      }
      return factor;
    }


    /**
     * Gets the font size;
     *
     * @return em
     */
    default double getFontSize() {
        return 12;
    }

    /**
     * Gets the x-height of the font size.
     *
     * @return ex
     */
    default double getFontXHeight() {
        return 8;
    }

  /**
   * Converts the specified value from input unit to output unit.
   *
   * @param value a value
   * @param inputUnit the units of the value
   * @param outputUnit the desired output unit
   * @return converted value
   */
  default double convert(double value, String inputUnit, String outputUnit) {
    if (value==0.0||Objects.equals(inputUnit, outputUnit)) {
      return value;
    }
    
    return value * getFactor(outputUnit) / getFactor(inputUnit);
  }
  
  /**
   * Converts the specified value from input unit to output unit.
   *
   * @param value a value
   * @param outputUnit the desired output unit
   * @return converted value
   */
  default double convert(CssSize value, String outputUnit) {
    return convert(value.getValue(),value.getUnits(),outputUnit);
  }  
}
