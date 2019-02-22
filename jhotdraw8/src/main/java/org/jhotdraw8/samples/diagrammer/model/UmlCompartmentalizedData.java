package org.jhotdraw8.samples.diagrammer.model;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableMap;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a set of compartmentalized data.
 * <p>
 * A data compartment contains a keyword and a list of textual items.
 * <p>
 * Thus this is effectively a map of type {@literal Map<String,List<String>>>}.
 */
public class UmlCompartmentalizedData {
    @Nonnull
    private final ImmutableMap<String, ImmutableList<String>> map;

    public UmlCompartmentalizedData() {
        this.map = ImmutableMap.emptyMap();
    }

    public UmlCompartmentalizedData(ImmutableMap<String, ImmutableList<String>> map) {
        this.map = map;
    }

    public UmlCompartmentalizedData(Map<String, ? extends ImmutableList<String>> map) {
        this.map = ImmutableMap.ofMap(map);
    }

    @Nonnull
    public ImmutableMap<String, ImmutableList<String>> getMap() {
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UmlCompartmentalizedData that = (UmlCompartmentalizedData) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        return "UmlCompartmentalizedData{" +
                map +
                '}';
    }
}
