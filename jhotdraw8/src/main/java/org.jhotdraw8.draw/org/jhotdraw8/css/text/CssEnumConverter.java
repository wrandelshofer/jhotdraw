/*
 * @(#)CssEnumConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * CssEnumConverter. Converts all enum names to lower-case.
 *
 * @author Werner Randelshofer
 */
public class CssEnumConverter<E extends Enum<E>> implements CssConverter<E> {

    @NonNull
    private final Class<E> enumClass;
    @NonNull
    private final String name;
    private final boolean nullable;

    public CssEnumConverter(@NonNull Class<E> enumClass) {
        this(enumClass, false);
    }

    public CssEnumConverter(@NonNull Class<E> enumClass, boolean nullable) {
        this.enumClass = enumClass;
        this.name = enumClass.getName().substring(enumClass.getName().lastIndexOf('.') + 1);
        this.nullable = nullable;
    }


    @Nullable
    public E parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_IDENT) {
            throw new ParseException(name + ": identifier expected", tt.getStartPosition());
        }

        String identifier = tt.currentString();
        if (nullable && CssTokenType.IDENT_NONE.equals(identifier)) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, identifier.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new ParseException(name + ": illegal identifier:" + identifier, tt.getStartPosition());
        }
    }


    @NonNull
    @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder("Format of ⟨");
        buf.append(name).append("⟩: ");
        boolean first = true;
        if (nullable) {
            buf.append(CssTokenType.IDENT_NONE);
            first = false;
        }
        for (Field f : enumClass.getDeclaredFields()) {
            if (f.isEnumConstant()) {
                if (first) {
                    first = false;
                } else {
                    buf.append('｜');
                }
                buf.append(f.getName().toLowerCase().replace('_', '-'));
            }
        }
        return buf.toString();
    }

    @Override
    public <TT extends E> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> consumer) {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is not nullable. enum type:" + enumClass + " value:" + value);
            }
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            StringBuilder out = new StringBuilder();
            for (char ch : value.toString().toLowerCase().replace('_', '-').toCharArray()) {
                if (Character.isWhitespace(ch)) {
                    break;
                }
                out.append(ch);
            }
            consumer.accept(new CssToken(CssTokenType.TT_IDENT, out.toString()));
        }
    }

    @NonNull
    public String toString(@Nullable E value) {
        StringBuilder out = new StringBuilder();
        produceTokens(value, null, token -> out.append(token.fromToken()));
        return out.toString();
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
