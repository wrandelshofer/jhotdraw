package org.jhotdraw8.samples.modeler.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlySet;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.samples.modeler.model.MLCompartmentalizedData;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CssUmlCompartmentalizedDataConverter extends AbstractCssConverter<MLCompartmentalizedData> {

    public CssUmlCompartmentalizedDataConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public MLCompartmentalizedData parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        LinkedHashMap<String, ImmutableList<String>> map = new LinkedHashMap<>();
        tt.requireNextToken(CssTokenType.TT_LEFT_CURLY_BRACKET, "Left curly bracket expected.");
        List<String> items = new ArrayList<>();
        while (tt.next() == CssTokenType.TT_STRING || tt.current() == CssTokenType.TT_IDENT) {
            String keyword = tt.currentStringNonnull();
            tt.requireNextToken(CssTokenType.TT_COLON, "Colon expected.");
            items.clear();
            if (tt.next() == CssTokenType.TT_LEFT_SQUARE_BRACKET) {
                Items:
                while (true) {
                    switch (tt.next()) {
                        case CssTokenType.TT_STRING:
                        case CssTokenType.TT_IDENT:
                            items.add(tt.currentStringNonnull());
                            break;
                        case CssTokenType.TT_COMMA:
                            break;
                        default:
                            break Items;
                    }
                }
                tt.pushBack();
                tt.requireNextToken(CssTokenType.TT_RIGHT_SQUARE_BRACKET, "Right square bracket expected.");
            } else {
                switch (tt.current()) {
                    case CssTokenType.TT_STRING:
                    case CssTokenType.TT_IDENT:
                        items.add(tt.currentStringNonnull());
                        break;
                    default:
                        throw new ParseException("String or Identifier expected", tt.getStartPosition());
                }
            }
            map.merge(keyword, ImmutableList.ofCollection(items), ImmutableList::addAll);
            if (tt.next() != CssTokenType.TT_COMMA) {
                tt.pushBack();
            }
        }

        tt.pushBack();
        tt.requireNextToken(CssTokenType.TT_RIGHT_CURLY_BRACKET, "Right curly bracket expected.");
        return new MLCompartmentalizedData(map);
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Compartments⟩: { ⟨Compartment⟩｛,⟨Compartment⟩｝ }"
                + "\nFormat of ⟨Compartment⟩: ⟨Label⟩:[ \"⟨Item⟩\"｛,\"⟨Item⟩\"｝]"
                + "\nExample:"
                + "\n  {attributes:[\"x:double\",\"y:double\"], operations:[\"magnitude()\"]}";

    }

    @Override
    protected <TT extends MLCompartmentalizedData> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_LEFT_CURLY_BRACKET));
        ReadOnlySet<Map.Entry<String, ImmutableList<String>>> entries = value.getMap().entrySet();
        if (!entries.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_S, "\n"));
        }
        boolean firstKey = true;
        for (Map.Entry<String, ImmutableList<String>> entry : entries) {
            if (firstKey) {
                firstKey = false;
            } else {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_S, "\n"));
            }
            String keyword = entry.getKey();
            out.accept(new CssToken(CssTokenType.TT_IDENT, keyword));
            out.accept(new CssToken(CssTokenType.TT_COLON));
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_LEFT_SQUARE_BRACKET));
            boolean firstValue = true;
            for (String v : entry.getValue()) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                }
                out.accept(new CssToken(CssTokenType.TT_STRING, v));
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_SQUARE_BRACKET));
        }
        if (!firstKey) {
            out.accept(new CssToken(CssTokenType.TT_S, "\n"));
        }
        out.accept(new CssToken(CssTokenType.TT_RIGHT_CURLY_BRACKET));
    }

}
