/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.text;

import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javafx.scene.shape.SVGPath;

/**
 * Converts a {@code Double} into the XML String representation.
 * <p>
 * Reference:
 * <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#double">W3C: XML
 * Schema Part 2: Datatypes Second Edition: 3.2.5 double</a>
 * </p>
 * <p>
 * The converter applies the linear function {@code f(x) = a*x + b} to the value
 * {@code x} before converting it to a string, and its inverse when converting
 * back from a string. {@code a} is referred to as the {@code factor} and
 * {@code b} as the {@code offset}.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XMLDoubleConverter implements Converter<Double> {
SVGPath s;
    private final HashSet<Double> enumeration;
    private final Double minInclusive;
    private final Double maxInclusive;
    private final Double minExclusive;
    private final Double maxExclusive;

    private final int minNegativeExponent = -3;
    private final int minPositiveExponent = 7;
    private final boolean alwaysUseScientificNotation = false;
    private final DecimalFormat decimalFormat;
    private final DecimalFormat scientificFormat;

    /**
     * Creates a new instance without constraints.
     */
    public XMLDoubleConverter() {
        this(null, null, null, null, null);
    }

    /**
     * Creates a new instance.
     *
     * @param enumeration The enumeration constraint.
     * @param minInclusive The minInclusive constraint.
     * @param maxInclusive The maxInclusive constraint.
     * @param minExclusive The minExclusive constraint.
     * @param maxExclusive The maxExclusive constraint.
     */
    public XMLDoubleConverter(Set<Double> enumeration, Double minInclusive, Double maxInclusive, Double minExclusive, Double maxExclusive) {
        this.enumeration = enumeration == null || enumeration.isEmpty() ? null : new HashSet<>(enumeration);
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.minExclusive = minExclusive;
        this.maxExclusive = maxExclusive;

        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.ENGLISH);
        decimalFormat = new DecimalFormat("######0.#################", s);
        scientificFormat = new DecimalFormat("0.#################E0", s);
    }

    @Override
    public String toString(Double v) {
        if (v == null) {
            return "";
        }
        if (Double.isNaN(v)) {
            return "NaN";
        }
        if (v == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }

        StringBuilder buf = new StringBuilder();
        String str;
        BigDecimal big = new BigDecimal(v);
        int exponent = big.scale() >= 0 ? big.precision() - big.scale() : -big.scale();
        if (alwaysUseScientificNotation || !(minNegativeExponent < exponent
                && exponent <= minPositiveExponent)) {
            str = scientificFormat.format(v);
        } else {
            str = decimalFormat.format(v);
        }
        buf.append(str);

        return str;
    }

    
    public Double fromString(String str, ParsePosition pp) {
        if ((str == null || str.length() - pp.getIndex() <= 0)) {
            return null;
        }

        if (str.startsWith("NaN", pp.getIndex())) {
            pp.setIndex(pp.getIndex() + 3);
            return Double.NaN;
        }
        if (str.startsWith("INF", pp.getIndex())) {
            pp.setIndex(pp.getIndex() + 3);
            return Double.POSITIVE_INFINITY;
        }
        if (str.startsWith("-INF", pp.getIndex())) {
            pp.setIndex(pp.getIndex() + 4);
            return Double.NEGATIVE_INFINITY;
        }

        // Parse. Find the end of the number.
        int end = pp.getIndex();
        {
            boolean noMoreSigns = false;
            boolean noMorePoints = false;
            boolean noMoreEs = false;
            Outer:
            for (; end < str.length(); end++) {
                char c = str.charAt(end);
                switch (c) {
                    case '+':
                    case '-':
                        if (noMoreSigns) {
                            break Outer;
                        }
                        noMoreSigns = true;
                        break;
                    case '.':
                        if (noMorePoints) {
                            break Outer;
                        }
                        noMoreSigns = true;
                        noMorePoints = true;
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        noMoreSigns = true;
                        break;
                    case 'e':
                    case 'E':
                        if (noMoreEs) {
                            break Outer;
                        }
                        noMoreSigns = false;
                        noMorePoints = false;
                        noMoreEs = true;
                        break;
                    default:
                        break Outer;
                }
            }
        }

        String text = str.substring(pp.getIndex(), end);

        Double value = 0.0;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            pp.setErrorIndex(pp.getIndex());
            return null;
        }

        if (!isValid(value)) {
            pp.setErrorIndex(pp.getIndex());
            return null;
        }

        pp.setIndex(end);
        return value;

    }

    private boolean isValid(Double value) {
        boolean valid = true;

        valid &= (enumeration == null) || enumeration.contains(value);
        valid &= (minInclusive == null) || minInclusive <= value;
        valid &= (minExclusive == null) || minExclusive < value;
        valid &= (maxInclusive == null) || value <= maxInclusive;
        valid &= (maxExclusive == null) || value < maxExclusive;

        return valid;
    }

    @Override
    public void toString(Double value, Appendable out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double fromString(CharBuffer buf) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
