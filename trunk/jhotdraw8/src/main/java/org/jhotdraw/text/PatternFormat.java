/* @(#)PatternFormat.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Same as MessageFormat, but allows to plug in your own formatters.
 * 
 * <p>
 PatternFormat uses patterns of the following form:

 <pre>{@code
 *     TextFormatPattern:
 *           String
 *           TextFormatPattern FormatElement String
 *
 *     FormatElement:
 *           { ArgumentIndex }
 *           { ArgumentIndex , FormatType }
 *           { ArgumentIndex , FormatType , FormatStyle }
 *
 *   FormatType: 
 *           as supported by the FormatFactory
 *
 *   FormatStyle:
 *           as supported by the FormatFactory
 * }</pre>
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PatternFormat extends Format {

    private FormatFactory factory;
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TextFormat for the default
     * {@link java.util.Locale.Category#FORMAT FORMAT} locale and the
     * specified pattern.
     * The constructor first sets the locale, then parses the pattern and
     * creates a list of subformats for the format elements contained in it.
     * Patterns and their interpretation are specified in the
     * <a href="#patterns">class description</a>.
     *
     * @param pattern the pattern for this message format
     * @exception IllegalArgumentException if the pattern is invalid
     */
    public PatternFormat(String pattern) {
        this.locale = Locale.getDefault(Locale.Category.FORMAT);
        this.factory = new FormatFactory() {

            @Override
            public Format apply(String type, String style) {
                if (type == null) {
                    return new DefaultFormat();
                }
                switch (type) {
                    case "number":
                        if (style == null) {
                            return NumberFormat.getInstance(locale);
                        }
                        switch (style) {
                            case "integer":
                                return NumberFormat.getIntegerInstance(locale);
                            case "currency":
                                return NumberFormat.getCurrencyInstance(locale);
                            case "percent":
                                return NumberFormat.getPercentInstance(locale);
                            default:
                                return new DecimalFormat(style, DecimalFormatSymbols.getInstance(locale));
                        }
                    case "date":
                        if (style == null) {
                            return DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                        }
                        switch (style) {
                            case "short":
                                return DateFormat.getDateInstance(DateFormat.SHORT, locale);
                            case "medium":
                                return DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
                            case "long":
                                return DateFormat.getDateInstance(DateFormat.LONG, locale);
                            case "full":
                                return DateFormat.getDateInstance(DateFormat.FULL, locale);
                            default:
                                return new SimpleDateFormat(style, locale);
                        }
                    case "time":
                        if (style == null) {
                            return DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
                        }
                        switch (style) {
                            case "short":
                                return DateFormat.getTimeInstance(DateFormat.SHORT, locale);
                            case "medium":
                                return DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
                            case "long":
                                return DateFormat.getTimeInstance(DateFormat.LONG, locale);
                            case "full":
                                return DateFormat.getTimeInstance(DateFormat.FULL, locale);
                            default:
                                return new SimpleDateFormat(style, locale);
                        }
                    case "choice":
                        return new ChoiceFormat(style);
                    default:
                        throw new IllegalArgumentException("type=" + type);
                }
            }
        };
        applyPattern(pattern);
    }

    /**
     * Constructs a TextFormat with the specified format types.
     * No locale is used.
     *
     * @param pattern the pattern for this message format
     * @param formatTypes the format types to be used
     * @exception IllegalArgumentException if the pattern is invalid
     */
    public PatternFormat(String pattern, FormatFactory factory) {
        this.locale = null;
        this.factory = factory;
        applyPattern(pattern);
    }

    /**
     * Constructs a TextFormat for the specified locale and
     * pattern.
     * The constructor first sets the locale, then parses the pattern and
     * creates a list of subformats for the format elements contained in it.
     * Patterns and their interpretation are specified in the
     * <a href="#patterns">class description</a>.
     *
     * @param pattern the pattern for this message format
     * @param locale the locale for this message format
     * @exception IllegalArgumentException if the pattern is invalid
     * @since 1.4
     */
    public PatternFormat(String pattern, Locale locale) {
        this.locale = locale;
        applyPattern(pattern);
    }

    /**
     * Sets the locale to be used when creating or comparing subformats.
     * This affects subsequent calls
     * <ul>
     * <li>to the {@link #applyPattern applyPattern}
     *     and {@link #toPattern toPattern} methods if format elements specify
     *     a format type and therefore have the subformats created in the
     *     <code>applyPattern</code> method, as well as
     * <li>to the <code>format</code> and
     *     {@link #formatToCharacterIterator formatToCharacterIterator} methods
     *     if format elements do not specify a format type and therefore have
     *     the subformats created in the formatting methods.
     * </ul>
     * Subformats that have already been created are not affected.
     *
     * @param locale the locale to be used when creating or comparing subformats
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the locale that's used when creating or comparing subformats.
     *
     * @return the locale used when creating or comparing subformats
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the pattern used by this message format.
     * The method parses the pattern and creates a list of subformats
     * for the format elements contained in it.
     * Patterns and their interpretation are specified in the
     * <a href="#patterns">class description</a>.
     *
     * @param pattern the pattern for this message format
     * @exception IllegalArgumentException if the pattern is invalid
     */
    @SuppressWarnings("fallthrough") // fallthrough in switch is expected, suppress it
    public void applyPattern(String pattern) {
        StringBuilder[] segments = new StringBuilder[4];
        // Allocate only segments[SEG_RAW] here. The rest are
        // allocated on demand.
        segments[SEG_RAW] = new StringBuilder();

        int part = SEG_RAW;
        int formatNumber = 0;
        boolean inQuote = false;
        int braceStack = 0;
        maxOffset = -1;
        for (int i = 0; i < pattern.length(); ++i) {
            char ch = pattern.charAt(i);
            if (part == SEG_RAW) {
                if (ch == '\'') {
                    if (i + 1 < pattern.length()
                            && pattern.charAt(i + 1) == '\'') {
                        segments[part].append(ch);  // handle doubles
                        ++i;
                    } else {
                        inQuote = !inQuote;
                    }
                } else if (ch == '{' && !inQuote) {
                    part = SEG_INDEX;
                    if (segments[SEG_INDEX] == null) {
                        segments[SEG_INDEX] = new StringBuilder();
                    }
                } else {
                    segments[part].append(ch);
                }
            } else {
                if (inQuote) {              // just copy quotes in parts
                    segments[part].append(ch);
                    if (ch == '\'') {
                        inQuote = false;
                    }
                } else {
                    switch (ch) {
                        case ',':
                            if (part < SEG_MODIFIER) {
                                if (segments[++part] == null) {
                                    segments[part] = new StringBuilder();
                                }
                            } else {
                                segments[part].append(ch);
                            }
                            break;
                        case '{':
                            ++braceStack;
                            segments[part].append(ch);
                            break;
                        case '}':
                            if (braceStack == 0) {
                                part = SEG_RAW;
                                makeFormat(i, formatNumber, segments);
                                formatNumber++;
                                // throw away other segments
                                segments[SEG_INDEX] = null;
                                segments[SEG_TYPE] = null;
                                segments[SEG_MODIFIER] = null;
                            } else {
                                --braceStack;
                                segments[part].append(ch);
                            }
                            break;
                        case ' ':
                            // Skip any leading space chars for SEG_TYPE.
                            if (part != SEG_TYPE || segments[SEG_TYPE].length() > 0) {
                                segments[part].append(ch);
                            }
                            break;
                        case '\'':
                            inQuote = true;
                        // fall through, so we keep quotes in other parts
                        default:
                            segments[part].append(ch);
                            break;
                    }
                }
            }
        }
        if (braceStack == 0 && part != 0) {
            maxOffset = -1;
            throw new IllegalArgumentException("Unmatched braces in the pattern.");
        }
        this.pattern = segments[0].toString();
    }

    /**
     * Returns a pattern representing the current state of the message format.
     * The string is constructed from internal information and therefore
     * does not necessarily equal the previously applied pattern.
     *
     * @return a pattern representing the current state of the message format
     */
    public String toPattern() {
        // later, make this more extensible
        int lastOffset = 0;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= maxOffset; ++i) {
            copyAndFixQuotes(pattern, lastOffset, offsets[i], result);
            lastOffset = offsets[i];
            result.append('{').append(argumentNumbers[i]);
            Format fmt = formats[i];
            if (fmt == null) {
                // do nothing, string format
            } else {
                result.append(fmt.toString());
            }
            result.append('}');
        }
        copyAndFixQuotes(pattern, lastOffset, pattern.length(), result);
        return result.toString();
    }

    /**
     * Sets the formats to use for the values passed into
     * <code>format</code> methods or returned from <code>parse</code>
     * methods. The indices of elements in <code>newFormats</code>
     * correspond to the argument indices used in the previously set
     * pattern string.
     * The order of formats in <code>newFormats</code> thus corresponds to
     * the order of elements in the <code>arguments</code> array passed
     * to the <code>format</code> methods or the result array returned
     * by the <code>parse</code> methods.
     * <p>
     * If an argument index is used for more than one format element
     * in the pattern string, then the corresponding new format is used
     * for all such format elements. If an argument index is not used
     * for any format element in the pattern string, then the
     * corresponding new format is ignored. If fewer formats are provided
     * than needed, then only the formats for argument indices less
     * than <code>newFormats.length</code> are replaced.
     *
     * @param newFormats the new formats to use
     * @exception NullPointerException if <code>newFormats</code> is null
     * @since 1.4
     */
    public void setFormatsByArgumentIndex(Format[] newFormats) {
        for (int i = 0; i <= maxOffset; i++) {
            int j = argumentNumbers[i];
            if (j < newFormats.length) {
                formats[i] = newFormats[j];
            }
        }
    }

    /**
     * Sets the formats to use for the format elements in the
     * previously set pattern string.
     * The order of formats in <code>newFormats</code> corresponds to
     * the order of format elements in the pattern string.
     * <p>
     * If more formats are provided than needed by the pattern string,
     * the remaining ones are ignored. If fewer formats are provided
     * than needed, then only the first <code>newFormats.length</code>
     * formats are replaced.
     * <p>
     * Since the order of format elements in a pattern string often
     * changes during localization, it is generally better to use the
     * {@link #setFormatsByArgumentIndex setFormatsByArgumentIndex}
     * method, which assumes an order of formats corresponding to the
     * order of elements in the <code>arguments</code> array passed to
     * the <code>format</code> methods or the result array returned by
     * the <code>parse</code> methods.
     *
     * @param newFormats the new formats to use
     * @exception NullPointerException if <code>newFormats</code> is null
     */
    public void setFormats(Format[] newFormats) {
        int runsToCopy = newFormats.length;
        if (runsToCopy > maxOffset + 1) {
            runsToCopy = maxOffset + 1;
        }
        for (int i = 0; i < runsToCopy; i++) {
            formats[i] = newFormats[i];
        }
    }

    /**
     * Sets the format to use for the format elements within the
     * previously set pattern string that use the given argument
     * index.
     * The argument index is part of the format element definition and
     * represents an index into the <code>arguments</code> array passed
     * to the <code>format</code> methods or the result array returned
     * by the <code>parse</code> methods.
     * <p>
     * If the argument index is used for more than one format element
     * in the pattern string, then the new format is used for all such
     * format elements. If the argument index is not used for any format
     * element in the pattern string, then the new format is ignored.
     *
     * @param argumentIndex the argument index for which to use the new format
     * @param newFormat the new format to use
     * @since 1.4
     */
    public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
        for (int j = 0; j <= maxOffset; j++) {
            if (argumentNumbers[j] == argumentIndex) {
                formats[j] = newFormat;
            }
        }
    }

    /**
     * Sets the format to use for the format element with the given
     * format element index within the previously set pattern string.
     * The format element index is the zero-based number of the format
     * element counting from the start of the pattern string.
     * <p>
     * Since the order of format elements in a pattern string often
     * changes during localization, it is generally better to use the
     * {@link #setFormatByArgumentIndex setFormatByArgumentIndex}
     * method, which accesses format elements based on the argument
     * index they specify.
     *
     * @param formatElementIndex the index of a format element within the pattern
     * @param newFormat the format to use for the specified format element
     * @exception ArrayIndexOutOfBoundsException if {@code formatElementIndex} is equal to or
     *            larger than the number of format elements in the pattern string
     */
    public void setFormat(int formatElementIndex, Format newFormat) {
        formats[formatElementIndex] = newFormat;
    }

    /**
     * Gets the formats used for the values passed into
     * <code>format</code> methods or returned from <code>parse</code>
     * methods. The indices of elements in the returned array
     * correspond to the argument indices used in the previously set
     * pattern string.
     * The order of formats in the returned array thus corresponds to
     * the order of elements in the <code>arguments</code> array passed
     * to the <code>format</code> methods or the result array returned
     * by the <code>parse</code> methods.
     * <p>
     * If an argument index is used for more than one format element
     * in the pattern string, then the format used for the last such
     * format element is returned in the array. If an argument index
     * is not used for any format element in the pattern string, then
     * null is returned in the array.
     *
     * @return the formats used for the arguments within the pattern
     * @since 1.4
     */
    public Format[] getFormatsByArgumentIndex() {
        int maximumArgumentNumber = -1;
        for (int i = 0; i <= maxOffset; i++) {
            if (argumentNumbers[i] > maximumArgumentNumber) {
                maximumArgumentNumber = argumentNumbers[i];
            }
        }
        Format[] resultArray = new Format[maximumArgumentNumber + 1];
        for (int i = 0; i <= maxOffset; i++) {
            resultArray[argumentNumbers[i]] = formats[i];
        }
        return resultArray;
    }

    /**
     * Gets the formats used for the format elements in the
     * previously set pattern string.
     * The order of formats in the returned array corresponds to
     * the order of format elements in the pattern string.
     * <p>
     * Since the order of format elements in a pattern string often
     * changes during localization, it's generally better to use the
     * {@link #getFormatsByArgumentIndex getFormatsByArgumentIndex}
     * method, which assumes an order of formats corresponding to the
     * order of elements in the <code>arguments</code> array passed to
     * the <code>format</code> methods or the result array returned by
     * the <code>parse</code> methods.
     *
     * @return the formats used for the format elements in the pattern
     */
    public Format[] getFormats() {
        Format[] resultArray = new Format[maxOffset + 1];
        System.arraycopy(formats, 0, resultArray, 0, maxOffset + 1);
        return resultArray;
    }

    /**
     * Formats an array of objects and appends the <code>PatternFormat</code>'s
     * pattern, with format elements replaced by the formatted objects, to the
     * provided <code>StringBuffer</code>.
     * <p>
     * The text substituted for the individual format elements is derived from
     * the current subformat of the format element and the
     * <code>arguments</code> element at the format element's argument index
     * as indicated by the first matching line of the following table. An
     * argument is <i>unavailable</i> if <code>arguments</code> is
     * <code>null</code> or has fewer than argumentIndex+1 elements.
     *
     * <table border=1 summary="Examples of subformat,argument,and formatted text">
     *    <tr>
     *       <th>Subformat
     *       <th>Argument
     *       <th>Formatted Text
     *    <tr>
     *       <td><i>any</i>
     *       <td><i>unavailable</i>
     *       <td><code>"{" + argumentIndex + "}"</code>
     *    <tr>
     *       <td><i>any</i>
     *       <td><code>null</code>
     *       <td><code>"null"</code>
     *    <tr>
     *       <td><code>instanceof ChoiceFormat</code>
     *       <td><i>any</i>
     *       <td><code>subformat.format(argument).indexOf('{') &gt;= 0 ?<br>
     (new PatternFormat(subformat.format(argument), getLocale())).format(argument) :
     subformat.format(argument)</code>
     *    <tr>
     *       <td><code>!= null</code>
     *       <td><i>any</i>
     *       <td><code>subformat.format(argument)</code>
     *    <tr>
     *       <td><code>null</code>
     *       <td><code>instanceof Number</code>
     *       <td><code>NumberFormat.getInstance(getLocale()).format(argument)</code>
     *    <tr>
     *       <td><code>null</code>
     *       <td><code>instanceof Date</code>
     *       <td><code>DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, getLocale()).format(argument)</code>
     *    <tr>
     *       <td><code>null</code>
     *       <td><code>instanceof String</code>
     *       <td><code>argument</code>
     *    <tr>
     *       <td><code>null</code>
     *       <td><i>any</i>
     *       <td><code>argument.toString()</code>
     * </table>
     * <p>
     * If <code>pos</code> is non-null, and refers to
     * <code>Field.ARGUMENT</code>, the location of the first formatted
     * string will be returned.
     *
     * @param arguments an array of objects to be formatted and substituted.
     * @param result where text is appended.
     * @param pos On input: an alignment field, if desired.
     *            On output: the offsets of the alignment field.
     * @return the string buffer passed in as {@code result}, with formatted
     * text appended
     * @exception IllegalArgumentException if an argument in the
     *            <code>arguments</code> array is not of the type
     *            expected by the format element(s) that use it.
     */
    public final StringBuffer format(Object[] arguments, StringBuffer result,
            FieldPosition pos) {
        return subformat(arguments, result, pos, null);
    }

    /**
     * Creates a PatternFormat with the given pattern and uses it
     to format the given arguments. This is equivalent to
     * <blockquote>
     *     <code>(new {@link #TextFormat(String) PatternFormat}(pattern)).{@link #format(java.lang.Object[], java.lang.StringBuffer, java.text.FieldPosition) format}(arguments, new StringBuffer(), null).toString()</code>
     * </blockquote>
     *
     * @param pattern   the pattern string
     * @param arguments object(s) to format
     * @return the formatted string
     * @exception IllegalArgumentException if the pattern is invalid,
     *            or if an argument in the <code>arguments</code> array
     *            is not of the type expected by the format element(s)
     *            that use it.
     */
    public static String format(String pattern, Object... arguments) {
        PatternFormat temp = new PatternFormat(pattern);
        return temp.format(arguments);
    }

    // Overrides
    /**
     * Formats an array of objects and appends the <code>PatternFormat</code>'s
     * pattern, with format elements replaced by the formatted objects, to the
     * provided <code>StringBuffer</code>.
     * This is equivalent to
     * <blockquote>
     *     <code>{@link #format(java.lang.Object[], java.lang.StringBuffer, java.text.FieldPosition) format}((Object[]) arguments, result, pos)</code>
     * </blockquote>
     *
     * @param arguments an array of objects to be formatted and substituted.
     * @param result where text is appended.
     * @param pos On input: an alignment field, if desired.
     *            On output: the offsets of the alignment field.
     * @exception IllegalArgumentException if an argument in the
     *            <code>arguments</code> array is not of the type
     *            expected by the format element(s) that use it.
     */
    public final StringBuffer format(Object arguments, StringBuffer result,
            FieldPosition pos) {
        return subformat((Object[]) arguments, result, pos, null);
    }

    /**
     * Parses the string.
     *
     * <p>Caveats: The parse may fail in a number of circumstances.
     * For example:
     * <ul>
     * <li>If one of the arguments does not occur in the pattern.
     * <li>If the format of an argument loses information, such as
     *     with a choice format where a large number formats to "many".
     * <li>Does not yet handle recursion (where
     *     the substituted strings contain {n} references.)
     * <li>Will not always find a match (or the correct match)
     *     if some part of the parse is ambiguous.
     *     For example, if the pattern "{1},{2}" is used with the
     *     string arguments {"a,b", "c"}, it will format as "a,b,c".
     *     When the result is parsed, it will return {"a", "b,c"}.
     * <li>If a single argument is parsed more than once in the string,
     *     then the later parse wins.
     * </ul>
     * When the parse fails, use ParsePosition.getErrorIndex() to find out
     * where in the string the parsing failed.  The returned error
     * index is the starting offset of the sub-patterns that the string
     * is comparing with.  For example, if the parsing string "AAA {0} BBB"
     * is comparing against the pattern "AAD {0} BBB", the error index is
     * 0. When an error occurs, the call to this method will return null.
     * If the source is null, return an empty array.
     *
     * @param source the string to parse
     * @param pos    the parse position
     * @return an array of parsed objects
     */
    public Object[] parse(String source, ParsePosition pos) {
        if (source == null) {
            Object[] empty = {};
            return empty;
        }

        int maximumArgumentNumber = -1;
        for (int i = 0; i <= maxOffset; i++) {
            if (argumentNumbers[i] > maximumArgumentNumber) {
                maximumArgumentNumber = argumentNumbers[i];
            }
        }
        Object[] resultArray = new Object[maximumArgumentNumber + 1];

        int patternOffset = 0;
        int sourceOffset = pos.getIndex();
        ParsePosition tempStatus = new ParsePosition(0);
        for (int i = 0; i <= maxOffset; ++i) {
            // match up to format
            int len = offsets[i] - patternOffset;
            if (len == 0 || pattern.regionMatches(patternOffset,
                    source, sourceOffset, len)) {
                sourceOffset += len;
                patternOffset += len;
            } else {
                pos.setErrorIndex(sourceOffset);
                return null; // leave index as is to signal error
            }

            // now use format
            if (formats[i] == null) {   // string format
                // if at end, use longest possible match
                // otherwise uses first match to intervening string
                // does NOT recursively try all possibilities
                int tempLength = (i != maxOffset) ? offsets[i + 1] : pattern.length();

                int next;
                if (patternOffset >= tempLength) {
                    next = source.length();
                } else {
                    next = source.indexOf(pattern.substring(patternOffset, tempLength),
                            sourceOffset);
                }

                if (next < 0) {
                    pos.setErrorIndex(sourceOffset);
                    return null; // leave index as is to signal error
                } else {
                    String strValue = source.substring(sourceOffset, next);
                    if (!strValue.equals("{" + argumentNumbers[i] + "}")) {
                        resultArray[argumentNumbers[i]]
                                = source.substring(sourceOffset, next);
                    }
                    sourceOffset = next;
                }
            } else {
                tempStatus.setIndex(sourceOffset);
                resultArray[argumentNumbers[i]]
                        = formats[i].parseObject(source, tempStatus);
                if (tempStatus.getIndex() == sourceOffset) {
                    pos.setErrorIndex(sourceOffset);
                    return null; // leave index as is to signal error
                }
                sourceOffset = tempStatus.getIndex(); // update
            }
        }
        int len = pattern.length() - patternOffset;
        if (len == 0 || pattern.regionMatches(patternOffset,
                source, sourceOffset, len)) {
            pos.setIndex(sourceOffset + len);
        } else {
            pos.setErrorIndex(sourceOffset);
            return null; // leave index as is to signal error
        }
        return resultArray;
    }

    /**
     * Parses text from the beginning of the given string to produce an object
     * array.
     * The method may not use the entire text of the given string.
     * <p>
     * See the {@link #parse(String, ParsePosition)} method for more information
     * on message parsing.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return An <code>Object</code> array parsed from the string.
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    public Object[] parse(String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Object[] result = parse(source, pos);
        if (pos.getIndex() == 0) // unchanged, returned object is null
        {
            throw new ParseException("TextFormat parse error!", pos.getErrorIndex());
        }

        return result;
    }

    /**
     * Parses text from a string to produce an object array.
     * <p>
     * The method attempts to parse text starting at the index given by
     * <code>pos</code>.
     * If parsing succeeds, then the index of <code>pos</code> is updated
     * to the index after the last character used (parsing does not necessarily
     * use all characters up to the end of the string), and the parsed
     * object array is returned. The updated <code>pos</code> can be used to
     * indicate the starting point for the next call to this method.
     * If an error occurs, then the index of <code>pos</code> is not
     * changed, the error index of <code>pos</code> is set to the index of
     * the character where the error occurred, and null is returned.
     * <p>
     * See the {@link #parse(String, ParsePosition)} method for more information
     * on message parsing.
     *
     * @param source A <code>String</code>, part of which should be parsed.
     * @param pos A <code>ParsePosition</code> object with index and error
     *            index information as described above.
     * @return An <code>Object</code> array parsed from the string. In case of
     *         error, returns null.
     * @exception NullPointerException if <code>pos</code> is null.
     */
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     */
    public Object clone() {
        PatternFormat other = (PatternFormat) super.clone();

        // clone arrays. Can't do with utility because of bug in Cloneable
        other.formats = formats.clone(); // shallow clone
        for (int i = 0; i < formats.length; ++i) {
            if (formats[i] != null) {
                other.formats[i] = (Format) formats[i].clone();
            }
        }
        // for primitives or immutables, shallow clone is enough
        other.offsets = offsets.clone();
        other.argumentNumbers = argumentNumbers.clone();

        return other;
    }

    /**
     * Equality comparison between two message format objects
     */
    public boolean equals(Object obj) {
        if (this == obj) // quick check
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PatternFormat other = (PatternFormat) obj;
        return (maxOffset == other.maxOffset
                && pattern.equals(other.pattern)
                && ((locale != null && locale.equals(other.locale))
                || (locale == null && other.locale == null))
                && Arrays.equals(offsets, other.offsets)
                && Arrays.equals(argumentNumbers, other.argumentNumbers)
                && Arrays.equals(formats, other.formats));
    }

    /**
     * Generates a hash code for the message format object.
     */
    public int hashCode() {
        return pattern.hashCode(); // enough for reasonable distribution
    }

    /**
     * Defines constants that are used as attribute keys in the
     * <code>AttributedCharacterIterator</code> returned
     * from <code>PatternFormat.formatToCharacterIterator</code>.
     *
     * @since 1.4
     */
    public static class Field extends Format.Field {

        // Proclaim serial compatibility with 1.4 FCS
        private static final long serialVersionUID = 7899943957617360810L;

        /**
         * Creates a Field with the specified name.
         *
         * @param name Name of the attribute
         */
        protected Field(String name) {
            super(name);
        }

        /**
         * Resolves instances being deserialized to the predefined constants.
         *
         * @throws InvalidObjectException if the constant could not be
         *         resolved.
         * @return resolved PatternFormat.Field constant
         */
        protected Object readResolve() throws InvalidObjectException {
            if (this.getClass() != PatternFormat.Field.class) {
                throw new InvalidObjectException("subclass didn't correctly implement readResolve");
            }

            return ARGUMENT;
        }

        //
        // The constants
        //
        /**
         * Constant identifying a portion of a message that was generated
         * from an argument passed into <code>formatToCharacterIterator</code>.
         * The value associated with the key will be an <code>Integer</code>
         * indicating the index in the <code>arguments</code> array of the
         * argument from which the text was generated.
         */
        public final static Field ARGUMENT
                = new Field("message argument field");
    }

    // ===========================privates============================
    /**
     * The locale to use for formatting numbers and dates.
     * @serial
     */
    private Locale locale;

    /**
     * The string that the formatted values are to be plugged into.  In other words, this
     * is the pattern supplied on construction with all of the {} expressions taken out.
     * @serial
     */
    private String pattern = "";

    /** The initially expected number of subformats in the format */
    private static final int INITIAL_FORMATS = 10;

    /**
     * An array of formatters, which are used to format the arguments.
     * @serial
     */
    private Format[] formats = new Format[INITIAL_FORMATS];

    /**
     * The positions where the results of formatting each argument are to be inserted
     * into the pattern.
     * @serial
     */
    private int[] offsets = new int[INITIAL_FORMATS];

    /**
     * The argument numbers corresponding to each formatter.  (The formatters are stored
     * in the order they occur in the pattern, not in the order in which the arguments
     * are specified.)
     * @serial
     */
    private int[] argumentNumbers = new int[INITIAL_FORMATS];

    /**
     * One less than the number of entries in <code>offsets</code>.  Can also be thought of
     * as the index of the highest-numbered element in <code>offsets</code> that is being used.
     * All of these arrays should have the same number of elements being used as <code>offsets</code>
     * does, and so this variable suffices to tell us how many entries are in all of them.
     * @serial
     */
    private int maxOffset = -1;

    /**
     * Internal routine used by format. If <code>characterIterators</code> is
     * non-null, AttributedCharacterIterator will be created from the
     * subformats as necessary. If <code>characterIterators</code> is null
     * and <code>fp</code> is non-null and identifies
     * <code>Field.MESSAGE_ARGUMENT</code>, the location of
     * the first replaced argument will be set in it.
     *
     * @exception IllegalArgumentException if an argument in the
     *            <code>arguments</code> array is not of the type
     *            expected by the format element(s) that use it.
     */
    private StringBuffer subformat(Object[] arguments, StringBuffer result,
            FieldPosition fp, List<AttributedCharacterIterator> characterIterators) {
        // note: this implementation assumes a fast substring & index.
        // if this is not true, would be better to append chars one by one.
        int lastOffset = 0;
        int last = result.length();
        for (int i = 0; i <= maxOffset; ++i) {
            result.append(pattern.substring(lastOffset, offsets[i]));
            lastOffset = offsets[i];
            int argumentNumber = argumentNumbers[i];
            if (arguments == null || argumentNumber >= arguments.length) {
                result.append('{').append(argumentNumber).append('}');
                continue;
            }
            // int argRecursion = ((recursionProtection >> (argumentNumber*2)) & 0x3);
            if (false) { // if (argRecursion == 3){
                // prevent loop!!!
                result.append('\uFFFD');
            } else {
                Object obj = arguments[argumentNumber];
                String arg = null;
                Format subFormatter = null;
                if (obj == null) {
                    arg = "null";
                } else if (formats[i] != null) {
                    subFormatter = formats[i];
                    if (subFormatter instanceof ChoiceFormat) {
                        arg = formats[i].format(obj);
                        if (arg.indexOf('{') >= 0) {
                            subFormatter = new PatternFormat(arg, locale);
                            obj = arguments;
                            arg = null;
                        }
                    }
                } else if (obj instanceof Number) {
                    // format number if can
                    subFormatter = NumberFormat.getInstance(locale);
                } else if (obj instanceof Date) {
                    // format a Date if can
                    subFormatter = DateFormat.getDateTimeInstance(
                            DateFormat.SHORT, DateFormat.SHORT, locale);//fix
                } else if (obj instanceof String) {
                    arg = (String) obj;

                } else {
                    arg = obj.toString();
                    if (arg == null) {
                        arg = "null";
                    }
                }

                // At this point we are in two states, either subFormatter
                // is non-null indicating we should format obj using it,
                // or arg is non-null and we should use it as the value.
                if (characterIterators != null) {
                    // If characterIterators is non-null, it indicates we need
                    // to get the CharacterIterator from the child formatter.
                    if (last != result.length()) {
                        characterIterators.add(
                                createAttributedCharacterIterator(result.substring(last)));
                        last = result.length();
                    }
                    if (subFormatter != null) {
                        AttributedCharacterIterator subIterator
                                = subFormatter.formatToCharacterIterator(obj);

                        append(result, subIterator);
                        if (last != result.length()) {
                            characterIterators.add(
                                    createAttributedCharacterIterator(
                                            subIterator, Field.ARGUMENT,
                                            Integer.valueOf(argumentNumber)));
                            last = result.length();
                        }
                        arg = null;
                    }
                    if (arg != null && arg.length() > 0) {
                        result.append(arg);
                        characterIterators.add(
                                createAttributedCharacterIterator(
                                        arg, Field.ARGUMENT,
                                        Integer.valueOf(argumentNumber)));
                        last = result.length();
                    }
                } else {
                    if (subFormatter != null) {
                        arg = subFormatter.format(obj);
                    }
                    last = result.length();
                    result.append(arg);
                    if (i == 0 && fp != null && Field.ARGUMENT.equals(
                            fp.getFieldAttribute())) {
                        fp.setBeginIndex(last);
                        fp.setEndIndex(result.length());
                    }
                    last = result.length();
                }
            }
        }
        result.append(pattern.substring(lastOffset, pattern.length()));
        if (characterIterators != null && last != result.length()) {
            characterIterators.add(createAttributedCharacterIterator(
                    result.substring(last)));
        }
        return result;
    }

    /**
     * Convenience method to append all the characters in
     * <code>iterator</code> to the StringBuffer <code>result</code>.
     */
    private void append(StringBuffer result, CharacterIterator iterator) {
        if (iterator.first() != CharacterIterator.DONE) {
            char aChar;

            result.append(iterator.first());
            while ((aChar = iterator.next()) != CharacterIterator.DONE) {
                result.append(aChar);
            }
        }
    }

    // Indices for segments
    private static final int SEG_RAW = 0;
    private static final int SEG_INDEX = 1;
    private static final int SEG_TYPE = 2;
    private static final int SEG_MODIFIER = 3; // modifier or subformat


    private void makeFormat(int position, int offsetNumber,
            StringBuilder[] textSegments) {
        String[] segments = new String[textSegments.length];
        for (int i = 0; i < textSegments.length; i++) {
            StringBuilder oneseg = textSegments[i];
            segments[i] = (oneseg != null) ? oneseg.toString() : "";
        }

        // get the argument number
        int argumentNumber;
        try {
            argumentNumber = Integer.parseInt(segments[SEG_INDEX]); // always unlocalized!
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("can't parse argument number: "
                    + segments[SEG_INDEX], e);
        }
        if (argumentNumber < 0) {
            throw new IllegalArgumentException("negative argument number: "
                    + argumentNumber);
        }

        // resize format information arrays if necessary
        if (offsetNumber >= formats.length) {
            int newLength = formats.length * 2;
            Format[] newFormats = new Format[newLength];
            int[] newOffsets = new int[newLength];
            int[] newArgumentNumbers = new int[newLength];
            System.arraycopy(formats, 0, newFormats, 0, maxOffset + 1);
            System.arraycopy(offsets, 0, newOffsets, 0, maxOffset + 1);
            System.arraycopy(argumentNumbers, 0, newArgumentNumbers, 0, maxOffset + 1);
            formats = newFormats;
            offsets = newOffsets;
            argumentNumbers = newArgumentNumbers;
        }
        int oldMaxOffset = maxOffset;
        maxOffset = offsetNumber;
        offsets[offsetNumber] = segments[SEG_RAW].length();
        argumentNumbers[offsetNumber] = argumentNumber;

        // now get the format
        Format newFormat = null;
        if (segments[SEG_TYPE].length() != 0) {
            String type = segments[SEG_TYPE];
            String modifier = null;
            if (segments[SEG_MODIFIER] != null) {
                modifier = segments[SEG_MODIFIER];
            }
            newFormat = factory.apply(type,modifier);
            formats[offsetNumber] = newFormat;
        }
    }

    private static final void copyAndFixQuotes(String source, int start, int end,
            StringBuilder target) {
        boolean quoted = false;

        for (int i = start; i < end; ++i) {
            char ch = source.charAt(i);
            if (ch == '{') {
                if (!quoted) {
                    target.append('\'');
                    quoted = true;
                }
                target.append(ch);
            } else if (ch == '\'') {
                target.append("''");
            } else {
                if (quoted) {
                    target.append('\'');
                    quoted = false;
                }
                target.append(ch);
            }
        }
        if (quoted) {
            target.append('\'');
        }
    }

    /**
     * After reading an object from the input stream, do a simple verification
     * to maintain class invariants.
     * @throws InvalidObjectException if the objects read from the stream is invalid.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        boolean isValid = maxOffset >= -1
                && formats.length > maxOffset
                && offsets.length > maxOffset
                && argumentNumbers.length > maxOffset;
        if (isValid) {
            int lastOffset = pattern.length() + 1;
            for (int i = maxOffset; i >= 0; --i) {
                if ((offsets[i] < 0) || (offsets[i] > lastOffset)) {
                    isValid = false;
                    break;
                } else {
                    lastOffset = offsets[i];
                }
            }
        }
        if (!isValid) {
            throw new InvalidObjectException("Could not reconstruct TextFormat from corrupt stream.");
        }
    }

    /**
     * Creates an <code>AttributedCharacterIterator</code> for the String
     * <code>s</code>.
     *
     * @param s String to create AttributedCharacterIterator from
     * @return AttributedCharacterIterator wrapping s
     */
    AttributedCharacterIterator createAttributedCharacterIterator(String s) {
        AttributedString as = new AttributedString(s);

        return as.getIterator();
    }

    /**
     * Returns an AttributedCharacterIterator with the String
     * <code>string</code> and additional key/value pair <code>key</code>,
     * <code>value</code>.
     *
     * @param string String to create AttributedCharacterIterator from
     * @param key Key for AttributedCharacterIterator
     * @param value Value associated with key in AttributedCharacterIterator
     * @return AttributedCharacterIterator wrapping args
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
            String string, AttributedCharacterIterator.Attribute key,
            Object value) {
        AttributedString as = new AttributedString(string);

        as.addAttribute(key, value);
        return as.getIterator();
    }

    /**
     * Creates an AttributedCharacterIterator with the contents of
     * <code>iterator</code> and the additional attribute <code>key</code>
     * <code>value</code>.
     *
     * @param iterator Initial AttributedCharacterIterator to add arg to
     * @param key Key for AttributedCharacterIterator
     * @param value Value associated with key in AttributedCharacterIterator
     * @return AttributedCharacterIterator wrapping args
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
            AttributedCharacterIterator iterator,
            AttributedCharacterIterator.Attribute key, Object value) {
        AttributedString as = new AttributedString(iterator);

        as.addAttribute(key, value);
        return as.getIterator();
    }
}
