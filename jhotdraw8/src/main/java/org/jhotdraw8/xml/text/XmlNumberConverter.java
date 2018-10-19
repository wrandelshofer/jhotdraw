/* @(#)XmlNumberConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.text.NumberConverter;

/**
 * Converts a {@code Double} into the XML String representation.
 * <p>
 * Reference:
 * <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#double">W3C: XML
 * Schema Part 2: Datatypes Second Edition: 3.2.5 double</a>
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlNumberConverter extends NumberConverter {

    private static final long serialVersionUID = 1L;

    public XmlNumberConverter() {
    }

    public XmlNumberConverter(double min, double max, double multiplier) {
        super(min, max, multiplier);
    }

    public XmlNumberConverter(double min, double max, double multiplier, boolean allowsNullValue) {
        super(min, max, multiplier, allowsNullValue);
    }

    public XmlNumberConverter(double min, double max, double multiplier, boolean allowsNullValue, String unit) {
        super(min, max, multiplier, allowsNullValue, unit);
    }

}
