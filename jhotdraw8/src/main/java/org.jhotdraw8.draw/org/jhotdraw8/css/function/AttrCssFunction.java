/*
 * @(#)AttrCssFunction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.function;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.ListCssTokenizer;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.UnitConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

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
 * <p>
 * References:
 * <dl>
 *     <dd></dd>attr() function<dt>
 *         <a href="https://drafts.csswg.org/css-values-4/#attr-notation">csswg.org</a></a>
 *     </dt>
 * </dl>
 * </p>
 */
public class AttrCssFunction<T> extends AbstractCssFunction<T> {
    /**
     * Function name.
     */
    public final static String NAME = "attr";

    public AttrCssFunction() {
        super(NAME);
    }

    public AttrCssFunction(String name) {
        super(name);
    }

    @Override
    public void process(@NonNull T element, @NonNull CssTokenizer tt,
                        @NonNull SelectorModel<T> model,
                        @NonNull CssFunctionProcessor<T> functionProcessor,
                        @NonNull Consumer<CssToken> out, Deque<CssFunction<T>> recursionStack) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈" + getName() + "〉: function " + getName() + "() expected.");
        if (!getName().equals(tt.currentString())) {
            throw tt.createParseException("〈" + getName() + "〉: function " + getName() + "() expected.");
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
                if (tt.current() == CssTokenType.TT_S && attrFallback.isEmpty()) {
                    continue;//remove leading white space
                }
                attrFallback.add(tt.getToken());
            }
            while (!attrFallback.isEmpty() && attrFallback.get(attrFallback.size() - 1).getType() == CssTokenType.TT_S) {
                attrFallback.remove(attrFallback.size() - 1);//remove trailing white space
            }
        }
        if (tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            throw new ParseException("〈attr〉: right bracket expected. " + tt.current(), tt.getStartPosition());
        }
        int end = tt.getEndPosition();

        applyFunction(element, model, functionProcessor, out, recursionStack, line, start, attrName, typeOrUnit, attrFallback, end);
    }

    public void applyFunction(@NonNull T element, @NonNull SelectorModel<T> model, @NonNull CssFunctionProcessor<T> functionProcessor, @NonNull Consumer<CssToken> out, Deque<CssFunction<T>> recursionStack, int line, int start, QualifiedName attrName, String typeOrUnit, List<CssToken> attrFallback, int end) throws IOException, ParseException {
        @Nullable List<CssToken> tokenizedValue = model.getAttribute(element, null, attrName.getNamespace(), attrName.getName());
        if (tokenizedValue != null) {
            switch (typeOrUnit == null ? "string" : typeOrUnit) {
                default:
                case "string":
                    if (applyAsString(element, model, out, line, start, attrName, end)) {
                        return;
                    }
                    if (attrFallback.isEmpty()) {
                        attrFallback = Arrays.asList(new CssToken(CssTokenType.TT_STRING, ""));
                    }
                    break;
                case "color":
                    // FIXME implement applyAsColor
                    if (attrFallback.isEmpty()) {
                        attrFallback = Arrays.asList(new CssToken(CssTokenType.TT_IDENT, "currentcolor"));
                    }
                    break;
                case "url":
                    // FIXME implement applyAsURL
                    if (attrFallback.isEmpty()) {
                        attrFallback = Arrays.asList(new CssToken(CssTokenType.TT_URL, "about:invalid"));
                    }
                    break;
                case "em":
                case "ex":
                case "px":
                case "rem":
                case "vw":
                case "vh":
                case "vmin":
                case "vmax":
                case "mm":
                case "cm":
                case "in":
                case "pt":
                case "pc":
                    if (applyAsLengthInTheGivenUnits(out, line, start, typeOrUnit, end, tokenizedValue)) {
                        return;
                    }
                    if (attrFallback.isEmpty()) {
                        attrFallback = Arrays.asList(new CssToken(CssTokenType.TT_DIMENSION, 0, typeOrUnit));
                    }
                    break;

                case "integer":
                case "number":
                    if (applyAsNumber(out, line, start, typeOrUnit, end, tokenizedValue)) {
                        return;
                    }
                    if (attrFallback.isEmpty()) {
                        attrFallback = Arrays.asList(new CssToken(CssTokenType.TT_NUMBER, 0, typeOrUnit));
                    }
                    break;
                case "length":
                    if (applyAsLength(out, line, start, typeOrUnit, end, tokenizedValue)) {
                        return;
                    }
                    if (attrFallback.isEmpty()) {
                        attrFallback = Arrays.asList(new CssToken(CssTokenType.TT_NUMBER, 0, typeOrUnit));
                    }
                    break;
                case "%":
                    if (applyAsPercentage(out, line, start, typeOrUnit, end, tokenizedValue)) {
                        return;
                    }
                    if (attrFallback.isEmpty()) {
                        attrFallback = Arrays.asList(new CssToken(CssTokenType.TT_NUMBER, 0, typeOrUnit));
                    }
                    break;
                case "angle":
                case "time":
                case "frequency":
                    // XXX currently not implemented
                    break; // use fallback
            }

        }
        recursionStack.push(this);
        functionProcessor.processToken(element, new ListCssTokenizer(
                        attrFallback.isEmpty() ? Collections.singletonList(new CssToken(CssTokenType.TT_IDENT, "none")) : attrFallback),
                out, recursionStack);
        recursionStack.pop();
    }

    /**
     * The attribute value is parsed as a CSS <number>, that is without the unit (e.g. 12.5), and interpreted as a <percentage>. If it is not valid, that is not a number or out of the range accepted by the CSS property, the default value is used.
     * If the given value is used as a length, attr() computes it to an absolute length.
     * Leading and trailing spaces are stripped.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/attr">
     * MDN web docs, attr()</a>
     */
    private boolean applyAsPercentage(Consumer<CssToken> out, int line, int start, String typeOrUnit, int end, List<CssToken> tokenizedValue) {
        final ListCssTokenizer t2 = new ListCssTokenizer(tokenizedValue);
        while (t2.next() != CssTokenType.TT_EOF) {
            switch (t2.current()) {
                case CssTokenType.TT_STRING:
                case CssTokenType.TT_IDENT:
                    double d;
                    try {
                        d = Double.parseDouble(t2.currentStringNonNull());
                    } catch (NumberFormatException e) {
                        return false; // use fallback
                    }
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, null, d, line, start, end));
                    return true;
                case CssTokenType.TT_NUMBER:
                case CssTokenType.TT_DIMENSION:
                case CssTokenType.TT_PERCENTAGE:
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, null, t2.currentNumberNonNull(), line, start, end));
                    return true;
            }
        }
        return false;
    }

    /**
     * The attribute value is parsed as a CSS {@code length} dimension,
     * that is including the unit (e.g. 12.5em). If it is not valid, that is
     * not a length or out of the range accepted by the CSS property, the default value is used.
     * If the given unit is a relative length, attr() computes it to an absolute length.
     * Leading and trailing spaces are stripped.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/attr">
     * MDN web docs, attr()</a>
     */
    private boolean applyAsLength(Consumer<CssToken> out, int line, int start, String typeOrUnit, int end, List<CssToken> tokenizedValue) {
        final ListCssTokenizer t2 = new ListCssTokenizer(tokenizedValue);
        if (t2.next() == CssTokenType.TT_EOF) {
            return false; // use fallback
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
                        return false; // use fallback
                    }
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, UnitConverter.DEFAULT, d, line, start, end));
                    return true;
                case CssTokenType.TT_NUMBER:
                case CssTokenType.TT_DIMENSION:
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, t2.currentString() == null ? "" : t2.currentString(), t2.currentNumberNonNull(), line, start, end));
                    return true;
                case CssTokenType.TT_PERCENTAGE:
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, "", t2.currentNumberNonNull().doubleValue() * 100, line, start, end));
                    return true;

            }
        }
        return false; // use fallback
    }

    /**
     * The attribute value is parsed as a CSS {@code number}.
     * If it is not valid, that is not a number or out of the range accepted
     * by the CSS property, the default value is used.
     * Leading and trailing spaces are stripped.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/attr">
     * MDN web docs, attr()</a>
     */
    private boolean applyAsNumber(Consumer<CssToken> out, int line, int start, String typeOrUnit, int end, List<CssToken> tokenizedValue) {
        final ListCssTokenizer t2 = new ListCssTokenizer(tokenizedValue);
        if (t2.next() == CssTokenType.TT_EOF) {
            return false;
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
                        return false; // use fallback
                    }
                    out.accept(new CssToken(CssTokenType.TT_NUMBER, null, d, line, start, end));
                    return true;
                case CssTokenType.TT_NUMBER:
                case CssTokenType.TT_DIMENSION:
                case CssTokenType.TT_PERCENTAGE:
                    out.accept(new CssToken(CssTokenType.TT_NUMBER, null, t2.currentNumberNonNull(), line, start, end));
                    return true;
            }
        }
        return false;
    }

    /**
     * The attribute value is parsed as a CSS {@code length} dimension, that is
     * including the unit (e.g. 12.5em). If it is not valid, that is not a
     * length or out of the range accepted by the CSS property, the default
     * value is used.
     * If the given unit is a relative length, attr() computes it to an
     * absolute length. Leading and trailing spaces are stripped.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/attr">
     * MDN web docs, attr()</a>
     */
    public boolean applyAsLengthInTheGivenUnits(@NonNull Consumer<CssToken> out, int line, int start, String typeOrUnit, int end, @NonNull List<CssToken> tokenizedValue) {
        final ListCssTokenizer t2 = new ListCssTokenizer(tokenizedValue);
        if (t2.next() == CssTokenType.TT_EOF) {
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, 0, typeOrUnit));
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
                        return false;
                    }
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, typeOrUnit, d, line, start, end));
                    return true;
                case CssTokenType.TT_NUMBER:
                case CssTokenType.TT_DIMENSION:
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, typeOrUnit, t2.currentNumberNonNull(), line, start, end));
                    return true;
                case CssTokenType.TT_PERCENTAGE:
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, typeOrUnit, t2.currentNumberNonNull().doubleValue() * 100.0, line, start, end));
                    return true;
            }
        }
        return false;
    }

    /**
     * The attribute value is treated as a CSS {@code string}.
     * It is NOT reparsed, and in particular the characters are used as-is
     * instead of CSS escapes being turned into different characters.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/attr">
     * MDN web docs, attr()</a>
     */
    public boolean applyAsString(@NonNull T element, @NonNull SelectorModel<T> model, @NonNull Consumer<CssToken> out, int line, int start, QualifiedName attrName, int end) {
        String attributeAsString = model.getAttributeAsString(element, null, attrName.getNamespace(), attrName.getName());
        if (attributeAsString == null) {
            return false;
        } else {
            out.accept(new CssToken(CssTokenType.TT_STRING, attributeAsString, null, line, start, end));
            return true;
        }
    }

    @NonNull
    private QualifiedName parseAttrName(@NonNull CssTokenizer tt) throws IOException, ParseException {
        String name;
        if (tt.next() == CssTokenType.TT_IDENT) {
            name = tt.currentString();
        } else {
            throw new ParseException("attr-name expected.", tt.getStartPosition());
        }
        return new QualifiedName(null, name);// FIXME parse namespace
    }

    @Override
    public String getHelpText() {
        return getName() + "(⟨attr-name⟩ ⟨type-or-unit⟩, ⟨fallback⟩)"
                + "\n    Retrieves an attribute value by name and converts it to type-or-unit."
                + "\n    If the attribute does not exist or if the conversion fails, the fallback is used. "
                + "\n    type-or-unit must be one of 'string', 'color', 'url', 'integer', 'number', 'length' "
                + "'angle', 'time', 'frequency', '%'.";

    }
}
