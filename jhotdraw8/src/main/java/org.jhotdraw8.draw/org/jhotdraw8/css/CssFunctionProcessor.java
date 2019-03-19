package org.jhotdraw8.css;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Interface for CSS function processors.
 * <p>
 * This is a macro processor which takes CssTokens from a CssTokenizer and produces
 * processed tokens.
 *
 * @param <T> the element type
 */
public interface CssFunctionProcessor<T> {
    /**
     * Processes tokens.
     *
     * @param element an element of the DOM
     * @param tt      the tokenizer providing input tokens
     * @param out     a consumer for the processed tokens
     * @throws IOException    in case of IO failure
     * @throws ParseException in case of a parsing failure
     */
    void process(T element, CssTokenizer tt, Consumer<CssToken> out) throws IOException, ParseException;

    /**
     * Convenience method for processing tokens.
     * <p>
     * The default implementation calls {@link #process(Object, CssTokenizer, Consumer)}.
     *
     * @param element an element of the DOM
     * @param in      the input tokens
     * @return the processed tokens
     * @throws ParseException in case of a parsing failure
     */
    default ImmutableList<CssToken> process(T element, ReadOnlyList<CssToken> in) throws ParseException {
        ListCssTokenizer tt = new ListCssTokenizer(in);
        ArrayList<CssToken> out = new ArrayList<>(in.size());
        try {
            process(element, tt, out::add);
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

}
