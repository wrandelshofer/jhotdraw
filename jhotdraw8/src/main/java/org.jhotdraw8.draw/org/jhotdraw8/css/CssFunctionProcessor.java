/*
 * @(#)CssFunctionProcessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.function.CssFunction;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Interface for CSS function processors.
 * <p>
 * This is a macro processor which takes CssTokens from a CssTokenizer and produces
 * processed CssTokens.
 * <p>
 * References:
 * <dl>
 * <dt>CSS Values and Units Module, Functional Notations.</dt>
 * <dd><a href="https://www.w3.org/TR/2019/CR-css-values-3-20190606/#functional-notations">w3.org</a></dd>
 * <dt>CSS Custom Properties for Cascading Variables Module Level 1.  Using Cascading Variables: the var() notation.</dt>
 * <dd><a href="https://www.w3.org/TR/css-variables-1/#using-variables">w3.org</a></dd>
 * </dl>
 *
 * @param <T> the element type
 */
public interface CssFunctionProcessor<T> {
    /**
     * Processes all tokens.
     *
     * @param element        an element of the DOM
     * @param tt             the tokenizer providing input tokens
     * @param out            a consumer for the processed tokens
     * @param recursionStack
     * @throws IOException    in case of IO failure
     * @throws ParseException in case of a parsing failure
     */
    void process(T element, CssTokenizer tt, Consumer<CssToken> out, @NonNull Deque<CssFunction<T>> recursionStack) throws IOException, ParseException;

    /**
     * Processes the next token(s).
     */
    void processToken(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out, @NonNull Deque<CssFunction<T>> recursionStack) throws IOException, ParseException;


    /**
     * Convenience method for processing tokens.
     * <p>
     * The default implementation calls {@link #process(Object, CssTokenizer, Consumer, Deque)}.
     *
     * @param element an element of the DOM
     * @param in      the input tokens
     * @return the processed tokens
     * @throws ParseException in case of a parsing failure
     */
    @NonNull
    default ImmutableList<CssToken> process(T element, @NonNull ReadOnlyList<CssToken> in) throws ParseException {
        ListCssTokenizer tt = new ListCssTokenizer(in);
        ArrayList<CssToken> out = new ArrayList<>(in.size());
        try {
            process(element, tt, out::add, new ArrayDeque<>());
        } catch (IOException e) {
            throw new RuntimeException("unexpected io exception.", e);
        }
        return ImmutableLists.ofCollection(out);
    }

    /**
     * Returns a localized help text describing the supported functions.
     *
     * @return help text
     */
    String getHelpText();

    void setModel(SelectorModel<T> model);

    void setCustomProperties(Map<String, ImmutableList<CssToken>> customProperties);

    Map<String, ImmutableList<CssToken>> getCustomProperties();

}
