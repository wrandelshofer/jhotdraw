/* @(#)IdFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.util.Objects;

/**
 * IdFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface IdFactory {

    /**
     * Clears all ids.
     */
    public void reset();

    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @return the id
     */
  default String createId(Object object) {
      return createId(object, "");
  }

    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @param prefix the desired prefix for the id
     * @return the id
     */
  public String createId(Object object, String prefix);
    /**
     * Gets an id for the specified object. Returns null if the object has no
     * id.
     *
     * @param object the object
     * @return the id
     */
    public String getId(Object object);

    /**
     * Gets the object for the specified id. Returns null if the id has no
     * object.
     *
     * @param id the id
     * @return the object
     */
    public Object getObject(String id);

    /**
     * Puts an id for the specified object. If the object already has an id, the
     * old id is replaced.
     *
     * @param object the object
     * @param id the id
     */
    public void putId(Object object, String id);

    /**
     * Gets the resolution in dots per inch.
     *
     * @return dpi
     */
    default double getDpi() {
        return 90;
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
        if (Objects.equals(inputUnit, outputUnit)) {
            return value;
        }

        return value * getFactor(outputUnit) / getFactor(inputUnit);
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
                    factor = 2.54/ getDpi();
                    break;
                case "mm":
                    factor = 25.4/getDpi();
                    break;
                case "in":
                    factor = 1.0/ getDpi();
                    break;
                case "pt":
                    factor = 72/ getDpi() ;
                    break;
                case "pc":
                    factor = 72*12.0 / getDpi();
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
}
