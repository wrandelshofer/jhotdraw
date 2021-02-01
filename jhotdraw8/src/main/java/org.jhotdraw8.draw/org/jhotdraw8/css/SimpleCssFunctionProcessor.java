/*
 * @(#)SimpleCssFunctionProcessor.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Takes a list of tokens and evaluates Css functions on them.
 *
 * @param <T> the element type
 */
public class SimpleCssFunctionProcessor<T> implements CssFunctionProcessor<T> {
    protected SelectorModel<T> model;
    protected Map<String, ImmutableList<CssToken>> customProperties;
    private final Map<String, CssFunction<T>> functions;
    /** Value must be greater equal to zero. */
private int maxRecursionDepth=256;
    public SimpleCssFunctionProcessor(List<CssFunction<T>> functions) {
        this(functions, null, null);
    }

    public SimpleCssFunctionProcessor(List<CssFunction<T>> functions, SelectorModel<T> model, Map<String, ImmutableList<CssToken>> customProperties) {
        this.model = model;
        this.customProperties = customProperties;
        this.functions = new LinkedHashMap<>();
        for (CssFunction<T> function : functions) {
            this.functions.put(function.getName(), function);
        }

    }

    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public void setMaxRecursionDepth(int maxRecursionDepth) {
        if (maxRecursionDepth<0)throw new IllegalArgumentException("maxRecursionDepth="+maxRecursionDepth);
        this.maxRecursionDepth = maxRecursionDepth;
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
            process(element, tt, out::add, new ArrayDeque<>());
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
        StringBuilder buf = new StringBuilder();
        for (CssFunction<T> value : functions.values()) {
            if (buf.length() != 0) {
                buf.append("\n");
            }
            buf.append(value.getHelpText());
        }
        return buf.toString();
    }

    public final void process(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out, @NonNull Deque<CssFunction<T>> recursionStack) throws IOException, ParseException {
        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            tt.pushBack();
            processToken(element, tt, out, recursionStack);
        }
    }

    public final void processToken(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out, @NonNull Deque<CssFunction<T>> recursionStack) throws IOException, ParseException {
        doProcessToken(element, tt, out, recursionStack);
    }

    protected void doProcessToken(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out, @NonNull Deque<CssFunction<T>> recursionStack) throws IOException, ParseException {
        if (recursionStack.size()>=maxRecursionDepth) {
            throw tt.createParseException("Too many recursions. Depth="+recursionStack.size());
        }
        if (tt.nextNoSkip() == CssTokenType.TT_FUNCTION) {

            @NonNull final String name = tt.currentStringNonNull();
            final CssFunction<T> function = functions.get(name);
            if (function != null) {
                tt.pushBack();
                function.process(element, tt, model, this, out, recursionStack);
            } else {
                tt.pushBack();
                processUnknownFunction(element, tt, out, recursionStack);
            }
        } else {
            out.accept(tt.getToken());
        }
    }


    /**
     * Processes an unknown function. Unknown functions will just be passed through.
     *
     * @param element        the element
     * @param tt             the tokenizer
     * @param out            the consumer
     * @param recursionStack recursion stack for detecting infinite recursions
     * @throws IOException    on io failure
     * @throws ParseException on parse failure
     */
    private void processUnknownFunction(@NonNull T element, @NonNull CssTokenizer tt, @NonNull Consumer<CssToken> out, @NonNull Deque<CssFunction<T>> recursionStack) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈func〉: function expected.");
        out.accept(tt.getToken());
        while (tt.nextNoSkip() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            tt.pushBack();
            processToken(element, tt, out, recursionStack);
        }
        if (tt.current() != CssTokenType.TT_EOF) {
            out.accept(tt.getToken());
        }
    }

}
