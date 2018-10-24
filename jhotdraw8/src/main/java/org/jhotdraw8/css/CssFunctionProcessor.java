package org.jhotdraw8.css;

import org.jhotdraw8.css.text.CssDimension;
import org.jhotdraw8.io.DefaultUnitConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Takes a list of tokens and evaluates common Css functions.
 * <p>
 * Supported standard functions:
 * <dl>
 * <dt>attr()</dt><dd>Attribute Reference. Returns the value of an attribute on the element.</dd>
 * <dt>calc()</dt><dd>Mathematical Expressions. Returns the value of a mathematical expression.</dd>
 * </dl>
 * <p>
 * Supported non-standard functions:
 * <dl>
 * <dt>replace()</dt><dd>Replace. Replaces all substrings matching a regular expression in a string.</dd>
 * <dt>concat()</dt><dd>Concat. Concatenates strings.</dd>
 * </dl>
 *
 * <p>
 * References:
 * <ul>
 * <li>CSS Values and Units Module, Functional Notations
 * <a href="https://www.w3.org/TR/css-values-3/#functional-notations">w3.org</a></li>
 * </ul>
 * </p>
 *
 * @param <T> the element type
 */
public class CssFunctionProcessor<T> {
    private final SelectorModel<T> model;

    public CssFunctionProcessor(SelectorModel<T> model) {
        this.model = model;
    }

    public List<CssToken> process(T element, List<CssToken> in) throws ParseException {
        CssListTokenizer tt = new CssListTokenizer(in);
        ArrayList<CssToken> out = new ArrayList<>(in.size());
        try {
            process(element, tt, out::add);
        } catch (IOException e) {
            e.printStackTrace();
            out.clear();
            out.addAll(in);
        }
        return out;
    }

