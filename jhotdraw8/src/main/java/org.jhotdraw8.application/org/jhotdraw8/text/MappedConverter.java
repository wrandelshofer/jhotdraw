package org.jhotdraw8.text;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableMap;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.collection.ReadOnlyCollection;
import org.jhotdraw8.collection.ReadOnlyMap;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This converter uses a map for conversion from/to String.
 *
 * @param <T> the value type
 */
public class MappedConverter<T> implements Converter<T> {
    private final ImmutableMap<String, T> strToValue;
    private final ImmutableMap<T, String> valueToStr;

    private MappedConverter(Map<String, T> strToValue, Map<T, String> valueToStr) {
        this.strToValue = ImmutableMaps.of(strToValue);
        this.valueToStr = ImmutableMaps.of(valueToStr);
    }

    private MappedConverter(ReadOnlyMap<String, T> strToValue, ReadOnlyMap<T, String> valueToStr) {
        this.strToValue = ImmutableMaps.of(strToValue);
        this.valueToStr = ImmutableMaps.of(valueToStr);
    }

    /**
     * Creates an instance of MappedConverter from the provided String-to-Value map.
     *
     * @param strToValue the String-to-Value map
     * @param <T>        the value type
     * @return the new instance
     */
    public static <T> MappedConverter<T> ofStrToValue(Map<String, T> strToValue) {
        return new MappedConverter<>(strToValue, inverseMap(strToValue.entrySet()));
    }

    public static <T> MappedConverter<T> ofStrToValue(ReadOnlyMap<String, T> strToValue) {
        return new MappedConverter<>(strToValue, inverseMap(strToValue.entrySet()));
    }

    /**
     * Creates an instance of MappedConverter from the provided Value-to-String map.
     *
     * @param valueToStr the Value-to-String map
     * @param <T>        the value type
     * @return the new instance
     */
    public static <T> MappedConverter<T> ofValueToStr(Map<T, String> valueToStr) {
        return new MappedConverter<T>(inverseMap(valueToStr.entrySet()), valueToStr);
    }

    public static <T> MappedConverter<T> ofValueToStr(ReadOnlyMap<T, String> valueToStr) {
        return new MappedConverter<T>(inverseMap(valueToStr.entrySet()), valueToStr);
    }

    private static <K, V> Map<V, K> inverseMap(Collection<Map.Entry<K, V>> entrySet) {
        HashMap<V, K> inverse = new HashMap<>(entrySet.size());
        for (Map.Entry<K, V> entry : entrySet) {
            inverse.put(entry.getValue(), entry.getKey());
        }
        return inverse;
    }

    private static <K, V> ImmutableMap<V, K> inverseMap(ReadOnlyCollection<Map.Entry<K, V>> entrySet) {
        return ImmutableMaps.inverseOf(entrySet);
    }

    @Nullable
    @Override
    public T fromString(@Nullable CharBuffer in, @Nullable IdFactory idFactory) throws IOException {
        String str = in == null ? null : in.toString();
        if (!strToValue.containsKey(str)) {
            throw new IOException("illegal string: " + str);
        }
        if (in != null) {
            in.position(str.length());
        }

        return strToValue.get(str);
    }

    @Nullable
    @Override
    public T getDefaultValue() {
        return null;
    }

    @Override
    public <TT extends T> void toString(Appendable out, @Nullable IdFactory idFactory, @Nullable TT value) throws IOException {
        if (!valueToStr.containsKey(value)) {
            throw new IOException("unsupported value: " + value);
        }
        out.append(valueToStr.get(value));
    }
}
