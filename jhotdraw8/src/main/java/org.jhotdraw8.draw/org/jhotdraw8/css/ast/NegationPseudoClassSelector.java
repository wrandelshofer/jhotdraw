package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssParser;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.ListCssTokenizer;
import org.jhotdraw8.css.SelectorModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Implements the negation pseudo-class selector.
 * <p>
 * The negation pseudo-class, {@code :not(X)}, is a functional notation taking a
 * simple selector (excluding the negation pseudo-class itself) as an argument.
 * It represents an element that is not represented by its argument.
 * <p>
 * Negations may not be nested; {@code :not(:not(...))} is invalid.
 * Note also that since pseudo-elements are not simple selectors,
 * they are not a valid argument to {@code :not()}.
 * <p>
 * FIXME This implementation takes a selector group as its argument, and thus allows
 * to nest {@code :not()} with itself.
 * <p>
 * See <a href="https://www.w3.org/TR/selectors-3/#negation">negation pseudo-class</a>.
 */
public class NegationPseudoClassSelector extends FunctionPseudoClassSelector {

    private final Selector selector;

    public NegationPseudoClassSelector(String functionIdentifier, @Nonnull List<CssToken> terms) {
        super(functionIdentifier, terms);
        CssParser p = new CssParser();
        Selector s;
        try {
            s = p.parseSelectorGroup(new ListCssTokenizer(terms));
        } catch (IOException | ParseException e) {
            s = new UniversalSelector();
        }
        this.selector=s;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        final T match = selector.match(model, element);
        return match == null ? element : null;
    }
}
