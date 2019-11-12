package org.jhotdraw8.css.functions;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.UnitConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
 */

public class CalcCssFunction<T> extends AbstractCssFunction<T> {
    /**
     * Function name.
     */
    public final static String NAME = "calc";

    public CalcCssFunction() {
        this(NAME);
    }

    public CalcCssFunction(String name) {
        super(name);
    }

    public void process(@NonNull T element,
                        @NonNull CssTokenizer tt,
                        @NonNull SelectorModel<T> model,
                        @NonNull CssFunctionProcessor<T> functionProcessor,
                        @NonNull Consumer<CssToken> out) throws IOException, ParseException {
        int line = tt.getLineNumber();
        int start = tt.getStartPosition();
        CssSize dim = parseCalcFunction(element, tt, functionProcessor);
        int end = tt.getEndPosition();
        produceNumberPercentageOrDimension(out, dim, line, start, end);
    }

    @Nullable
    private CssSize parseCalcFunction(@NonNull T element, @NonNull CssTokenizer tt, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈" + getName() + "〉: " + getName() + "() function expected.");
        if (!getName().equals(tt.currentStringNonNull())) {
            throw new ParseException("〈" + getName() + "〉: " + getName() + "() function expected.", tt.getStartPosition());
        }
        CssSize dim = parseCalcSum(element, tt, functionProcessor);
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "〈" + getName() + "〉: right bracket \")\" expected.");
        return dim;
    }

    @Nullable
    private CssSize parseCalcSum(@NonNull T element, @NonNull CssTokenizer tt, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        CssSize dim = parseCalcProduct(element, tt, functionProcessor);
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        Loop:
        for (; ; ) {
            switch (tt.next()) {
                case '+': {
                    CssSize dim2 = parseCalcProduct(element, tt, functionProcessor);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                            || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() + dim2.getValue(), dim.getUnits());
                    } else {
                        dim = new CssSize(dim.getValue() + c.convert(dim2, dim.getUnits()), dim.getUnits());
                    }
                    break;
                }
                case '-': {
                    CssSize dim2 = parseCalcProduct(element, tt, functionProcessor);
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
    private CssSize parseCalcProduct(@NonNull T element, @NonNull CssTokenizer tt, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        CssSize dim = parseCalcValue(element, tt, functionProcessor);
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        Loop:
        for (; ; ) {
            switch (tt.next()) {
                case '*': {
                    CssSize dim2 = parseCalcProduct(element, tt, functionProcessor);
                    if (dim2.getUnits().equals(UnitConverter.DEFAULT)
                            || dim.getUnits().equals(dim2.getUnits())) {
                        dim = new CssSize(dim.getValue() * dim2.getValue(), dim.getUnits());
                    } else {
                        dim = c.convertSize(new CssSize(dim.getConvertedValue() * dim2.getConvertedValue(), UnitConverter.DEFAULT), dim.getUnits());
                    }
                    break;
                }
                case '/': {
                    CssSize dim2 = parseCalcProduct(element, tt, functionProcessor);
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
    protected CssSize parseCalcValue(@NonNull T element, @NonNull CssTokenizer tt, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssSize(tt.currentNumberNonNull().doubleValue());
            case CssTokenType.TT_PERCENTAGE:
                return new CssSize(tt.currentNumberNonNull().doubleValue(), "%");
            case CssTokenType.TT_DIMENSION:
                return new CssSize(tt.currentNumberNonNull().doubleValue(), tt.currentStringNonNull());
            case '(':
                CssSize dim = parseCalcSum(element, tt, functionProcessor);
                tt.requireNextToken(')', "calc-value: right bracket ')' expected.");
                return dim;
            case CssTokenType.TT_FUNCTION:
                String name = tt.currentString();
                tt.pushBack();
                List<CssToken> list = new ArrayList<>();
                functionProcessor.processToken(element, tt, list::add);
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

    @Override
    public String getHelpText() {
        return getName() + "(⟨expression⟩)"
                + "\n    Computes a mathematical expression with addition (+),"
                + " subtraction (-), multiplication (*), and division (/)."
                + "\n    It can be used wherever ⟨length⟩, ⟨frequency⟩, "
                + "⟨angle⟩, ⟨time⟩, ⟨percentage⟩, ⟨number⟩, or ⟨integer⟩ values are allowed. ";
    }
}
