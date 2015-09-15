/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jhotdraw.io.StreamPosTokenizer;

/**
 * Converts an object array from or to a string representation.
 * <p>
 * The string representation must be described using a pattern.
 * <p>
 * The pattern has the following format:
 * <pre>
 * <i>TextFormatPattern:</i>
 *       <i>PatternElement</i>
 *       <i>PatternElement TextFormatPattern</i>
 *
 * <i>PatternElement:</i>
 *       <i>Regex</i>
 *       <i>Argument</i>
 *
 * <i>Argument:</i>
 *       { <i>ArgumentIndex</i> }
 *       { <i>ArgumentIndex</i> , <i>FormatType</i> }
 *       { <i>ArgumentIndex</i> , <i>FormatType</i> , <i>FormatStyle</i> }
 *
 * <i>Regex:</i>
 *       <i>RegexChars RegexRepeat</i>
 *       <i>RegexCharclass RegexRepeat</i>
 *       <i>RegexChoice RegexRepeat</i>
 *
 * <i>RegexChars:</i>
 *       <i>Char</i>
 *       <i>QuotedChars</i>
 *
 * <i>RegexRepeat:</i>
 *       *
 *       +
 *       <i>empty</i>
 *
 * <i>RegexCharclass:</i>
 *       [ <i>Chars</i> ]
 *
 * <i>RegexChoice:</i>
 *       ( <i>RegexChoiceList</i> )
 *
 * <i>RegexChoiceList</i>
 *       <i>Regex</i>
 *       <i>Regex</i> | <i>RegexChoiceList</i>
 *
 * <i>FormatType:</i>
 *       list
 *       choice
 *       <i>FormatTypeSupportedByConverterFactory</i>
 *
 * <i>FormatStyle:</i>
 *       <i>ListPattern</i>
 *       <i>ChoicePattern</i>
 *       <i>FormatStyleSupportedByConverterFactory</i>
 *
 * <i>ListPattern:</i>
 *       <i>ItemPattern | SeparatorPattern </i>
 *
 * <i>ItemPattern:</i>
 * <i>SeparatorPattern:</i>
 *       <i>TextFormatPattern</i>
 *
 * <i>ChoicePattern:</i>
 *       <i>Limit</i> # <i>TextFormatPattern</i>
 *       <i>Limit</i> # <i>TextFormatPattern</i> | <i>ChoicePattern</i>
 *
 * <i>Limit: a double number</i>
 * </pre>
 * <p>
 * Within a {@code String}, a pair of single quotes can be used to quote any
 * arbitrary characters except single quotes. For example, pattern string
 * {@code "'{0}'"} represents string {@code "{0}"}, not a FormatElement. A
 * single quote itself must be represented by doubled single quotes {@code ''}.
 * <p>
 * Any curly braces within {@code FormatType} and {@code FormatStyle} must be
 * balanced.
 * </p>
 * <p>
 * The {@code ChoicePattern} works like in {@code java.text.ChoiceFormat}.
 * The choice is specified with an ascending list of doubles, where each item
 * specifies a half-open interval up to the next item:
 * </p>
 * <pre>
 * X matches j if and only if limit[j] ≤ X &lt; limit[j+1]
 * </pre>
 * <p>
 * If there is no match, then either the first or last index is used, depending
 * on whether the number (X) is too low or too high. If the limit array is not
 * in ascending order, the results of formatting will be incorrect. ChoiceFormat
 * also accepts {@code \u221E} as equivalent to infinity(INF).
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PatternConverter implements Converter<Object[]> {

    private AST ast;
    private ConverterFactory factory;

    public PatternConverter(String pattern, ConverterFactory factory) {
        try {
            ast = parseTextFormatPattern(pattern);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Illegal pattern", ex);
        }

        this.factory = factory;
    }

    @Override
    public void toString(Object[] value, Appendable out) throws IOException {
        System.out.println("MaxArgumentIndex:"
                + ast.getMaxArgumentIndex(value, new ArgumentOffset()));

        ast.toString(value, out, factory, new ArgumentOffset());
    }

    @Override
    public Object[] fromString(CharBuffer buf) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class ArgumentOffset {

        int offset;

        public ArgumentOffset() {
            this(0);
        }

        public ArgumentOffset(int offset) {
            this.offset = offset;
        }

    }

    /** Pattern AST.
     * This class is only public for testing purposes.
     */
    public static class AST {

        protected List<AST> children = new ArrayList<>();

        @Override
        public String toString() {
            return "AST{" + children + '}';
        }

        public void toString(Object[] value, Appendable out, ConverterFactory factory, ArgumentOffset argumentOffset) throws IOException {
            for (AST child : children) {
                child.toString(value, out, factory, argumentOffset);
            }
        }

        public int getMaxArgumentIndex(Object[] value, ArgumentOffset argumentOffset) {
            int index = -1;
            for (AST child : children) {
                index = Math.max(index, argumentOffset.offset
                        + child.getMaxArgumentIndex(value, argumentOffset));
            }
            return index;
        }
    }

    public static class Argument extends AST {

        protected int index;
    }

    public static class SimpleArgument extends Argument {

        protected String type;
        protected String style;
        protected Converter converter;

        @Override
        public String toString() {
            return "SimpleArgument{index='" + index + "' type=" + type
                    + " style=" + style + '}';
        }

        @Override
        public void toString(Object[] value, Appendable out, ConverterFactory factory, ArgumentOffset argumentOffset) throws IOException {
            if (converter == null) {
                converter = factory.apply(type, style);
            }
            converter.toString(value[index + argumentOffset.offset], out);
        }

        @Override
        public int getMaxArgumentIndex(Object[] value, ArgumentOffset argumentOffset) {
            return index + argumentOffset.offset;
        }
    }

    /** Each child represents a choice. */
    public static class ChoiceArgument extends Argument {

        protected ArrayList<Double> limits;

        @Override
        public String toString() {
            return "ChoiceArgument{index='" + index + "' limits=" + limits
                    + "' children=" + children
                    + '}';
        }

        @Override
        public void toString(Object[] value, Appendable out, ConverterFactory factory, ArgumentOffset argumentOffset) throws IOException {
            int choiceIndex = Collections.binarySearch(limits, ((Number) value[index]).doubleValue());
            if (choiceIndex < 0) {
                choiceIndex = 0;
            } else if (choiceIndex >= limits.size()) {
                choiceIndex = limits.size() - 1;
            }

            children.get(choiceIndex).toString(value, out, factory, argumentOffset);
        }

        @Override
        public int getMaxArgumentIndex(Object[] value, ArgumentOffset argumentOffset) {
            int i = index + argumentOffset.offset;
            for (AST child : children) {
                i = Math.max(i, child.getMaxArgumentIndex(value, argumentOffset));
            }
            return i;
        }
    }

    /** First child represents item, second child represents separator. */
    public static class ListArgument extends Argument {

        @Override
        public String toString() {
            return "ListArgument{index='" + index + "' children=" + children
                    + '}';
        }

        @Override
        public int getMaxArgumentIndex(Object[] value, ArgumentOffset argumentOffset) {
            int i = index + argumentOffset.offset;

            int childi = -1;
            AST child = children.get(0);
            childi = Math.max(childi, child.getMaxArgumentIndex(value, argumentOffset));

            int repeat = ((Number) value[i]).intValue();
            int step = (childi - i);
            System.out.println("ListArgument #=" + value.length + " argOffs="
                    + argumentOffset.offset + " i=" + i + " repeat=" + repeat
                    + " step=" + step + " childi=" + childi);

            argumentOffset.offset = i + repeat * step;

            return i + repeat * step;
        }

        @Override
        public void toString(Object[] value, Appendable out, ConverterFactory factory, ArgumentOffset argumentOffset) throws IOException {
            int i = index + argumentOffset.offset;

            AST separator = children.get(1);

            int childi = -1;
            AST child = children.get(0);
            childi = Math.max(childi, child.getMaxArgumentIndex(value, argumentOffset));

            int repeat = ((Number) value[i]).intValue();
            int step = (childi - i);
            System.out.println("ListArgument #=" + value.length + " argOffs="
                    + argumentOffset.offset + " i=" + i + " repeat=" + repeat
                    + " step=" + step);

            for (int j = 0; j < repeat; j++) {
                if (j != 0) {
                    separator.toString(value, out, factory, new ArgumentOffset(0));
                }
                child.toString(value, out, factory, new ArgumentOffset(i + j
                        * step));
            }

            argumentOffset.offset = i + repeat * step;
        }
    }

    public static class Regex extends AST {

        protected int maxRepeat;
        protected int minRepeat;
    }

    public static class RegexChars extends Regex {

        protected String chars;

        @Override
        public String toString() {
            return "RegexChars{chars='" + chars + "' repeat=" + minRepeat + ".."
                    + maxRepeat + '}';
        }

        @Override
        public void toString(Object[] value, Appendable out, ConverterFactory factory, ArgumentOffset argumentOffset) throws IOException {
            for (int i = 0; i < minRepeat; i++) {
                out.append(chars);
            }
        }
    }

    public static class RegexCharclass extends Regex {

        protected String chars;

        @Override
        public String toString() {
            return "RegexCharclass{chars='" + chars + "' repeat=" + minRepeat
                    + ".." + maxRepeat + '}';
        }

        @Override
        public void toString(Object[] value, Appendable out, ConverterFactory factory, ArgumentOffset argumentOffset) throws IOException {
            for (int i = 0; i < minRepeat; i++) {
                out.append(chars.charAt(0));
            }
        }
    }

    public static class RegexChoice extends Regex {

        @Override
        public String toString() {
            return "RegexChoice{children='" + children + "' repeat=" + minRepeat
                    + ".." + maxRepeat + '}';
        }

        @Override
        public void toString(Object[] value, Appendable out, ConverterFactory factory, ArgumentOffset argumentOffset) throws IOException {
            for (int i = 0; i < minRepeat; i++) {
                children.get(0).toString(value, out, factory, argumentOffset);
            }
        }
    }

    public static AST parseTextFormatPattern(String pattern) throws IOException {
        return parseTextFormatPattern(pattern, new AST(), 0);
    }

    private static AST parseTextFormatPattern(String pattern, AST parent, int offset) throws IOException {
        StreamPosTokenizer tt = new StreamPosTokenizer(new StringReader(pattern));
        tt.resetSyntax();
        tt.quoteChar('\'');

        parseTextFormatPattern(tt, parent, offset);
        return parent;
    }

    private static void parseTextFormatPattern(StreamPosTokenizer tt, AST parent, int offset) throws IOException {
        while (tt.nextToken() != StreamPosTokenizer.TT_EOF) {
            tt.pushBack();
            parsePatternElement(tt, parent, offset);
        }
    }

    private static void parsePatternElement(StreamPosTokenizer tt, AST parent, int offset) throws IOException {
        switch (tt.nextToken()) {
            case StreamPosTokenizer.TT_EOF:
                return;
            case '{':
                tt.pushBack();
                parseArgument(tt, parent, offset);
                break;
            default:
                tt.pushBack();
                parseRegex(tt, parent, offset);
                break;
        }
    }

    private static void parseRegex(StreamPosTokenizer tt, AST parent, int offset) throws IOException {
        switch (tt.nextToken()) {
            case StreamPosTokenizer.TT_EOF:
                throw new IOException("RegexExpression expected @"
                        + (tt.getStartPosition() + offset));
            case '\'': {
                RegexChars regex = new RegexChars();
                regex.chars = (tt.sval.isEmpty()) ? "\'" : tt.sval;
                parseRegexRepeat(tt, regex, offset);
                parent.children.add(regex);
            }
            break;
            case '(':
                tt.pushBack();
                parseRegexChoice(tt, parent, offset);
                break;
            case '[':
                tt.pushBack();
                parseRegexCharclass(tt, parent, offset);
                break;
            case '+':
            case '*':
            case ')':
            case ']':
                throw new IOException("RegexExpression may not start with '"
                        + (char) tt.ttype + "' @"
                        + (tt.getStartPosition() + offset));

            default: {
                RegexChars regex = new RegexChars();
                regex.chars = String.valueOf((char) tt.ttype);
                parseRegexRepeat(tt, regex, offset);
                parent.children.add(regex);
            }
            break;
        }
    }

    private static void parseRegexRepeat(StreamPosTokenizer tt, Regex regex, int offset) throws IOException {
        switch (tt.nextToken()) {
            case '+':
                regex.minRepeat = 1;
                regex.maxRepeat = Integer.MAX_VALUE;
                break;
            case '*':
                regex.minRepeat = 0;
                regex.maxRepeat = Integer.MAX_VALUE;
                break;
            default:
                regex.minRepeat = 1;
                regex.maxRepeat = 1;
                tt.pushBack();
                break;
        }
    }

    private static void parseRegexChoice(StreamPosTokenizer tt, AST parent, int offset) throws IOException {
        RegexChoice regex = new RegexChoice();
        if (tt.nextToken() != '(') {
            throw new IOException("RegexChoice '(' expected @"
                    + (tt.getStartPosition() + offset));
        }
        do {
            parseRegex(tt, regex, offset);
        } while (tt.nextToken() == '|');
        if (tt.ttype != ')') {
            throw new IOException("RegexChoice ')' expected @"
                    + (tt.getStartPosition() + offset));
        }
        parseRegexRepeat(tt, regex, offset);
        parent.children.add(regex);
    }

    private static void parseRegexCharclass(StreamPosTokenizer tt, AST parent, int offset) throws IOException {
        RegexCharclass regex = new RegexCharclass();
        if (tt.nextToken() != '[') {
            throw new IOException("RegexCharclass '[' expected @"
                    + (tt.getStartPosition() + offset));
        }
        regex.chars = "";
        while (tt.nextToken() != ']') {
            tt.pushBack();
            switch (tt.nextToken()) {
                case StreamPosTokenizer.TT_EOF:
                    throw new IOException("RegexCharclass character expected @"
                            + (tt.getStartPosition() + offset));
                case '\'':
                    regex.chars += (tt.sval.isEmpty()) ? "\'" : tt.sval;
                default:
                    regex.chars += (char) tt.ttype;
                    break;
            }
        }
        if (regex.chars.isEmpty()) {
            throw new IOException("RegexCharclass illegal empty character class @"
                    + (tt.getStartPosition() + offset));

        }
        parseRegexRepeat(tt, regex, offset);
        parent.children.add(regex);
    }

    private static void parseArgument(StreamPosTokenizer tt, AST parent, int offset) throws IOException {
        RegexChoice regex = new RegexChoice();
        if (tt.nextToken() != '{') {
            throw new IOException("Argument '{' expected @"
                    + (tt.getStartPosition() + offset));
        }
        if (tt.nextToken() == '}') {
            throw new IOException("Argument unexpected '}' expected @"
                    + (tt.getStartPosition() + offset));
        }
        tt.pushBack();

        // parse argument index
        int index = 0;
        while (tt.nextToken() != ',' && tt.ttype != '}' && tt.ttype
                != StreamPosTokenizer.TT_EOF) {

            switch (tt.ttype) {
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
                    index = index * 10 + tt.ttype - '0';
                    break;
            }
        }
        // parse argument type
        StringBuilder type = new StringBuilder();
        if (tt.ttype == ',') {
            while (tt.nextToken() != ',' && tt.ttype != '}' && tt.ttype
                    != StreamPosTokenizer.TT_EOF) {

                switch (tt.ttype) {
                    case '\'':
                        type.append((tt.sval.isEmpty()) ? "\'" : tt.sval);
                        break;
                    default:
                        type.append((char) tt.ttype);
                        break;
                }
            }
        }
        if (tt.ttype != ',') {
            tt.pushBack();
        }
        String typeStr = type.toString();

        switch (typeStr) {
            case "choice":
                parseChoiceArgumentStyle(tt, parent, index, typeStr, offset);
                break;
            case "list":
                parseListArgumentStyle(tt, parent, index, typeStr, offset);
                break;
            default:
                parseSimpleArgumentStyle(tt, parent, index, typeStr, offset);
                break;
        }
    }

    private static void parseSimpleArgumentStyle(StreamPosTokenizer tt, AST parent, int index, String type, int offset) throws IOException {
        // parse argument style
        StringBuilder style = new StringBuilder();

        // parse item
        int depth = 0;
        while ((tt.nextToken() != '}' || depth > 0) && tt.ttype
                != StreamPosTokenizer.TT_EOF) {

            switch (tt.ttype) {
                case '\'':
                    style.append((tt.sval.isEmpty()) ? "\'" : tt.sval);
                    break;
                case '{':
                    style.append('{');
                    depth++;
                    break;
                case '}':
                    style.append('}');
                    depth--;
                    break;
                default:
                    style.append((char) tt.ttype);
                    break;
            }
        }

        SimpleArgument argument = new SimpleArgument();
        argument.index = index;
        argument.type = type;
        argument.style = style.toString();
        parent.children.add(argument);
    }

    private static void parseChoiceArgumentStyle(StreamPosTokenizer tt, AST parent, int index, String type, int offset) throws IOException {
        ChoiceArgument argument = new ChoiceArgument();
        argument.index = index;
        argument.limits = new ArrayList<>();
        // parse argument style
        StringBuilder style = new StringBuilder();

        int depth = 0;
        while ((tt.nextToken() != '}' || depth > 0) && tt.ttype
                != StreamPosTokenizer.TT_EOF) {

            tt.pushBack();

            // parse limit
            int startPosition = tt.getStartPosition();
            while (((tt.nextToken() != '#' && tt.ttype != '}') || depth > 0)
                    && tt.ttype != StreamPosTokenizer.TT_EOF) {
                switch (tt.ttype) {
                    case '\'':
                        style.append((tt.sval.isEmpty()) ? "\'" : tt.sval);
                        break;
                    default:
                        style.append((char) tt.ttype);
                        break;
                }
            }
            try {
                double limit = Double.parseDouble(style.toString());
                argument.limits.add(limit);
            } catch (NumberFormatException e) {
                throw new IOException("Choice Arugment: Illegal number format for limit: '"
                        + style + "' @" + (startPosition + offset));
            }
            style.delete(0, style.length());
            if (tt.ttype != '#') {
                tt.pushBack();
            }

            // parse item
            while (((tt.nextToken() != '|' && tt.ttype != '}') || depth > 0)
                    && tt.ttype != StreamPosTokenizer.TT_EOF) {
                switch (tt.ttype) {
                    case '\'':
                        style.append((tt.sval.isEmpty()) ? "\'" : tt.sval);
                        break;
                    case '{':
                        style.append('{');
                        depth++;
                        break;
                    case '}':
                        style.append('}');
                        depth--;
                        break;
                    default:
                        style.append((char) tt.ttype);
                        break;
                }
            }
            AST child = new AST();
            argument.children.add(child);
            parseTextFormatPattern(style.toString(), child, offset
                    + tt.getEndPosition());
            style.delete(0, style.length());
            if (tt.ttype != '|') {
                tt.pushBack();
            }
        }

        parseTextFormatPattern(tt, argument, offset + tt.getEndPosition());
        parent.children.add(argument);
    }

    private static void parseListArgumentStyle(StreamPosTokenizer tt, AST parent, int index, String type, int offset) throws IOException {
        ListArgument argument = new ListArgument();
        argument.index = index;
        // parse argument style
        StringBuilder style = new StringBuilder();

        int depth = 0;
        while ((tt.nextToken() != '}' || depth > 0) && tt.ttype
                != StreamPosTokenizer.TT_EOF) {

            tt.pushBack();

            // parse item
            while (((tt.nextToken() != '|' && tt.ttype != '}') || depth > 0)
                    && tt.ttype != StreamPosTokenizer.TT_EOF) {
                switch (tt.ttype) {
                    case '\'':
                        style.append((tt.sval.isEmpty()) ? "\'" : tt.sval);
                        break;
                    case '{':
                        style.append('{');
                        depth++;
                        break;
                    case '}':
                        style.append('}');
                        depth--;
                        break;
                    default:
                        style.append((char) tt.ttype);
                        break;
                }
            }
            AST child = new AST();
            argument.children.add(child);
            parseTextFormatPattern(style.toString(), child, offset
                    + tt.getEndPosition());
            style.delete(0, style.length());
            if (tt.ttype != '|') {
                tt.pushBack();
            }
        }

        parent.children.add(argument);
    }

}
