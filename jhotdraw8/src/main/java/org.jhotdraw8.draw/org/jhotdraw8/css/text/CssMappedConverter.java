/*
 * @(#)CssMappedConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CssMappedConverter<E> implements CssConverter<E> {

    private final Map<String, E> fromStringMap;
    private final Map<E, String> toStringMap;
    private final boolean nullable;
    private final String name;


    public CssMappedConverter(String name, Map<String, E> fromStringMap) {
        this(name, fromStringMap, false);
    }

    public CssMappedConverter(String name, Map<String, E> fromStringMap, boolean nullable) {
        this.fromStringMap = new LinkedHashMap<>();
        this.toStringMap = new LinkedHashMap<>();
        for (Map.Entry<String, E> entry : fromStringMap.entrySet()) {
            this.fromStringMap.putIfAbsent(entry.getKey(), entry.getValue());
            this.toStringMap.putIfAbsent(entry.getValue(), entry.getKey());
        }
        this.name = name;
        this.nullable = nullable;
    }

    @Nullable
    public E parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_IDENT) {
            throw new ParseException("identifier expected", tt.getStartPosition());
        }

        String identifier = tt.currentString();
        if (nullable && CssTokenType.IDENT_NONE.equals(identifier)) {
            return null;
        }
        E e = fromStringMap.get(identifier);
        if (e == null) {
            throw new ParseException("Illegal value=\"" + identifier + "\"", 0);
        }
        return e;
    }


    @Nonnull
    @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder("Format of ⟨");
        buf.append(name).append("⟩: ");
        boolean first = true;
        if (nullable) {
            buf.append(CssTokenType.IDENT_NONE);
            first = false;
        }
        for (String f : toStringMap.values()) {
            if (first) {
                first = false;
            } else {
                buf.append('｜');
            }
            buf.append(f);
        }

        return buf.toString();
    }

    @Override
    public <TT extends E> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> consumer) {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("Value is not nullable.");
            }
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            String s = toStringMap.get(value);
            if (s == null) {
                throw new IllegalArgumentException("Unsupported value: " + value);
            }
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, s));
        }
    }


    @Nullable
    @Override
    public E getDefaultValue() {
        return null;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

}
