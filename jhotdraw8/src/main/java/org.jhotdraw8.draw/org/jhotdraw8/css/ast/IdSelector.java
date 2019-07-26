/*
 * @(#)IdSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
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
 * @version $Id$
 */
public class IdSelector extends SimpleSelector {

    private final String id;

    public IdSelector(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Id:" + id;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasId(element, id)) //
                ? element : null;
    }

    @Override
    public int getSpecificity() {
        return 100;
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_HASH, id));
    }
}
