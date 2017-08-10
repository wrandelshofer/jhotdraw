/* @(#)DefaultUnitConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

/**
 * DefaultUnitConverter.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DefaultUnitConverter implements UnitConverter {

    final static DefaultUnitConverter instance = new DefaultUnitConverter(90);

    public static DefaultUnitConverter getInstance() {
        return instance;
    }

    private final double dpi;

    public DefaultUnitConverter(double dpi) {
        this.dpi = dpi;
    }

    public DefaultUnitConverter() {
        this(72.0);
    }

    public double getDpi() {
        return dpi;
    }
}
