/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import org.jhotdraw.draw.io.IdFactory;

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
public class XmlNumberConverter implements Converter<Number> {

    private static final long serialVersionUID = 1L;

    /**
     * Specifies whether the formatter allows null values.
     */
    private boolean allowsNullValue = false;
    @SuppressWarnings("rawtypes")
    private Comparable min;
    @SuppressWarnings("rawtypes")
    private Comparable max;
    private String unit;
    private DecimalFormat decimalFormat;
    private DecimalFormat scientificFormat;
    private double factor = 1;
    private int minIntDigits;
    private int maxIntDigits;
    private int minFractionDigits;
    private int maxFractionDigits;
    private int minNegativeExponent = -3;
    private int minPositiveExponent = 8;
    private boolean usesScientificNotation = true;

    /**
     * Creates a <code>NumberFormatter</code> with the a default
     * <code>NumberFormat</code> instance obtained from
     * <code>NumberFormat.getNumberInstance()</code>.
     */
    public XmlNumberConverter() {
        super();
        initFormats();
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param min the min
     * @param max the max
     * @param multiplier the multiplier
     */
    public XmlNumberConverter(double min, double max, double multiplier) {
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
    public XmlNumberConverter(double min, double max, double multiplier, boolean allowsNullValue) {
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
    public XmlNumberConverter(double min, double max, double multiplier, boolean allowsNullValue, String unit) {
        super();
        initFormats();
        this.min = min;
        this.max = max;
        this.factor = multiplier;
        this.allowsNullValue = allowsNullValue;
        this.unit = unit;
    }

    private void initFormats() {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.ENGLISH);
        decimalFormat = new DecimalFormat("#################0.#################", s);
        scientificFormat = new DecimalFormat("0.0################E0", s);
    }

    /**
     * Sets the minimum permissible value. If the <code>valueClass</code> has
     * not been specified, and <code>minimum</code> is non null, the
     * <code>valueClass</code> will be set to that of the class of
     * <code>minimum</code>.
     *
     * @param minimum Minimum legal value that can be input
     */
    @SuppressWarnings("rawtypes")
    public void setMinimum(Comparable minimum) {
        min = minimum;
    }

    /**
     * Returns the minimum permissible value.
     *
     * @return Minimum legal value that can be input
     */
    @SuppressWarnings("rawtypes")
    public Comparable getMinimum() {
        return min;
    }

    /**
     * Sets the maximum permissible value. If the <code>valueClass</code> has
     * not been specified, and <code>max</code> is non null, the
     * <code>valueClass</code> will be set to that of the class of
     * <code>max</code>.
     *
     * @param max Maximum legal value that can be input
     */
    @SuppressWarnings("rawtypes")
    public void setMaximum(Comparable max) {
        this.max = max;
    }

    /**
     * Returns the maximum permissible value.
     *
     * @return Maximum legal value that can be input
     */
    @SuppressWarnings("rawtypes")
    public Comparable getMaximum() {
        return max;
    }

    /**
     * Sets the factor for use in percent, per mille, and similar formats.
     *
     * @param newValue the factor
     */
    public void setFactor(double newValue) {
        factor = newValue;
    }

    /**
     * Gets the factor for use in percent, per mille, and similar formats.
     *
     * @return the factor
     */
    public double getFactor() {
        return factor;
    }

    /**
     * Allows/Disallows null values.
     *
     * @param newValue true if null values are allowed
     */
    public void setAllowsNullValue(boolean newValue) {
        allowsNullValue = newValue;
    }

    /**
     * Returns true if null values are allowed.
     *
     * @return true if null values are allowed
     */
    public boolean getAllowsNullValue() {
        return allowsNullValue;
    }

    /**
     * Specifies how many "0" are appended to double and float values. By
     * default this is 0.
     *
     * @param newValue the value
     */
    public void setMinimumFractionDigits(int newValue) {
        minFractionDigits = newValue;
        decimalFormat.setMinimumFractionDigits(newValue);
    }

    /**
     * Returns the minimum fraction digits.
     *
     * @return the minimum fraction digits
     */
    public int getMinimumFractionDigits() {
        return minFractionDigits;
    }

    @Override
    public void toString(Appendable buf, IdFactory idFactory, Number value) throws IOException {
        if (value == null && allowsNullValue) {
            return;
        }

        double v = value.doubleValue();
        if (factor != 1.0) {
            v = v * factor;
        }
        String str;
        BigDecimal big = new BigDecimal(v);
        int exponent = big.scale() >= 0 ? big.precision() - big.scale() : -big.scale();
        if (!usesScientificNotation || exponent > minNegativeExponent
                && exponent < minPositiveExponent) {
            str = decimalFormat.format(v);
        } else {
            str = scientificFormat.format(v);
        }
        buf.append(str);

        if (value != null) {
            if (unit != null) {
                buf.append(unit);
            }
        }
        return;
    }

    @Override
    public Double fromString(CharBuffer str, IdFactory idFactory) throws ParseException {
        if ((str == null || str.length() == 0) && getAllowsNullValue()) {
            return null;
        }

        // Parse the remaining characters from the CharBuffer
        final int remaining = str.remaining();
        int end = 0; // end is a relative to CharBuffer.position();
        {
            boolean noMoreSigns = false;
            boolean noMorePoints = false;
            boolean noMoreEs = false;
            Outer:
            for (; end < remaining; end++) {
                char c = str.charAt(end); // does not consume chars from CharBuffer!
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

        String text = str.subSequence(0, end).toString();
        // Remove unit from text
        if (unit != null && end + unit.length() <= remaining) {
            if (str.subSequence(end, end + unit.length()).toString().startsWith(unit)) {
                end += unit.length();
            }
        }

        if (text.isEmpty()) {
            throw new ParseException("invalid value", str.position());
        }

        Object value;
        double v = Double.parseDouble(text);
        if (factor != 1.0) {
            v = (v / factor);
        }
        value = new Double(v);

        try {
            if (!isValidValue(value, true)) {
                throw new ParseException("invalid value", str.position());
            }
        } catch (ClassCastException cce) {
            ParseException pe = new ParseException("invalid value", str.position());
            pe.initCause(cce);
            throw pe;
        }
        // consume the text that we just parsed
        str.position(str.position() + end);
        return (Double) value;
    }

    /**
     * Returns true if <code>value</code> is between the min/max.
     *
     * @param wantsCCE If false, and a ClassCastException is thrown in comparing
     * the values, the exception is consumed and false is returned.
     */
    @SuppressWarnings("unchecked")
    boolean isValidValue(Object value, boolean wantsCCE) {
        try {
            if (min != null && min.compareTo((Number) value) > 0) {
                return false;
            }
        } catch (ClassCastException cce) {
            if (wantsCCE) {
                throw cce;
            }
            return false;
        }

        try {
            if (max != null && max.compareTo((Number) value) < 0) {
                return false;
            }
        } catch (ClassCastException cce) {
            if (wantsCCE) {
                throw cce;
            }
            return false;
        }
        return true;
    }

    /**
     * If non-null the unit string is appended to the value.
     *
     * @param value the unit string
     */
    public void setUnit(String value) {
        unit = value;
    }

    /**
     * If non-null the unit string is appended to the value.
     *
     * @return the unit string
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Gets the minimum number of digits allowed in the integer portion of a
     * number.
     *
     * @return the minimum integer digits
     */
    public int getMinimumIntegerDigits() {
        return minIntDigits;
    }

    /**
     * Sets the minimum number of digits allowed in the integer portion of a
     * number.
     *
     * @param newValue the new value
     */
    public void setMinimumIntegerDigits(int newValue) {
        decimalFormat.setMinimumIntegerDigits(newValue);
        scientificFormat.setMinimumIntegerDigits(newValue);
        this.minIntDigits = newValue;
    }

    /**
     * Gets the maximum number of digits allowed in the integer portion of a
     * number.
     *
     * @return the maximum integer digits
     */
    public int getMaximumIntegerDigits() {
        return maxIntDigits;
    }

    /**
     * Sets the maximum number of digits allowed in the integer portion of a
     * number.
     *
     * @param newValue the new value
     */
    public void setMaximumIntegerDigits(int newValue) {
        decimalFormat.setMaximumIntegerDigits(newValue);
        scientificFormat.setMaximumIntegerDigits(newValue);
        this.maxIntDigits = newValue;
    }

    /**
     * Gets the maximum number of digits allowed in the fraction portion of a
     * number.
     *
     * @return the maximum fraction digits
     */
    public int getMaximumFractionDigits() {
        return maxFractionDigits;
    }

    /**
     * Sets the maximum number of digits allowed in the fraction portion of a
     * number.
     *
     * @param newValue the maximum fraction digits
     */
    public void setMaximumFractionDigits(int newValue) {
        decimalFormat.setMaximumFractionDigits(newValue);
        scientificFormat.setMaximumFractionDigits(newValue);
        this.maxFractionDigits = newValue;
    }

    /**
     * Gets the minimum negative exponent value for scientific notation.
     *
     * @return the minimum negative exponent
     */
    public int getMinimumNegativeExponent() {
        return minNegativeExponent;
    }

    /**
     * Sets the minimum negative exponent value for scientific notation.
     *
     * @param newValue the minimum negative exponent
     */
    public void setMinimumNegativeExponent(int newValue) {
        this.minNegativeExponent = newValue;
    }

    /**
     * Gets the minimum positive exponent value for scientific notation.
     *
     * @return the minimum positive exponent
     */
    public int getMinimumPositiveExponent() {
        return minPositiveExponent;
    }

    /**
     * Sets the minimum positive exponent value for scientific notation.
     *
     * @param newValue the maximum positive exponent
     */
    public void setMinimumPositiveExponent(int newValue) {
        this.minPositiveExponent = newValue;
    }

    /**
     * Returns true if scientific notation is used.
     *
     * @return true if scientific notation is used
     */
    public boolean isUsesScientificNotation() {
        return usesScientificNotation;
    }

    /**
     * Sets whether scientific notation is used.
     *
     * @param newValue true if scientific notation is used
     */
    public void setUsesScientificNotation(boolean newValue) {
        this.usesScientificNotation = newValue;
    }

    @Override
    public Double getDefaultValue() {
        return allowsNullValue ? null : 0.0;
    }
}
