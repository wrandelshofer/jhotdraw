/*
 * @(#)ClassSelector.java
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
 * A "class selector" matches an element if the element has a style class with
 * the specified value.
 *
 * @author Werner Randelshofer
 */
public class ClassSelector extends SimpleSelector {

    private final String clazz;

    public ClassSelector(String clazz) {
        this.clazz = clazz;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Class:" + clazz;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasStyleClass(element, clazz)) //
                ? element : null;
    }

    @Override
    public int getSpecificity() {
        return 10;
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_POINT));
        consumer.accept(new CssToken(CssTokenType.TT_IDENT, clazz));
    }
}
