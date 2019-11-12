package org.jhotdraw8.css.function;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.SelectorModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Interface for CSS functions.
 */
public interface CssFunction<T> {
    /**
     * Proceses the function.
     */
    void process(@NonNull T element,
                 @NonNull CssTokenizer tt,
                 @NonNull SelectorModel<T> model,
                 @NonNull CssFunctionProcessor<T> functionProcessor,
                 @NonNull Consumer<CssToken> out) throws IOException, ParseException;


    /**
     * Gets localized help text about this function.
     */
    String getHelpText();

    /**
     * Returns the function name.
     */
    String getName();

}
