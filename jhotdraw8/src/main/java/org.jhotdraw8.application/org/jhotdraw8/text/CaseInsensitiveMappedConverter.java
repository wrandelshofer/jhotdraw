/*
 * @(#)CaseInsensitiveMappedConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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

public class CaseInsensitiveMappedConverter<E> implements Converter<E> {
    private final @NonNull Map<String, E> fromStringMap;
    private final @NonNull Map<E, String> toStringMap;

    public CaseInsensitiveMappedConverter(@NonNull Map<String, E> fromStringMap) {
        this.fromStringMap = new LinkedHashMap<>();
        this.toStringMap = new LinkedHashMap<>();
        for (Map.Entry<String, E> entry : fromStringMap.entrySet()) {
            this.fromStringMap.putIfAbsent(entry.getKey().toLowerCase(), entry.getValue());
            this.toStringMap.putIfAbsent(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public @Nullable E fromString(@NonNull CharBuffer in, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (in == null) {
            throw new ParseException("Illegal value=null", 0);
        }
        String str = in.toString();
        in.position(in.length());
        E e = fromStringMap.get(str.toLowerCase());
        if (e == null) {
            throw new ParseException("Illegal value=\"" + str + "\"", 0);
        }
        return e;
    }

    @Override
    public @Nullable E getDefaultValue() {
        return null;
    }

    @Override
    public <TT extends E> void toString(@NonNull Appendable out, @Nullable IdSupplier idSupplier, @Nullable TT value) throws IOException {
        if (value == null) {
            throw new IOException("Illegal value=null.");
        }
        String s = toStringMap.get(value);
        if (s == null) {
            throw new IOException("Illegal value=" + value);
        }
        out.append(s);
    }

}