    public void process(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            tt.pushBack();
            processToken(element, tt, out);
        }
    }

    private void processToken(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        if (tt.nextNoSkip() == CssTokenType.TT_FUNCTION) {
            switch (tt.currentStringNonnull()) {
                case "attr":
                    tt.pushBack();
                    processAttrFunction(element, tt, out);
                    break;
                case "calc":
                    tt.pushBack();
                    processCalcFunction(element, tt, out);
                    break;
                case "replace":
                    tt.pushBack();
                    processReplaceFunction(element, tt, out);
                    break;
                case "concat":
                    tt.pushBack();
                    processConcatFunction(element, tt, out);
                    break;
                default:
                    tt.pushBack();
                    processUnknownFunction(element, tt, out);
                    break;
            }
        } else {
            out.accept(tt.getToken());
        }
    }

    /**
     * Processes the attr() function.
     * <pre>
     *     attr = "attr(" ,  s* , attr-name, s* , [ type-or-unit ] ,  s* , [ "," ,  s* , attr-fallback ] ,  s* , ")" ;
     *     attr-name = qualified-name;
     *     type-or-unit = "string" | "color" | "url" | "integer" | "number"
     *                   | ["%" ,] ( "length" | "angle" | "time" | "frequency" )
     *                   ;
     *     attr-fallback = ident-token;
     *
     *     qualified-name = [ [ ident-token ], "|" ] , ident-token ;
     * </pre>
     *
     * @param tt  the tokenizer
     * @param out the consumer
     * @throws IOException
     */
    private void processAttrFunction(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈attr〉: function attr() expected.");
        if (!"attr".equals(tt.currentString())) {
            throw new ParseException("〈attr〉: function attr() expected.", tt.getStartPosition());
        }
        int line = tt.getLineNumber();
        int start = tt.getStartPosition();

        String attrName = parseAttrName(tt);
        String typeOrUnit = null;

        List<CssToken> attrFallback = new ArrayList<>();
        if (tt.next() == CssTokenType.TT_PERCENT_DELIM) {
            if (tt.next() == CssTokenType.TT_IDENT) {
                typeOrUnit = "%" + tt.currentString();
            } else {
                typeOrUnit = "%";
                tt.pushBack();
            }
        } else if (tt.current() == CssTokenType.TT_IDENT) {
            typeOrUnit = tt.currentString();
        } else {
            tt.pushBack();
        }

        if (tt.next() == CssTokenType.TT_COMMA) {
            while (tt.next() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
                attrFallback.add(tt.getToken());
            }
        }
        if (tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            throw new ParseException("〈attr〉: right bracket expected. " + tt.current(), tt.getStartPosition());
        }
        int end = tt.getEndPosition();

        String attrValue = model.getAttributeAsString(element, attrName);
        if (attrValue != null && !attrValue.isEmpty()) {
            if (typeOrUnit == null) {
                typeOrUnit = "string";
            }
            CssStreamTokenizer att = new CssStreamTokenizer(attrValue);
            Outer:
            switch (typeOrUnit) {
                case "string":
                    String strValue;
                    switch (att.next()) {
                        case CssTokenType.TT_STRING:
                            strValue = att.currentValue();
                        case CssTokenType.TT_IDENT:
                            strValue = att.currentValue();
                            break;
                        case CssTokenType.TT_NUMBER:
                        case CssTokenType.TT_PERCENTAGE:
                        case CssTokenType.TT_DIMENSION:
                            strValue = att.getToken().fromToken();
                            break;
                        default:
                            strValue = null;
                            break;
                    }
                    if (strValue == null) {
                        throw new ParseException("〈attr〉: could not convert attribute value of attribute " + attrName + "=" + attrValue, tt.getStartPosition());
                    }
                    out.accept(new CssToken(CssTokenType.TT_STRING, strValue, null, line, start, end));
                    return;
                case "color":
                    if (attrValue.startsWith("#")) {
                        out.accept(new CssToken(CssTokenType.TT_IDENT, attrValue.substring(1), null, line, start, end));
                    }
                    return;
                case "url":
                    out.accept(new CssToken(CssTokenType.TT_FUNCTION, "url", null, line, start, end));
                    out.accept(new CssToken(CssTokenType.TT_STRING, attrValue, null, line, start, end));
                    out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET, null, null, line, start, end));
                    return;
                case "integer":
                    try {
                        out.accept(new CssToken(CssTokenType.TT_NUMBER, null, Integer.parseInt(attrValue, start), line, start, end));
                        return;
                    } catch (NumberFormatException e) {
                        break;
                    }
                case "number":
                    try {
                        out.accept(new CssToken(CssTokenType.TT_NUMBER, null, parseDimensionOrDouble(attrValue, start).getValue(), line, start, end));
                        return;
                    } catch (NumberFormatException e) {
                        break;
                    }
                case "length":
                case "angle":
                case "time":
                case "frequency":
                    try {
                        CssDimension dim = parseDimensionOrDouble(attrValue, start);
                        out.accept(new CssToken(CssTokenType.TT_DIMENSION, dim.getUnits(), dim.getValue(), line, start, end));
                        return;
                    } catch (NumberFormatException | ParseException e) {
                        break;
                    }
                case "%length":
                case "%angle":
                case "%time":
                case "%frequency":
                    try {
                        out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, "%", parseDimensionOrDouble(attrValue, start).getValue(), line, start, end));
                        return;
                    } catch (NumberFormatException e) {
                        break;
                    }
                default:
                    try {
                        out.accept(new CssToken(CssTokenType.TT_DIMENSION, typeOrUnit, parseDimensionOrDouble(attrValue, start).getValue(), line, start, end));
                        return;
                    } catch (NumberFormatException e) {
                        break;
                    }
            }
        }
        for (CssToken t : attrFallback) {
            out.accept(t);
        }

    }

    /**
     * Processes an unknown function. Unknown functions will just be passed through.
     *
     * @param element the element
     * @param tt      the tokenizer
     * @param out     the consumer
     * @throws IOException    on io failure
     * @throws ParseException on parse failure
     */
    private void processUnknownFunction(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈func〉: function expected.");
        out.accept(tt.getToken());
        while (tt.next() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            tt.pushBack();
            processToken(element, tt, out);
        }
        if (tt.current() != CssTokenType.TT_EOF) {
            out.accept(tt.getToken());
        }

    }

    private CssDimension parseDimensionOrDouble(String attrValue, int pos) throws IOException, ParseException {
        CssStreamTokenizer tt = new CssStreamTokenizer(attrValue);
        if (tt.next() == CssTokenType.TT_DIMENSION) {
            return new CssDimension(tt.currentNumber().doubleValue(), tt.currentString());
        } else if (tt.current() == CssTokenType.TT_NUMBER) {
            return new CssDimension(tt.currentNumber().doubleValue(), null);
        }
        throw new ParseException("dimension expected, got: \"" + attrValue + "\"", pos);
    }

    private String parseAttrName(CssTokenizer tt) throws IOException, ParseException {
        StringBuilder buf = new StringBuilder();
        if (tt.next() == CssTokenType.TT_IDENT) {
            buf.append(tt.currentString());
        } else {
            tt.pushBack();
        }
        if (tt.nextNoSkip() == CssTokenType.TT_VERTICAL_LINE) {
            buf.append('|');
            if (tt.nextNoSkip() == CssTokenType.TT_IDENT) {
                buf.append(tt.currentString());
            } else {
                throw new ParseException("attr-name: identifier expected after \"|\".", tt.getStartPosition());
            }
        } else {
            tt.pushBack();
        }
        if (buf.length() == 0) {
            throw new ParseException("attr-name: identifier expected.", tt.getStartPosition());
        }
        return buf.toString();
    }

    /**
     * Processes the calc() function.
     * <pre>
     * calc               = "calc(", calc-sum, ")" ;
     * calc-sum            = calc-product ,  { [ '+' | '-' ] , calc-product } ;
     * calc-product        = calc-value , { '*' , calc-value | '/' , calc-number-value } ;
     * calc-value          = number | dimension | percentage | '(' , calc-sum , ')' ;
     * calc-number-sum     = calc-number-product , { [ '+' | '-' ] calc-number-product } ;
     * calc-number-product = calc-number-value> , { '*' , calc-number-value | '/' , calc-number-value } ;
     * calc-number-value   = number | calc-number-sum ;
     * </pre>
     * In addition, white space is required on both sides of the '+' and '-' operators.
     * (The '*' and '/' operaters can be used without white space around them.)
     *
     * @param tt  the tokenizer
     * @param out the consumer
     * @throws IOException
     */
    private void processCalcFunction(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        int line = tt.getLineNumber();
        int start = tt.getStartPosition();
        CssDimension dim = parseCalcFunction(element, tt);
        int end = tt.getEndPosition();
        if (dim.getUnits() == null) {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, null, dim.getValue(), line, start, end));
        } else if ("%".equals(dim.getUnits())) {
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, null, dim.getValue(), line, start, end));
        } else {
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, dim.getUnits(), dim.getValue(), line, start, end));
        }

    }

    private CssDimension parseCalcFunction(T element, CssTokenizer tt) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈calc〉: calc() function expected.");
        if (!"calc".equals(tt.currentStringNonnull())) {
            throw new ParseException("〈calc〉: calc() function expected.", tt.getStartPosition());
        }
        CssDimension dim = parseCalcSum(element, tt);
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "〈calc〉: right bracket \")\" expected.");
        return dim;
    }

    private CssDimension parseCalcSum(T element, CssTokenizer tt) throws IOException, ParseException {
        CssDimension dim = parseCalcProduct(element, tt);
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        Loop:
        for (; ; ) {
            switch (tt.next()) {
                case '+': {
                    CssDimension dim2 = parseCalcProduct(element, tt);
                    if (dim.getUnits() == null || dim2.getUnits() == null) {
                        dim = new CssDimension(dim.getValue() + dim2.getValue(), dim.getUnits());
                    } else {
                        dim = new CssDimension(dim.getValue() + c.convert(dim2, dim.getUnits()), dim.getUnits());
                    }
                    break;
                }
                case '-': {
                    CssDimension dim2 = parseCalcProduct(element, tt);
                    if (dim.getUnits() == null || dim2.getUnits() == null) {
                        dim = new CssDimension(dim.getValue() - dim2.getValue(), dim.getUnits());
                    } else {
                        dim = new CssDimension(dim.getValue() - c.convert(dim2, dim.getUnits()), dim.getUnits());
                    }
                    break;
                }
                default:
                    tt.pushBack();
                    break Loop;
            }
        }
        return dim;
    }

    private CssDimension parseCalcProduct(T element, CssTokenizer tt) throws IOException, ParseException {
        CssDimension dim = parseCalcValue(element, tt);
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        Loop:
        for (; ; ) {
            switch (tt.next()) {
                case '*': {
                    CssDimension dim2 = parseCalcProduct(element, tt);
                    if (dim.getUnits() == null || dim2.getUnits() == null) {
                        dim = new CssDimension(dim.getValue() * dim2.getValue(), dim.getUnits());
                    } else {
                        dim = new CssDimension(dim.getValue() * c.convert(dim2, dim.getUnits()), dim.getUnits());
                    }
                    break;
                }
                case '/': {
                    CssDimension dim2 = parseCalcProduct(element, tt);
                    if (dim.getUnits() == null || dim2.getUnits() == null) {
                        dim = new CssDimension(dim.getValue() / dim2.getValue(), dim.getUnits());
                    } else {
                        dim = new CssDimension(dim.getValue() / c.convert(dim2, dim.getUnits()), dim.getUnits());
                    }
                    break;
                }
                default:
                    tt.pushBack();
                    break Loop;
            }
        }
        return dim;
    }

    private CssDimension parseCalcValue(T element, CssTokenizer tt) throws IOException, ParseException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssDimension(tt.currentNumber().doubleValue(), null);
            case CssTokenType.TT_PERCENTAGE:
                return new CssDimension(tt.currentNumber().doubleValue(), "%");
            case CssTokenType.TT_DIMENSION:
                return new CssDimension(tt.currentNumber().doubleValue(), tt.currentString());
            case '(':
                CssDimension dim = parseCalcSum(element, tt);
                tt.requireNextToken(')', "calc-value: right bracket ')' expected.");
                return dim;
            case CssTokenType.TT_FUNCTION:
                String name = tt.currentString();
                tt.pushBack();
                List<CssToken> list = new ArrayList<>();
                processToken(element, tt, list::add);
                if (list.size() != 1) {
                    throw new ParseException("calc-value: function " + name + "() must return single value.", tt.getStartPosition());
                }
                CssToken token = list.get(0);
                switch (token.getType()) {
                    case CssTokenType.TT_NUMBER:
                        return new CssDimension(token.getNumericValue().doubleValue(), null);
                    case CssTokenType.TT_PERCENTAGE:
                        return new CssDimension(token.getNumericValue().doubleValue(), "%");
                    case CssTokenType.TT_DIMENSION:
                        return new CssDimension(token.getNumericValue().doubleValue(), token.getStringValue());
                    default:
                        throw new ParseException("calc-value: function " + name + "() must return numeric value.", tt.getStartPosition());
                }
            default:
                throw new ParseException("calc-value: number, percentage, dimension or (sum) expected.", tt.getStartPosition());
        }
    }

    /**
     * Processes the replace() function.
     * <pre>
     * replace     = "replace(", string, [","], regex, [","], replacement, ")" ;
     * string      = string-token ;
     * regex       = string-token ;
     * replacement = string-token ;
     * </pre>
     *
     * @param tt  the tokenizer
     * @param out the consumer
     * @throws IOException
     */
    private void processReplaceFunction(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈replace〉: replace() function expected.");
        if (!"replace".equals(tt.currentStringNonnull())) {
            throw new ParseException("〈replace〉: replace() function expected.", tt.getStartPosition());
        }

        int line = tt.getLineNumber();
        int start = tt.getStartPosition();

        String str = evalString(element, tt, "replace");
        if (tt.next() != CssTokenType.TT_COMMA) {
            tt.pushBack();
        }
        String regex = evalString(element, tt, "replace");
        if (tt.next() != CssTokenType.TT_COMMA) {
            tt.pushBack();
        }
        String repl = evalString(element, tt, "replace");
        if (tt.next() != CssTokenType.TT_RIGHT_BRACKET) {
            throw new ParseException("〈replace〉: right bracket ')' expected.", tt.getStartPosition());
        }

        try {
            String result = Pattern.compile(regex).matcher(str).replaceAll(repl);
            int end = tt.getEndPosition();
            out.accept(new CssToken(CssTokenType.TT_STRING, result, null, line, start, end));
        } catch (IllegalArgumentException e) {
            ParseException ex = new ParseException("〈replace〉: " + e.getMessage(), tt.getStartPosition());
            ex.initCause(e);
            throw ex;
        }

    }

    /**
     * Processes the concat() function.
     * <pre>
     * concat              = "concat(", string-list, ")" ;
     * string-iist         = value ,  { [ ',' ] , value } ;
     * value               = string | number | dimension | percentage | url ;
     * </pre>
     *
     * @param tt  the tokenizer
     * @param out the consumer
     * @throws IOException
     */
    private void processConcatFunction(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈concat〉: concat() function expected.");
        if (!"concat".equals(tt.currentStringNonnull())) {
            throw new ParseException("〈concat〉: concat() function expected.", tt.getStartPosition());
        }

        int line = tt.getLineNumber();
        int start = tt.getStartPosition();

        StringBuilder buf = new StringBuilder();
        boolean first = true;
        while (tt.next() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            switch (tt.current()) {
                case CssTokenType.TT_COMMA:
                    if (!first) {
                        continue;
                    }
                    // fall through
                default:
                    tt.pushBack();
                    buf.append(evalString(element, tt, "concat"));
            }
            first = false;
        }
        if (tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            throw new ParseException("〈concat〉: right bracket ')' expected.", tt.getStartPosition());
        }
        int end = tt.getEndPosition();
        out.accept(new CssToken(CssTokenType.TT_STRING, buf.toString(), null, line, start, end));
    }

    private String evalString(T element, CssTokenizer tt, String expressionName) throws IOException, ParseException {
        StringBuilder buf = new StringBuilder();
        List<CssToken> temp = new ArrayList<>();
        temp.clear();
        processToken(element, tt, temp::add);
        for (CssToken t : temp) {
            switch (t.getType()) {
                case CssTokenType.TT_STRING:
                case CssTokenType.TT_URL:
                    buf.append(t.getStringValue());
                    break;
                case CssTokenType.TT_NUMBER:
                case CssTokenType.TT_DIMENSION:
                case CssTokenType.TT_PERCENTAGE:
                    buf.append(t.fromToken());
                    break;
                default:
                    throw new ParseException("〈" + expressionName + "〉: String, Number, Dimension, Percentage or URL expected.", t.getStartPos());
            }
        }
        return buf.toString();
    }
}
