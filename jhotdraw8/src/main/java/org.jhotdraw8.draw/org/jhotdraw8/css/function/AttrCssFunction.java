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
import java.util.Collections;
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
                        @NonNull Consumer<CssToken> out) throws IOException, ParseException {
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
        functionProcessor.processToken(element, new ListCssTokenizer(
                        attrFallback.isEmpty() ? Collections.singletonList(new CssToken(CssTokenType.TT_IDENT, "none")) : attrFallback),
                out);

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
