/* @(#)XmlDoubleConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

/**
 * XmlDoubleConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlDoubleConverter implements Converter<Double> {

    private XmlNumberConverter c;

    public XmlDoubleConverter() {
        this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param min the min
     * @param max the max
     * @param multiplier the multiplier
     */
    public XmlDoubleConverter(double min, double max, double multiplier) {
        this(min, max, multiplier, false, null);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param min the min
     * @param max the max
     * @param multiplier the multiplier
     * @param allowsNullValue whether null values are allowed
     */
    public XmlDoubleConverter(double min, double max, double multiplier, boolean allowsNullValue) {
        this(min, max, multiplier, allowsNullValue, null);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param min the min
     * @param max the max
     * @param multiplier the multiplier
     * @param allowsNullValue whether null values are allowed
     * @param unit the unit string
     */
    public XmlDoubleConverter(double min, double max, double multiplier, boolean allowsNullValue, String unit) {
        c = new XmlNumberConverter(min, max, multiplier, allowsNullValue, unit);
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, Double value) throws IOException {
        c.toString(out, idFactory, value);
    }

    @Nonnull
    @Override
    public Double fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        Number n = c.fromString(in, idFactory);
        return (n == null || n instanceof Double) ? (Double) n : n.doubleValue();
    }

    @Nonnull
    @Override
    public Double getDefaultValue() {
        Number n = c.getDefaultValue();
        return (n == null || n instanceof Double) ? (Double) n : n.doubleValue();
    }

}
