/*
 * @(#)TypeSelector.java
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
 * A "class selector" matches an element if the element has a type with the
 * specified value.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TypeSelector extends SimpleSelector {
    @Nullable
    private final String namespace;
    @Nonnull
    private final String type;

    public TypeSelector(@Nullable String namespace, @Nonnull String type) {
        this.namespace = namespace;
        this.type = type;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Type:" + type;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasType(element, namespace, type)) //
                ? element : null;
    }

    @Override
    public int getSpecificity() {
        return 1;
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        if (namespace != null) {
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, namespace));
            consumer.accept(new CssToken(CssTokenType.TT_VERTICAL_LINE));
        }
        consumer.accept(new CssToken(CssTokenType.TT_IDENT, type));
    }
}
