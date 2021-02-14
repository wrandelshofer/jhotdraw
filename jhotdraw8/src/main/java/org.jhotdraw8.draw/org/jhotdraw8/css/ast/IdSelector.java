/*
 * @(#)IdSelector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.function.Consumer;

/**
 * An "id selector" matches an element if the element has an id with the
 * specified value.
 *
 * @author Werner Randelshofer
 */
public class IdSelector extends SimpleSelector {

    private final String id;

    public IdSelector(String id) {
        this.id = id;
    }

    @Override
    public @NonNull String toString() {
        return "Id:" + id;
    }

    @Override
    public @Nullable <T> T match(@NonNull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasId(element, id)) //
                ? element : null;
    }

    @Override
    public int getSpecificity() {
        return 100;
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_HASH, id));
    }
}
