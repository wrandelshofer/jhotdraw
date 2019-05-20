package org.jhotdraw8.css;

import org.jhotdraw8.collection.ImmutableList;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Takes a list of tokens and evaluates common Css functions.
 * <p>
 * Supported standard functions: see {@link SimpleCssFunctionProcessor}.
 * <p>
 * Supported non-standard functions:
 * <dl>
 * <dt>concat()</dt><dd>Concat. Concatenates strings.</dd>
 * <dt>replace()</dt><dd>Replace. Replaces all substrings matching a regular expression in a string.</dd>
 * <dt>round()</dt><dd>Rounds a number.</dd>
 * </dl>
 *
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
public class ExtendedCssFunctionProcessor<T> extends SimpleCssFunctionProcessor<T> {
    public static final String REPLACE_FUNCTION_NAME = "replace";
    public static final String ROUND_FUNCTION_NAME = "round";
    public static final String CONCAT_FUNCTION_NAME = "concat";

    public ExtendedCssFunctionProcessor() {
        super();
    }

    public ExtendedCssFunctionProcessor(SelectorModel<T> model, Map<String, ImmutableList<CssToken>> customProperties) {
        super(model, customProperties);
    }


    protected void doProcessToken(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        if (tt.nextNoSkip() == CssTokenType.TT_FUNCTION) {
            switch (tt.currentStringNonnull()) {
                case REPLACE_FUNCTION_NAME:
                    tt.pushBack();
                    processReplaceFunction(element, tt, out);
                    break;
                case CONCAT_FUNCTION_NAME:
                    tt.pushBack();
                    processConcatFunction(element, tt, out);
                    break;
                case ROUND_FUNCTION_NAME:
                    tt.pushBack();
                    processRoundFunction(element, tt, out);
                    break;
                default:
                    tt.pushBack();
                    super.doProcessToken(element, tt, out);
                    break;
            }
        } else {
            out.accept(tt.getToken());
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
        if (!REPLACE_FUNCTION_NAME.equals(tt.currentStringNonnull())) {
            throw new ParseException("〈replace〉: replace() function expected.", tt.getStartPosition());
        }

        int line = tt.getLineNumber();
        int start = tt.getStartPosition();

        String str = evalString(element, tt, REPLACE_FUNCTION_NAME);
        if (tt.next() == CssTokenType.TT_COMMA) {
            tt.next();
        }
            tt.pushBack();
        String regex = evalString(element, tt, REPLACE_FUNCTION_NAME);
        if (tt.next() == CssTokenType.TT_COMMA) {
            tt.next();
        }
            tt.pushBack();
        String repl = evalString(element, tt, REPLACE_FUNCTION_NAME);
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
        if (!CONCAT_FUNCTION_NAME.equals(tt.currentStringNonnull())) {
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
                    tt.pushBack();
                    buf.append(evalString(element, tt, CONCAT_FUNCTION_NAME));
                    break;
                default:
                    tt.pushBack();
                    buf.append(evalString(element, tt, CONCAT_FUNCTION_NAME));
                    break;
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
                    throw new ParseException("〈" + expressionName + "〉: String, Number, CssSize, Percentage or URL expected.", t.getStartPos());
            }
        }
        return buf.toString();
    }

    /**
     * Processes the round() function.
     * <pre>
     * round              = "round(", value, ")" ;
     * value               = number | dimension | percentage ;
     * </pre>
     *
     * @param tt  the tokenizer
     * @param out the consumer
     * @throws IOException
     */
    private void processRoundFunction(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException {
        int line = tt.getLineNumber();
        int start = tt.getStartPosition();
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈" + ROUND_FUNCTION_NAME + "〉: " + ROUND_FUNCTION_NAME + "() function expected.");
        if (!ROUND_FUNCTION_NAME.equals(tt.currentStringNonnull())) {
            throw new ParseException("〈" + ROUND_FUNCTION_NAME + "〉: " + ROUND_FUNCTION_NAME + "() function expected.", tt.getStartPosition());
        }
        CssSize dim = parseCalcValue(element, tt);
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "〈" + ROUND_FUNCTION_NAME + "〉: right bracket \")\" expected.");
        int end = tt.getEndPosition();

        CssSize rounded = new CssSize(Math.round(dim.getValue()), dim.getUnits());
        produceNumberPercentageOrDimension(out, rounded, line, start, end);
    }

    @Override
    public String getHelpText() {
        return super.getHelpText()
                + "\n"
                + "\n  concat(⟨string⟩, ...)"
                + "\n    Concatenates a list of strings."
                + "\n"
                + "\n  replace(⟨string⟩, ⟨regex⟩, ⟨replacement⟩)"
                + "\n    Replaces matches of ⟨regex⟩ by ⟨replacement⟩ in the given ⟨string⟩."
                + "\n"
                + "\n  round(⟨value⟩)"
                + "\n    Rounds the specified value."
                + "\n    The value can be given as a number, dimension or a percentage."
                ;
    }
}
