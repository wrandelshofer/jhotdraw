/*
 * @(#)PrefixMatchSelector.java
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
 * A "prefix match selector" {@code ^=} matches an element if the element has an
 * attribute with the specified name and its value starts with the specified
 * substring.
 *
 * @author Werner Randelshofer
 */
public class PrefixMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String namespace;
    @NonNull
    private final String attributeName;
    @NonNull
    private final String substring;

    public PrefixMatchSelector(@Nullable String namespace, @NonNull String attributeName, @NonNull String substring) {
        this.namespace = namespace;
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Nullable
    @Override
    protected <T> T match(@NonNull SelectorModel<T> model, @NonNull T element) {
        return (model.attributeValueStartsWith(element, namespace, attributeName, substring))//
                ? element : null;
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + attributeName + "^=" + substring + ']';
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_LEFT_SQUARE_BRACKET));
        if (namespace != null) {
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, namespace));
            consumer.accept(new CssToken(CssTokenType.TT_VERTICAL_LINE));
        }
        consumer.accept(new CssToken(CssTokenType.TT_PREFIX_MATCH));
        consumer.accept(new CssToken(CssTokenType.TT_STRING, substring));
        consumer.accept(new CssToken(CssTokenType.TT_RIGHT_SQUARE_BRACKET));
    }
}
