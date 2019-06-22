package org.jhotdraw8.text;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CaseInsensitiveMappedConverter<E> implements Converter<E> {
    private final Map<String, E> fromStringMap;
    private final Map<E, String> toStringMap;

    public CaseInsensitiveMappedConverter(Map<String, E> fromStringMap) {
        this.fromStringMap = new LinkedHashMap<>();
        this.toStringMap = new LinkedHashMap<>();
        for (Map.Entry<String, E> entry : fromStringMap.entrySet()) {
            this.fromStringMap.putIfAbsent(entry.getKey().toLowerCase(), entry.getValue());
            this.toStringMap.putIfAbsent(entry.getValue(), entry.getKey());
        }
    }

    @Nullable
    @Override
    public E fromString(@Nullable CharBuffer in, @Nullable IdFactory idFactory) throws ParseException, IOException {
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

    @Nullable
    @Override
    public E getDefaultValue() {
        return null;
    }

    @Override
    public <TT extends E> void toString(Appendable out, @Nullable IdFactory idFactory, @Nullable TT value) throws IOException {
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
