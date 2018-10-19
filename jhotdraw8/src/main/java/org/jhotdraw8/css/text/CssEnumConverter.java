/* @(#)WordConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.io.IdFactory;

/**
 * CssEnumConverter. Converts all enum names to lower-case.
 * <p>
 * FIXME change this class, so that it can map internal enum names to external names using
 * a hash-map.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssEnumConverter<E extends Enum<E>> implements CssConverter<E> {

    @Nonnull
    private final Class<E> enumClass;
    @Nonnull
    private final String name;
    private final boolean nullable;

    public CssEnumConverter(Class<E> enumClass) {
        this(enumClass,false);
    }
    public CssEnumConverter(Class<E> enumClass, boolean nullable) {
        this.enumClass = enumClass;
        this.name = enumClass.getName().substring(enumClass.getName().lastIndexOf('.') + 1);
        this.nullable = nullable;
    }


    @Nullable
    public E parse(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.nextToken() != CssTokenType.TT_IDENT) {
            throw new ParseException("identifier expected", tt.getStartPosition());
        }

        String identifier = tt.currentString();
        if (nullable && CssTokenType.IDENT_NONE.equals(identifier)) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, identifier.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new ParseException("illegal identifier:" + identifier, tt.getStartPosition());
        }
    }


    @Nonnull
    @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder("Format of ⟨");
        buf.append(name).append("⟩: ");
        boolean first = true;
        if (nullable) {
            buf.append("none");
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
    public <TT extends E> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> consumer) {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is not nullable. enum type:" + enumClass + " value:" + value);
            }
            consumer.accept(new Token(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            StringBuilder out = new StringBuilder();
            for (char ch : value.toString().toLowerCase().replace('_', '-').toCharArray()) {
                if (Character.isWhitespace(ch)) {
                    break;
                }
                out.append(ch);
            }
            consumer.accept(new Token(CssTokenType.TT_IDENT, out.toString()));
        }
    }

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

}
