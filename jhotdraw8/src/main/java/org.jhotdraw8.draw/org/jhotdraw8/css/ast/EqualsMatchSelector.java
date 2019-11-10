/*
 * @(#)EqualsMatchSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.function.Consumer;

/**
 * An "attribute value selector" matches an element if the element has an
 * attribute with the specified name and value.
 *
 * @author Werner Randelshofer
 */
public class EqualsMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String namespace;
    @NonNull
    private final String attributeName;
    @NonNull
    private final String attributeValue;

    public EqualsMatchSelector(@Nullable String namespace, @NonNull String attributeName, @NonNull String attributeValue) {
        this.namespace = namespace;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Nullable
    @Override
    protected <T> T match(@NonNull SelectorModel<T> model, @NonNull T element) {
        return model.attributeValueEquals(element, namespace, attributeName, attributeValue) ? element : null;
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + attributeName + "=" + attributeValue + ']';
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_LEFT_SQUARE_BRACKET));
        if (namespace != null) {
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, namespace));
            consumer.accept(new CssToken(CssTokenType.TT_VERTICAL_LINE));
        }
        consumer.accept(new CssToken(CssTokenType.TT_IDENT, attributeName));
        consumer.accept(new CssToken(CssTokenType.TT_EQUALS));
        consumer.accept(new CssToken(CssTokenType.TT_STRING, attributeValue));
        consumer.accept(new CssToken(CssTokenType.TT_RIGHT_SQUARE_BRACKET));
    }

}
