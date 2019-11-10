/*
 * @(#)SimpleCssFunctionProcessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
 * References:
 * <ul>
 * <li>CSS Values and Units Module, Functional Notations.
 * <a href="https://www.w3.org/TR/css-values-3/#functional-notations">w3.org</a></li>
 * <li>CSS Custom Properties for Cascading Variables Module Level 1.  Using Cascading Variables: the var() notation.
 * <a href="https://www.w3.org/TR/css-variables-1/#using-variables">w3.org</a></li>
 * </ul>
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

    @NonNull
    public final ReadOnlyList<CssToken> process(@NonNull T element, @NonNull ImmutableList<CssToken> in) throws ParseException {
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
        return ImmutableLists.ofCollection(out);
    }

    @Override
    public String getHelpText() {
        return "Supported Functions:"
                + "\n  attr(⟨attr-name⟩ ⟨type-or-unit⟩, ⟨fallback⟩)"
                + "\n    Retrieves an attribute value by name and converts it to type-or-unit."
                + "\n    If the attribute does not exist or if the conversion fails, the fallback is used. "
                + "\n    type-or-unit must be one of 'string', 'color', 'url', 'integer', 'number', 'length' "
                + "'angle', 'time', 'frequency', '%'."
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

    public final void process(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out) throws IOException, ParseException {
        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            tt.pushBack();
            processToken(element, tt, out);
        }
    }

    private final static int MAX_RECURSION_DEPTH = 4;
    private int recursion;

    protected final void processToken(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out) throws IOException, ParseException {
        if (++recursion > MAX_RECURSION_DEPTH) {
            recursion = 0;
            throw new ParseException("Max recursion depth exceeded", tt.getStartPosition());
        }
        try {
            doProcessToken(element, tt, out);
        } finally {
            --recursion;
        }
    }

    protected void doProcessToken(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out) throws IOException, ParseException {
        if (tt.nextNoSkip() == CssTokenType.TT_FUNCTION) {
            switch (tt.currentStringNonNull()) {
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
     *                   | "%" ( "length" | "angle" | "time" | "frequency" )
     *                   ;
     *     attr-fallback = ident-token;
     *
     *     qualified-name = [ [ ident-token ], "|" ] , ident-token ;
     * </pre>
     * If attr-fallback is not given, then ident "none" is assumed.
     *
     * @param tt  the tokenizer
     * @param out the consumer
     * @throws IOException
     */
    private void processAttrFunction(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out) throws IOException, ParseException {
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
            typeOrUnit = UnitConverter.PERCENTAGE;
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

        @Nullable List<CssToken> strValue = model.getAttribute(element, null, attrName.getNamespace(), attrName.getName());
        if (strValue != null) {
            Outer:
            switch (typeOrUnit == null ? "string" : typeOrUnit) {
                case "string": {
                    final ListCssTokenizer t2 = new ListCssTokenizer(strValue);
                    while (t2.next() != CssTokenType.TT_EOF) {
                        switch (t2.current()) {
                            case CssTokenType.TT_STRING:
                                out.accept(new CssToken(CssTokenType.TT_STRING, t2.currentStringNonNull(), null, line, start, end));
                                break;
                            case CssTokenType.TT_IDENT:
                                if (t2.currentStringNonNull().equals(CssTokenType.IDENT_NONE)) {
                                    out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
                                } else {
                                    out.accept(new CssToken(CssTokenType.TT_STRING, t2.currentStringNonNull(), null, line, start, end));
                                }
                                break;
                            case CssTokenType.TT_NUMBER:
                            case CssTokenType.TT_PERCENTAGE:
                                out.accept(new CssToken(CssTokenType.TT_STRING, t2.currentNumberNonNull().toString(), null, line, start, end));
                                break;
                            case CssTokenType.TT_DIMENSION:
                                out.accept(new CssToken(CssTokenType.TT_STRING, t2.currentNumberNonNull().toString() + t2.currentStringNonNull(), null, line, start, end));
                                break;
                            default:
                                break Outer; // use fallback
                        }
                    }
                    return; // use output
                }
                case "color":
                case "url":
                    break;//use fallback
                case "integer":
                case "number": {
                    final ListCssTokenizer t2 = new ListCssTokenizer(strValue);
                    if (t2.next() == CssTokenType.TT_EOF) {
                        break Outer; // use fallback
                    }
                    t2.pushBack();
                    while (t2.next() != CssTokenType.TT_EOF) {
                        switch (t2.current()) {
                            case CssTokenType.TT_STRING:
                            case CssTokenType.TT_IDENT:
                                double d;
                                try {
                                    d = Double.parseDouble(t2.currentStringNonNull());
                                } catch (NumberFormatException e) {
                                    break Outer; // use fallback
                                }
                                out.accept(new CssToken(CssTokenType.TT_NUMBER, null, d, line, start, end));
                                break;
                            case CssTokenType.TT_NUMBER:
                            case CssTokenType.TT_DIMENSION:
                            case CssTokenType.TT_PERCENTAGE:
                                out.accept(new CssToken(CssTokenType.TT_NUMBER, null, t2.currentNumberNonNull(), line, start, end));
                                break;
                            default:
                                break Outer; // use fallback
                        }
                    }
                    return; // use output
                }
                case "length": {
                    final ListCssTokenizer t2 = new ListCssTokenizer(strValue);
                    if (t2.next() == CssTokenType.TT_EOF) {
                        break Outer; // use fallback
                    }
                    t2.pushBack();
                    while (t2.next() != CssTokenType.TT_EOF) {
                        switch (t2.current()) {
                            case CssTokenType.TT_STRING:
                            case CssTokenType.TT_IDENT:
                                double d;
                                try {
                                    d = Double.parseDouble(t2.currentStringNonNull());
                                } catch (NumberFormatException e) {
                                    break Outer; // use fallback
                                }
                                out.accept(new CssToken(CssTokenType.TT_DIMENSION, UnitConverter.DEFAULT, d, line, start, end));
                                break;
                            case CssTokenType.TT_NUMBER:
                            case CssTokenType.TT_DIMENSION:
                            case CssTokenType.TT_PERCENTAGE:
                                out.accept(new CssToken(CssTokenType.TT_DIMENSION, t2.currentString() == null ? "" : t2.currentStringNonNull(), t2.currentNumberNonNull(), line, start, end));
                                break;
                            default:
                                break Outer; // use fallback
                        }
                    }
                    return; // use output
                }
                case "%": {
                    final ListCssTokenizer t2 = new ListCssTokenizer(strValue);
                    while (t2.next() != CssTokenType.TT_EOF) {
                        switch (t2.current()) {
                            case CssTokenType.TT_STRING:
                            case CssTokenType.TT_IDENT:
                                double d;
                                try {
                                    d = Double.parseDouble(t2.currentStringNonNull());
                                } catch (NumberFormatException e) {
                                    break Outer; // use fallback
                                }
                                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, null, d, line, start, end));
                                break;
                            case CssTokenType.TT_NUMBER:
                            case CssTokenType.TT_DIMENSION:
                            case CssTokenType.TT_PERCENTAGE:
                                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, null, t2.currentNumberNonNull(), line, start, end));
                                break;
                            default:
                                break Outer; // use fallback
                        }
                    }
                    return; // use output
                }
                case "angle":
                case "time":
                case "frequency":
                    // XXX currently not implemented
                    break; // use fallback
                default:
                    final ListCssTokenizer t2 = new ListCssTokenizer(strValue);
                    while (t2.next() != CssTokenType.TT_EOF) {
                        switch (t2.current()) {
                            case CssTokenType.TT_STRING:
                            case CssTokenType.TT_IDENT:
                                break Outer;// use fallback
                            case CssTokenType.TT_NUMBER:
                            case CssTokenType.TT_DIMENSION:
                            case CssTokenType.TT_PERCENTAGE:
                                out.accept(new CssToken(CssTokenType.TT_DIMENSION, typeOrUnit, t2.currentNumberNonNull(), line, start, end));
                                break;
                            default:
                                break Outer; // use fallback
                        }
                    }
                    return; // use output
            }

        }
        processToken(element, new ListCssTokenizer(
                        attrFallback.isEmpty() ? Collections.singletonList(new CssToken(CssTokenType.TT_IDENT, "none")) : attrFallback),
                out);

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
    private void processVarFunction(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out) throws IOException, ParseException {
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
    private void processUnknownFunction(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out) throws IOException, ParseException {
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

    @Nullable
    protected final CssSize parseDimensionOrDouble(String attrValue, int pos) throws IOException, ParseException {
        StreamCssTokenizer tt = new StreamCssTokenizer(attrValue);
        if (tt.next() == CssTokenType.TT_DIMENSION) {
            return new CssSize(tt.currentNumber().doubleValue(), tt.currentString());
        } else if (tt.current() == CssTokenType.TT_NUMBER) {
            return new CssSize(tt.currentNumber().doubleValue());
        }
        throw new ParseException("dimension expected, got: \"" + attrValue + "\"", pos);
    }

    @Nullable
    private QualifiedName parseAttrName(@NonNull CssTokenizer tt) throws IOException, ParseException {
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
    private void processCalcFunction(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out) throws IOException, ParseException {
        int line = tt.getLineNumber();
        int start = tt.getStartPosition();
        CssSize dim = parseCalcFunction(element, tt);
        int end = tt.getEndPosition();
        produceNumberPercentageOrDimension(out, dim, line, start, end);
    }

    protected void produceNumberPercentageOrDimension(@NonNull Consumer<CssToken> out, @NonNull CssSize dim, int line, int start, int end) {
        if (dim.getUnits() == null) {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, null, dim.getValue(), line, start, end));
        } else if ("%".equals(dim.getUnits())) {
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, null, dim.getValue(), line, start, end));
        } else {
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, dim.getUnits(), dim.getValue(), line, start, end));
        }
    }

    @Nullable
    private CssSize parseCalcFunction(@NonNull T element, @NonNull CssTokenizer tt) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈" + CALC_FUNCTION_NAME + "〉: " + CALC_FUNCTION_NAME + "() function expected.");
        if (!CALC_FUNCTION_NAME.equals(tt.currentStringNonNull())) {
            throw new ParseException("〈" + CALC_FUNCTION_NAME + "〉: " + CALC_FUNCTION_NAME + "() function expected.", tt.getStartPosition());
        }
        CssSize dim = parseCalcSum(element, tt);
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "〈" + CALC_FUNCTION_NAME + "〉: right bracket \")\" expected.");
        return dim;
    }

    @Nullable
    private CssSize parseCalcSum(@NonNull T element, @NonNull CssTokenizer tt) throws IOException, ParseException {
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

    @Nullable
    private CssSize parseCalcProduct(@NonNull T element, @NonNull CssTokenizer tt) throws IOException, ParseException {
        CssSize dim = parseCalcValue(element, tt);
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        Loop:
        for (; ; ) {
            switch (tt.next()) {
                case '*': {
                    CssSize dim2 = parseCalcProduct(element, tt);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                            || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() * dim2.getValue(), dim.getUnits());
                    } else {
                        dim = c.convertSize(new CssSize(dim.getConvertedValue() * dim2.getConvertedValue(), UnitConverter.DEFAULT), dim.getUnits());
                    }
                    break;
                }
                case '/': {
                    CssSize dim2 = parseCalcProduct(element, tt);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                            || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() / dim2.getValue(), dim.getUnits());
                    } else {
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

    @Nullable
    protected CssSize parseCalcValue(@NonNull T element, @NonNull CssTokenizer tt) throws IOException, ParseException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssSize(tt.currentNumberNonNull().doubleValue());
            case CssTokenType.TT_PERCENTAGE:
                return new CssSize(tt.currentNumberNonNull().doubleValue(), "%");
            case CssTokenType.TT_DIMENSION:
                return new CssSize(tt.currentNumberNonNull().doubleValue(), tt.currentStringNonNull());
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
