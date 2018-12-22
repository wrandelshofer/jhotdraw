package org.jhotdraw8.css;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.UnitConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Takes a list of tokens and evaluates common Css functions.
 * <p>
 * Supported standard functions:
 * <dl>
 * <dt>attr()</dt><dd>Attribute Reference. Returns the value of an attribute on the element.</dd>
 * <dt>calc()</dt><dd>Mathematical Expressions. Returns the value of a mathematical expression.</dd>
 * <dt>var()</dt><dd>Cascading variable. Substitutes the value of a property from the value of another property.</dd>
 * </dl>
 * <p>
 *
 * <p>
 * References:
 * <ul>
 * <li>CSS Values and Units Module, Functional Notations.
 * <a href="https://www.w3.org/TR/css-values-3/#functional-notations">w3.org</a></li>
 * <li>CSS Custom Properties for Cascading Variables Module Level 1.  Using Cascading Variables: the var() notation.
 * <a href="https://www.w3.org/TR/css-variables-1/#using-variables">w3.org</a></li>
 * </ul>
 * </p>
 *
 * @param <T> the element type
 */
public class SimpleCssFunctionProcessor<T> implements CssFunctionProcessor<T> {
    private static final String ATTR_FUNCTION_NAME = "attr";
    private static final String CALC_FUNCTION_NAME = "calc";
    private static final String VAR_FUNCTION_NAME = "var";

    protected SelectorModel<T> model;
    protected Map<String, ImmutableList<CssToken>> customProperties;

    public SimpleCssFunctionProcessor() {
    }
    public SimpleCssFunctionProcessor(SelectorModel<T> model, Map<String, ImmutableList<CssToken>> customProperties) {
        this.model = model;
        this.customProperties = customProperties;
    }

    public SelectorModel<T> getModel() {
        return model;
    }

    public void setModel(SelectorModel<T> model) {
        this.model = model;
    }

