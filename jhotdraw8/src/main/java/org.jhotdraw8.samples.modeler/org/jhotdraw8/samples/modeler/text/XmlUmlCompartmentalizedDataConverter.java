/*
 * @(#)XmlUmlCompartmentalizedDataConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlySet;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.samples.modeler.model.MLCompartmentalizedData;

import java.util.Map;
import java.util.function.Consumer;

public class XmlUmlCompartmentalizedDataConverter extends CssUmlCompartmentalizedDataConverter {
    public XmlUmlCompartmentalizedDataConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    protected <TT extends MLCompartmentalizedData> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_LEFT_CURLY_BRACKET));
        ReadOnlySet<Map.Entry<String, ImmutableList<String>>> entries = value.getMap().entrySet();
        boolean firstKey = true;
        for (Map.Entry<String, ImmutableList<String>> entry : entries) {
            if (firstKey) {
                firstKey = false;
            } else {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
            }
            String keyword = entry.getKey();
            out.accept(new CssToken(CssTokenType.TT_IDENT, keyword));
            out.accept(new CssToken(CssTokenType.TT_COLON));
            out.accept(new CssToken(CssTokenType.TT_LEFT_SQUARE_BRACKET));
            boolean firstValue = true;
            for (String v : entry.getValue()) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                }
                out.accept(new CssToken(CssTokenType.TT_STRING, v, '\''));
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_SQUARE_BRACKET));
        }
        out.accept(new CssToken(CssTokenType.TT_RIGHT_CURLY_BRACKET));
    }

}
