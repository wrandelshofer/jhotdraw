/*
 * @(#)ExistsMatchSelector.java
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
 * An "exists match" matches an element if the element has an attribute with the
 * specified name.
 *
 * @author Werner Randelshofer
 */
public class ExistsMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String attributeNamespace;
    @Nonnull
    private final String attributeName;

    public ExistsMatchSelector(@Nullable String attributeNamespace, @Nonnull String attributeName) {
        this.attributeNamespace = attributeNamespace;
        this.attributeName = attributeName;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return model.hasAttribute(element, attributeNamespace, attributeName) ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeNamespace + ":" + attributeName + ']';
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_LEFT_SQUARE_BRACKET));
        if (attributeNamespace != null) {
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, attributeNamespace));
            consumer.accept(new CssToken(CssTokenType.TT_VERTICAL_LINE));
        }
        consumer.accept(new CssToken(CssTokenType.TT_IDENT, attributeName));
        consumer.accept(new CssToken(CssTokenType.TT_RIGHT_SQUARE_BRACKET));
    }
}
