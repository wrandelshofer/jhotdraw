/*
 * @(#)TypeSelector.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
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
 */
public class TypeSelector extends SimpleSelector {
    @Nullable
    private final String namespace;
    @NonNull
    private final String type;

    public TypeSelector(@Nullable String namespace, @NonNull String type) {
        this.namespace = namespace;
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "Type:"
                + (namespace == null ? "" : namespace + "|")
                + type;
    }

    @Nullable
    @Override
    public <T> T match(@NonNull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasType(element, namespace, type)) //
                ? element : null;
    }

    @Override
    public int getSpecificity() {
        return 1;
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        if (namespace != null) {
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, namespace));
            consumer.accept(new CssToken(CssTokenType.TT_VERTICAL_LINE));
        }
        consumer.accept(new CssToken(CssTokenType.TT_IDENT, type));
    }
}