    public Map<String, ImmutableList<CssToken>> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, ImmutableList<CssToken>> customProperties) {
        this.customProperties = customProperties;
    }

    public final ReadOnlyList<CssToken> process(T element, ImmutableList<CssToken> in) throws ParseException {
        ListCssTokenizer tt = new ListCssTokenizer(in);
        ArrayList<CssToken> out = new ArrayList<>(in.size());
        try {
            process(element, tt, out::add);
        } catch (IOException e) {
            e.printStackTrace();
            out.clear();
            for (CssToken t : in) {
                out.add(t);
            }
        }
        return ImmutableList.ofCollection(out);
    }

    @Override
    public String getHelpText() {
        return "Supported Functions:"
                + "\n  attr(⟨attr-name⟩ ⟨type-or-unit⟩, ⟨fallback⟩)"
                + "\n    Retrieves an attribute value by name and converts it to type-or-unit."
                + "\n    If the attribute does not exist or if the conversion fails, the fallback is used. "
                + "\n    type-or-unit must be one of 'string', 'color', 'url', 'integer', 'number', 'length' "
                +"'angle', 'time', 'frequency', '%'."
                + "\n"
                + "\n  calc(⟨expression⟩)"
                + "\n    Computes a mathematical expression with addition (+),"
                + " subtraction (-), multiplication (*), and division (/)."
                + "\n    It can be used wherever ⟨length⟩, ⟨frequency⟩, "
                + "⟨angle⟩, ⟨time⟩, ⟨percentage⟩, ⟨number⟩, or ⟨integer⟩ values are allowed. "
                + "\n"
                + "\n  var(⟨custom-property-name⟩, ⟨fallback⟩)"
                + "\n    Retrieves a custom-property by name."
                + "\n    If the custom-property is not found, the fallback is used."
                + "\n    A custom-property is a property defined on a parent element (or on ':root')."
                + "\n    The name of a custom-property must start with two dashes: '--'."
                ;
    }

    public final void process(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            tt.pushBack();
            processToken(element, tt, out);
        }
    }

    private final static int MAX_RECURSION_DEPTH = 4;
    private int recursion;

    protected final void processToken(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        if (++recursion > MAX_RECURSION_DEPTH) {
            recursion=0;
            throw new ParseException("Max recursion depth exceeded", tt.getStartPosition());
        }
        try {
            doProcessToken(element, tt, out);
        } finally {
            --recursion;
        }
    }

    protected void doProcessToken(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        if (tt.nextNoSkip() == CssTokenType.TT_FUNCTION) {
            switch (tt.currentStringNonnull()) {
                case ATTR_FUNCTION_NAME:
                    tt.pushBack();
                    processAttrFunction(element, tt, out);
                    break;
                case CALC_FUNCTION_NAME:
                    tt.pushBack();
                    processCalcFunction(element, tt, out);
                    break;
                case VAR_FUNCTION_NAME:
                    tt.pushBack();
                    processVarFunction(element, tt, out);
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
        if (!ATTR_FUNCTION_NAME.equals(tt.currentString())) {
            throw new ParseException("〈attr〉: function attr() expected.", tt.getStartPosition());
        }
        int line = tt.getLineNumber();
        int start = tt.getStartPosition();

        QualifiedName attrName = parseAttrName(tt);
        String typeOrUnit = null;

        List<CssToken> attrFallback = new ArrayList<>();
        if (tt.next() == CssTokenType.TT_PERCENT_DELIM) {
            if (tt.next() == CssTokenType.TT_IDENT) {
                typeOrUnit = UnitConverter.PERCENTAGE + tt.currentString();
            } else {
                typeOrUnit = UnitConverter.PERCENTAGE;
                tt.pushBack();
            }
        } else if (tt.current() == CssTokenType.TT_IDENT) {
            typeOrUnit = tt.currentString();
        } else {
            tt.pushBack();
        }

        if (tt.next() == CssTokenType.TT_COMMA) {
            while (tt.nextNoSkip() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
                attrFallback.add(tt.getToken());
            }
        }
        if (tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            throw new ParseException("〈attr〉: right bracket expected. " + tt.current(), tt.getStartPosition());
        }
        int end = tt.getEndPosition();

        String attrValue = model.getAttributeAsString(element, attrName.getNamespace(), attrName.getName());
        if (attrValue != null && !attrValue.isEmpty()) {
            if (typeOrUnit == null) {
                typeOrUnit = "string";
            }
            StreamCssTokenizer att = new StreamCssTokenizer(attrValue);
            Outer:
            switch (typeOrUnit) {
                case "string":
                    String strValue;
                    switch (att.next()) {
                        case CssTokenType.TT_STRING:
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
                        CssSize dim = parseDimensionOrDouble(attrValue, start);
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
        processToken(element, new ListCssTokenizer(attrFallback), out);

    }

    /**
     * Processes the var() function.
     * <pre>
     *     var = "var(" ,  s* , custom-property-name, s* , [ "," ,  s* , declaration-value ] ,  s* , ")" ;
     *     custom-property-name = ident-token;
     *     declaration-value = fallback-value;
     * </pre>
     * THe custom-property-name must start with two dashes "--".
     *
     * @param tt  the tokenizer
     * @param out the consumer
     * @throws IOException
     */
    private void processVarFunction(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈var〉: function var() expected.");
        if (!VAR_FUNCTION_NAME.equals(tt.currentString())) {
            throw new ParseException("〈var〉: function var() expected.", tt.getStartPosition());
        }
        int line = tt.getLineNumber();
        int start = tt.getStartPosition();

        tt.requireNextToken(CssTokenType.TT_IDENT, "〈var〉: function custom-property-name expected.");

        String customPropertyName = tt.currentString();
        List<CssToken> attrFallback = new ArrayList<>();
        if (tt.next() == CssTokenType.TT_COMMA) {
            while (tt.nextNoSkip() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
                attrFallback.add(tt.getToken());
            }
        }
        if (tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            throw new ParseException("〈attr〉: right bracket expected. " + tt.current(), tt.getStartPosition());
        }
        int end = tt.getEndPosition();

        if (!customPropertyName.startsWith("--")) {
            throw new ParseException("〈var〉: custom-property-name starting with two dashes \"--\" expected. Found: \"" + customPropertyName + "\"", tt.getStartPosition());
        }
        ReadOnlyList<CssToken> customValue = customProperties.get(customPropertyName);
        if (customValue == null) {
            process(element, new ListCssTokenizer(attrFallback), out);
        } else {
            process(element, new ListCssTokenizer(customValue), out);
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
        while (tt.nextNoSkip() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            tt.pushBack();
            processToken(element, tt, out);
        }
        if (tt.current() != CssTokenType.TT_EOF) {
            out.accept(tt.getToken());
        }

    }

    protected final CssSize parseDimensionOrDouble(String attrValue, int pos) throws IOException, ParseException {
        StreamCssTokenizer tt = new StreamCssTokenizer(attrValue);
        if (tt.next() == CssTokenType.TT_DIMENSION) {
            return new CssSize(tt.currentNumber().doubleValue(), tt.currentString());
        } else if (tt.current() == CssTokenType.TT_NUMBER) {
            return new CssSize(tt.currentNumber().doubleValue());
        }
        throw new ParseException("dimension expected, got: \"" + attrValue + "\"", pos);
    }

    private QualifiedName parseAttrName(CssTokenizer tt) throws IOException, ParseException {
        String name;
        if (tt.next() == CssTokenType.TT_IDENT) {
            name = tt.currentString();
        } else {
            throw new ParseException("attr-name expected.", tt.getStartPosition());
        }
        return new QualifiedName(null, name);// FIXME parse namespace
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
        CssSize dim = parseCalcFunction(element, tt);
        int end = tt.getEndPosition();
        produceNumberPercentageOrDimension(out, dim, line, start, end);
    }

    protected void produceNumberPercentageOrDimension(Consumer<CssToken> out, CssSize dim, int line, int start, int end) {
        if (dim.getUnits() == null) {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, null, dim.getValue(), line, start, end));
        } else if ("%".equals(dim.getUnits())) {
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, null, dim.getValue(), line, start, end));
        } else {
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, dim.getUnits(), dim.getValue(), line, start, end));
        }
    }

    private CssSize parseCalcFunction(T element, CssTokenizer tt) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈" + CALC_FUNCTION_NAME + "〉: " + CALC_FUNCTION_NAME + "() function expected.");
        if (!CALC_FUNCTION_NAME.equals(tt.currentStringNonnull())) {
            throw new ParseException("〈" + CALC_FUNCTION_NAME + "〉: " + CALC_FUNCTION_NAME + "() function expected.", tt.getStartPosition());
        }
        CssSize dim = parseCalcSum(element, tt);
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "〈" + CALC_FUNCTION_NAME + "〉: right bracket \")\" expected.");
        return dim;
    }

    private CssSize parseCalcSum(T element, CssTokenizer tt) throws IOException, ParseException {
        CssSize dim = parseCalcProduct(element, tt);
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        Loop:
        for (; ; ) {
            switch (tt.next()) {
                case '+': {
                    CssSize dim2 = parseCalcProduct(element, tt);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                            || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() + dim2.getValue(), dim.getUnits());
                    } else {
                        dim = new CssSize(dim.getValue() + c.convert(dim2, dim.getUnits()), dim.getUnits());
                    }
                    break;
                }
                case '-': {
                    CssSize dim2 = parseCalcProduct(element, tt);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                            || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() - dim2.getValue(), dim.getUnits());
                    } else {
                        dim = new CssSize(dim.getValue() - c.convert(dim2, dim.getUnits()), dim.getUnits());
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

    private CssSize parseCalcProduct(T element, CssTokenizer tt) throws IOException, ParseException {
        CssSize dim = parseCalcValue(element, tt);
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        Loop:
        for (; ; ) {
            switch (tt.next()) {
                case '*': {
                    CssSize dim2 = parseCalcProduct(element, tt);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                            || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() * dim2.getValue(),  dim.getUnits());
                    }else {
                        dim = c.convertSize(new CssSize(dim.getConvertedValue() * dim2.getConvertedValue(), UnitConverter.DEFAULT), dim.getUnits());
                    }
                    break;
                }
                case '/': {
                    CssSize dim2 = parseCalcProduct(element, tt);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                    || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() / dim2.getValue(),  dim.getUnits());
                    }else {
                        dim = c.convertSize(new CssSize(dim.getConvertedValue() / dim2.getConvertedValue(), UnitConverter.DEFAULT), dim.getUnits());
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

    protected CssSize parseCalcValue(T element, CssTokenizer tt) throws IOException, ParseException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssSize(tt.currentNumberNonnull().doubleValue());
            case CssTokenType.TT_PERCENTAGE:
                return new CssSize(tt.currentNumberNonnull().doubleValue(), "%");
            case CssTokenType.TT_DIMENSION:
                return new CssSize(tt.currentNumberNonnull().doubleValue(), tt.currentStringNonnull());
            case '(':
                CssSize dim = parseCalcSum(element, tt);
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
                        return new CssSize(token.getNumericValue().doubleValue());
                    case CssTokenType.TT_PERCENTAGE:
                        return new CssSize(token.getNumericValue().doubleValue(), "%");
                    case CssTokenType.TT_DIMENSION:
                        return new CssSize(token.getNumericValue().doubleValue(), token.getStringValue());
                    default:
                        throw new ParseException("calc-value: function " + name + "() must return numeric value.", tt.getStartPosition());
                }
            default:
                throw new ParseException("calc-value: number, percentage, dimension or (sum) expected.", tt.getStartPosition());
        }
    }
}
