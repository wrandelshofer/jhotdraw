/* @(#)NumberConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import org.jhotdraw8.io.IdFactory;

/**
 * Formats real numbers.
 * <p>
 * FIXME should be a converter factory and not a converter.
 * </p>
 * <p>
 * Supports clamping into a {@code [min,max]} range (inclusive), a scale factor
 * and a unit label.
 * </p>
 * <p>
 * Also allows to specify the minimum and maximum of integer digits, fraction
 * digits, as well as the minimum of negative and positive exponent.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NumberConverter implements Converter<Number> {

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
    private DecimalFormat doubleDecimalFormat;
    private DecimalFormat floatDecimalFormat;
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
    public NumberConverter() {
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
    public NumberConverter(double min, double max, double multiplier) {
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
    public NumberConverter(double min, double max, double multiplier, boolean allowsNullValue) {
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
    public NumberConverter(double min, double max, double multiplier, boolean allowsNullValue, String unit) {
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
        doubleDecimalFormat = new DecimalFormat("#################0.#################", s);
        floatDecimalFormat = new DecimalFormat("#################0.########", s);
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
        doubleDecimalFormat.setMinimumFractionDigits(newValue);
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

        if (value instanceof Double) {
            double v = ((Double) value).doubleValue();
            if (factor != 1.0) {
                v = v * factor;
            }
            if (Double.isInfinite(v)) {
                if (v < 0.0) {
                    buf.append('-');
                }
                buf.append("INF");
            } else if (Double.isNaN(v)) {
                buf.append("NaN");
            } else {
                String str;
                BigDecimal big = new BigDecimal(v);
                int exponent = big.scale() >= 0 ? big.precision() - big.scale() : -big.scale();
                if (!usesScientificNotation || exponent > minNegativeExponent
                        && exponent < minPositiveExponent) {
                    str = doubleDecimalFormat.format(v);
                } else {
                    str = scientificFormat.format(v);
                }
                buf.append(str);
            }
        } else if (value instanceof Float) {
            float v = ((Float) value).floatValue();
            if (factor != 1.0) {
                v = (float) (v * factor);
            }
            String str;// = Float.toString(v);
            BigDecimal big = new BigDecimal(v);
            int exponent = big.scale() >= 0 ? big.precision() - big.scale() : -big.scale();
            if (!usesScientificNotation || exponent > minNegativeExponent
                    && exponent < minPositiveExponent) {
                str = floatDecimalFormat.format(v);
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
        if (value != null) {
            if (unit != null) {
                buf.append(unit);
            }
        }
        return;
    }

    @Override
    public Number fromString(CharBuffer str, IdFactory idFactory) throws ParseException {
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
                char c = str.charAt(end);// does not consume chars from CharBuffer!
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
                    case 'I':// INF
                    case 'N': // NaN
                        end += 3;
                        break Outer;
                    default:
                        break Outer;
                }
            }
        }

        String text = str.subSequence(0, end).toString();
        switch (text) {
            case "-INF":
                str.position(str.position() + end);
                return Double.NEGATIVE_INFINITY;
            case "INF":
                str.position(str.position() + end);
                return Double.POSITIVE_INFINITY;
            case "NaN":
                str.position(str.position() + end);
                return Double.NaN;
        }

        // Remove unit from text
        if (unit != null && end + unit.length() <= str.length()) {
            if (str.subSequence(end, end + unit.length()).toString().startsWith(unit)) {
                end += unit.length();
            }
        }
        if (text.isEmpty()) {
            throw new ParseException("invalid value", str.position());
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
                    value = Float.valueOf(v);
                } else if (valueClass == Double.class) {
                    double v = Double.parseDouble(text);
                    if (factor != 1.0) {
                        v = (v / factor);
                    }
                    value = Double.valueOf(v);
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
                    throw new ParseException("parse error (1)", str.position());
                }
            } catch (NumberFormatException e) {
                ParseException pe = new ParseException("illegal number format", str.position());
                pe.initCause(e);
                throw pe;
            }
        } else {
            throw new ParseException("illegal value class:" + valueClass, str.position());
        }

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
        return (Number) value;
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
        doubleDecimalFormat.setMinimumIntegerDigits(newValue);
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
        doubleDecimalFormat.setMaximumIntegerDigits(newValue);
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
        doubleDecimalFormat.setMaximumFractionDigits(newValue);
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

    /**
     * Gets the value class.
     *
     * @return the value class
     */
    public Class<? extends Number> getValueClass() {
        return valueClass;
    }

    /**
     * Sets the value class.
     *
     * @param valueClass the value class
     */
    public void setValueClass(Class<? extends Number> valueClass) {
        this.valueClass = valueClass;
    }

    @Override
    public Number getDefaultValue() {
        return 0.0;
    }
}
