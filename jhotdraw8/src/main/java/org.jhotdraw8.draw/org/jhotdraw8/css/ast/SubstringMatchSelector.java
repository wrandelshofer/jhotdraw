/* @(#)SubstringMatchSelector.java
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
 * A "substring match selector" {@code *=} matches an element if the element has
 * an attribute with the specified name and its value contains the specified
 * substring.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SubstringMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String namespace;
    @Nonnull
    private final String attributeName;
    @Nonnull
    private final String substring;

    public SubstringMatchSelector(@Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        this.namespace = namespace;
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return (model.attributeValueContains(element, namespace, attributeName, substring))//
                ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeName + "*=" + substring + ']';
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_LEFT_SQUARE_BRACKET));
        if (namespace != null) {
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, namespace));
            consumer.accept(new CssToken(CssTokenType.TT_VERTICAL_LINE));
        }
        consumer.accept(new CssToken(CssTokenType.TT_SUBSTRING_MATCH));
        consumer.accept(new CssToken(CssTokenType.TT_STRING, substring));
        consumer.accept(new CssToken(CssTokenType.TT_RIGHT_SQUARE_BRACKET));
    }
}
