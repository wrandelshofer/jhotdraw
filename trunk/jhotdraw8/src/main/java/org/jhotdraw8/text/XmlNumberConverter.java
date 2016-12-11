/* @(#)XmlNumberConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import org.jhotdraw8.draw.io.IdFactory;

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
