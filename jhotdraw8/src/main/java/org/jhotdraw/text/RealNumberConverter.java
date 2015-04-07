/*
 * @(#)RealNumberConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * {@code ScaledNumberFormatter} is used to format real numbers.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RealNumberConverter implements Converter<Number> {

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
    private Class<? extends Number> valueClass = Double.class;

    /**
     * Creates a <code>NumberFormatter</code> with the a default
     * <code>NumberFormat</code> instance obtained from
     * <code>NumberFormat.getNumberInstance()</code>.
     */
    public RealNumberConverter() {
        super();
        initFormats();
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     */
    public RealNumberConverter(double min, double max, double multiplier) {
        this(min, max, multiplier, false, null);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     */
    public RealNumberConverter(double min, double max, double multiplier, boolean allowsNullValue) {
        this(min, max, multiplier, allowsNullValue, null);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     */
    public RealNumberConverter(double min, double max, double multiplier, boolean allowsNullValue, String unit) {
        super();
        initFormats();
        setMinimum(min);
        setMaximum(max);
        setFactor(multiplier);
        setAllowsNullValue(allowsNullValue);
        setUnit(unit);
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
     * @see #setValueClass
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
     * @see #setValueClass
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
     */
    public void setFactor(double newValue) {
        factor = newValue;
    }

    /**
     * Gets the factor for use in percent, per mille, and similar formats.
     */
    public double getFactor() {
        return factor;
    }

    /**
     * Allows/Disallows null values.
     *
     * @param newValue
     */
    public void setAllowsNullValue(boolean newValue) {
        allowsNullValue = newValue;
    }

    /**
     * Returns true if null values are allowed.
     */
    public boolean getAllowsNullValue() {
        return allowsNullValue;
    }

    /**
     * Specifies whether ".0" is appended to double and float
     * values. By default this is true.
     *
     * @param newValue
     */
    public void setMinimumFractionDigits(int newValue) {
        minFractionDigits = newValue;
        decimalFormat.setMinimumFractionDigits(newValue);
    }

    /**
     * Returns true if null values are allowed.
     */
    public int getMinimumFractionDigits() {
        return minFractionDigits;
    }

    /**
     * Returns a String representation of the Object <code>value</code>.
     * This invokes <code>format</code> on the current <code>Format</code>.
     *
     * @throws ParseException if there is an error in the conversion
     * @param value Value to convert
     * @return String representation of value
     */
    @Override
    public String toString(Number value) {
        if (value == null && allowsNullValue) {
            return "";
        }

        StringBuilder buf = new StringBuilder();
        if (value instanceof Double) {
            double v = ((Double) value).doubleValue();
            if (factor != 1.0) {
                v = v * factor;
            }
            String str;
            BigDecimal big = new BigDecimal(v);
            int exponent = big.scale() >= 0 ? big.precision() - big.scale() : -big.scale();
            if (!usesScientificNotation || exponent > minNegativeExponent && exponent < minPositiveExponent) {
                str = decimalFormat.format(v);
            } else {
                str = scientificFormat.format(v);
            }
            buf.append(str);
        } else if (value instanceof Float) {
            float v = ((Float) value).floatValue();
            if (factor != 1.0) {
                v = (float) (v * factor);
            }
            String str;// = Float.toString(v);
            BigDecimal big = new BigDecimal(v);
            int exponent = big.scale() >= 0 ? big.precision() - big.scale() : -big.scale();
            if (!usesScientificNotation || exponent > minNegativeExponent && exponent < minPositiveExponent) {
                str = decimalFormat.format(v);
            } else {
                str = scientificFormat.format(v);
            }
            buf.append(str);
        } else if (value instanceof Long) {
            long v = ((Long) value).longValue();
            if (factor != 1.0) {
                v = (long) (v * factor);
            }
            buf.append(Long.toString(v));
        } else if (value instanceof Integer) {
            int v = ((Integer) value).intValue();
            if (factor != 1.0) {
                v = (int) (v * factor);
            }
            buf.append(Integer.toString(v));
        } else if (value instanceof Byte) {
            byte v = ((Byte) value).byteValue();
            if (factor != 1.0) {
                v = (byte) (v * factor);
            }
            buf.append(Byte.toString(v));
        } else if (value instanceof Short) {
            short v = ((Short) value).shortValue();
            if (factor != 1.0) {
                v = (short) (v * factor);
            }
            buf.append(Short.toString(v));
        }
        if (buf.length() != 0) {
            if (unit != null) {
                buf.append(unit);
            }
            return buf.toString();
        }
        throw new IllegalArgumentException("Value is of unsupported class " + value);
    }

    /**
     * Returns the <code>Object</code> representation of the
     * <code>String</code> <code>text</code>.
     *
     * @param text <code>String</code> to convert
     * @return <code>Object</code> representation of text
     * @throws ParseException if there is an error in the conversion
     */
    @Override
    public Number toValue(String text) throws ParseException {
        ParsePosition pp = new ParsePosition(0);
        Number value = toValue(text, pp);
        return value;
    }

    /**
     * Returns the <code>Object</code> representation of the
     * <code>String</code> <code>text</code>.
     *
     * @param text <code>String</code> to convert
     * @return <code>Object</code> representation of text
     * @throws ParseException if there is an error in the conversion
     */
    @Override
    public Number toValue(String str, ParsePosition pp) {
        if ((str == null || str.length() == 0) && getAllowsNullValue()) {
            return null;
        }

        // Parse
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

        // Remove unit from text
        if (unit != null && end < str.length()) {
            if (str.substring(end).startsWith(unit)) {
                end += unit.length();
            }
        }

        Class<?> valueClass = getValueClass();
        Object value;
        if (valueClass != null) {
            try {
                if (valueClass == Integer.class) {
                    int v = Integer.parseInt(text);
                    if (factor != 1.0) {
                        v = (int) (v / factor);
                    }
                    value = v;
                } else if (valueClass == Long.class) {
                    long v = Long.parseLong(text);
                    if (factor != 1.0) {
                        v = (long) (v / factor);
                    }
                    value = v;
                } else if (valueClass == Float.class) {
                    float v = Float.parseFloat(text);
                    if (factor != 1.0) {
                        v = (float) (v / factor);
                    }
                    value = new Float(v);
                } else if (valueClass == Double.class) {
                    double v = Double.parseDouble(text);
                    if (factor != 1.0) {
                        v = (v / factor);
                    }
                    value = new Double(v);
                } else if (valueClass == Byte.class) {
                    byte v = Byte.parseByte(text);
                    if (factor != 1.0) {
                        v = (byte) (v / factor);
                    }
                    value = v;
                } else if (valueClass == Short.class) {
                    short v = Short.parseShort(text);
                    if (factor != 1.0) {
                        v = (short) (v / factor);
                    }
                    value = v;
                } else {
                    pp.setErrorIndex(pp.getIndex());
                    return null;
                }
            } catch (NumberFormatException e) {
                pp.setErrorIndex(pp.getIndex());
                return null;
            }
        } else {
            pp.setErrorIndex(pp.getIndex());
            return null;
        }

        try {
            if (!isValidValue(value, true)) {
                pp.setErrorIndex(pp.getIndex());
                return null;
            }
        } catch (ClassCastException cce) {
            pp.setErrorIndex(pp.getIndex());
            return null;
        }
        pp.setIndex(end);
        return (Number) value;
    }

    /**
     * Returns true if <code>value</code> is between the min/max.
     *
     * @param wantsCCE If false, and a ClassCastException is thrown in
     *                 comparing the values, the exception is consumed and
     *                 false is returned.
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

    /** If non-null the unit string is appended to the value. */
    public void setUnit(String value) {
        unit = value;
    }

    /** If non-null the unit string is appended to the value. */
    public String getUnit() {
        return unit;
    }

    /**
     * Gets the minimum number of digits allowed in the integer portion of a
     * number.
     */
    public int getMinimumIntegerDigits() {
        return minIntDigits;
    }

    /**
     * Sets the minimum number of digits allowed in the integer portion of a
     * number.
     */
    public void setMinimumIntegerDigits(int newValue) {
        decimalFormat.setMinimumIntegerDigits(newValue);
        scientificFormat.setMinimumIntegerDigits(newValue);
        this.minIntDigits = newValue;
    }

    /**
     * Gets the maximum number of digits allowed in the integer portion of a
     * number.
     */
    public int getMaximumIntegerDigits() {
        return maxIntDigits;
    }

    /**
     * Sets the maximum number of digits allowed in the integer portion of a
     * number.
     */
    public void setMaximumIntegerDigits(int newValue) {
        decimalFormat.setMaximumIntegerDigits(newValue);
        scientificFormat.setMaximumIntegerDigits(newValue);
        this.maxIntDigits = newValue;
    }

    /**
     * Gets the maximum number of digits allowed in the fraction portion of a
     * number.
     */
    public int getMaximumFractionDigits() {
        return maxFractionDigits;
    }

    /**
     * Sets the maximum number of digits allowed in the fraction portion of a
     * number.
     */
    public void setMaximumFractionDigits(int newValue) {
        decimalFormat.setMaximumFractionDigits(newValue);
        scientificFormat.setMaximumFractionDigits(newValue);
        this.maxFractionDigits = newValue;
    }

    /**
     * Gets the minimum negative exponent value for scientific notation.
     */
    public int getMinimumNegativeExponent() {
        return minNegativeExponent;
    }

    /**
     * Sets the minimum negative exponent value for scientific notation.
     */
    public void setMinimumNegativeExponent(int newValue) {
        this.minNegativeExponent = newValue;
    }

    /**
     * Gets the minimum positive exponent value for scientific notation.
     */
    public int getMinimumPositiveExponent() {
        return minPositiveExponent;
    }

    /**
     * Sets the minimum positive exponent value for scientific notation.
     */
    public void setMinimumPositiveExponent(int newValue) {
        this.minPositiveExponent = newValue;
    }

    /**
     * Returns true if scientific notation is used.
     */
    public boolean isUsesScientificNotation() {
        return usesScientificNotation;
    }

    /**
     * Sets whether scientific notation is used.
     */
    public void setUsesScientificNotation(boolean newValue) {
        this.usesScientificNotation = newValue;
    }

    public Class<? extends Number> getValueClass() {
        return valueClass;
    }

    public void setValueClass(Class<? extends Number> valueClass) {
        this.valueClass = valueClass;
    }

}
