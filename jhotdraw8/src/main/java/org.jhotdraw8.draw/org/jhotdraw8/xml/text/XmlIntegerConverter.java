/* @(#)XmlIntegerConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * XmlIntegerConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlIntegerConverter implements Converter<Integer> {

    private XmlNumberConverter c;

    public XmlIntegerConverter() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param min the min
     * @param max the max
     */
    public XmlIntegerConverter(int min, int max) {
        this(min, max, false, null);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param min             the min
     * @param max             the max
     * @param allowsNullValue whether null values are allowed
     */
    public XmlIntegerConverter(int min, int max, boolean allowsNullValue) {
        this(min, max, allowsNullValue, null);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param min             the min
     * @param max             the max
     * @param allowsNullValue whether null values are allowed
     * @param unit            the unit string
     */
    public XmlIntegerConverter(int min, int max, boolean allowsNullValue, String unit) {
        c = new XmlNumberConverter(min, max, 1, allowsNullValue, unit);
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, Integer value) throws IOException {
        c.toString(out, idFactory, value);
    }

    @Nonnull
    @Override
    public Integer fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        Number n = c.fromString(in, idFactory);
        return (n == null || n instanceof Integer) ? (Integer) n : n.intValue();
    }

    @Nonnull
    @Override
    public Integer getDefaultValue() {
        Number n = c.getDefaultValue();
        return (n == null || n instanceof Integer) ? (Integer) n : n.intValue();
    }

}
