/*
 * @(#)MappedConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @param <E>
 */
public class MappedConverter<E> implements Converter<E> {
    @NonNull
    private final Map<String, E> fromStringMap;
    @NonNull
    private final Map<E, String> toStringMap;
    private final String nullValue;


    public MappedConverter(@NonNull Map<String, E> fromStringMap) {
        this(fromStringMap, false);
    }

    public MappedConverter(@NonNull Map<String, E> fromStringMap, boolean nullable) {
        this(fromStringMap, nullable ? "none" : null);
    }

    public MappedConverter(@NonNull Map<String, E> fromStringMap, String nullValue) {
        this.fromStringMap = new LinkedHashMap<>();
        this.toStringMap = new LinkedHashMap<>();
        for (Map.Entry<String, E> entry : fromStringMap.entrySet()) {
            this.fromStringMap.putIfAbsent(entry.getKey(), entry.getValue());
            this.toStringMap.putIfAbsent(entry.getValue(), entry.getKey());
        }
        this.nullValue = nullValue;
    }

    @Nullable
    @Override
    public E fromString(@NonNull CharBuffer in, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (in == null) {
            throw new ParseException("Illegal value=null", 0);
        }
        String identifier = in.toString();
        in.position(in.length());
        if (nullValue != null && nullValue.equals(identifier)) {
            return null;
        }
        E e = fromStringMap.get(identifier);
        if (e == null) {
            throw new ParseException("Illegal value=\"" + identifier + "\"", 0);
        }
        return e;
    }

    @Nullable
    @Override
    public E getDefaultValue() {
        return null;
    }

    @Override
    public <TT extends E> void toString(@NonNull Appendable out, @Nullable IdSupplier idSupplier, @Nullable TT value) throws IOException {
        if (value == null) {
            if (nullValue != null) {
                throw new IOException("Illegal value=null.");
            }
            out.append(nullValue);
        }
        String s = toStringMap.get(value);
        if (s == null) {
            throw new IOException("Illegal value=" + value);
        }
        out.append(s);
    }
}
